package com.stockflow.copilot.services

import com.stockflow.copilot.models.*
import org.springframework.stereotype.Service

@Service
class CopilotOrchestratorService(
    private val geminiApiService: GeminiApiService
) {

    private val systemInstruction = """
        You are StockFlow Copilot, an inventory decision-support assistant.
        Use only the supplied StockFlow tools for tenant-specific facts and numbers.
        Never invent inventory, demand, forecast, cost, route or carbon values.
        If a tool returns no data, state clearly that the requested data is unavailable.
        Distinguish confirmed facts, model forecasts, and recommendations.
        State forecast confidence when discussing future demand.
        Never claim that a purchase order or transfer was executed.
        Keep answers concise and clear.
    """.trimIndent()

    fun handleChat(tenantId: String, request: ChatRequest): ChatResponse {
        val toolsUsed = mutableSetOf<String>()
        val evidenceList = mutableListOf<Evidence>()
        val warnings = mutableListOf<String>()

        val userPrompt = """
            User Message: ${request.message}
            UI Context: Workspace=${request.currentWorkspace ?: "N/A"}, Warehouse=${request.selectedWarehouseId ?: "N/A"}, SKU=${request.selectedSkuId ?: "N/A"}
        """.trimIndent()

        val conversationHistory = mutableListOf(
            GeminiContent(role = "user", parts = listOf(GeminiPart(text = userPrompt)))
        )

        var finalAnswer = "Unable to process inventory request."
        var rounds = 0
        val maxRounds = 3

        while (rounds < maxRounds) {
            rounds++
            val response = geminiApiService.callGemini(conversationHistory, systemInstruction)
            val candidateContent = response?.candidates?.firstOrNull()?.content ?: break

            // Append model response to conversation history
            conversationHistory.add(candidateContent)

            val part = candidateContent.parts.firstOrNull() ?: break

            // Case A: Model requested a function/tool call
            if (part.functionCall != null) {
                val functionCall = part.functionCall
                val toolName = functionCall.name
                toolsUsed.add(toolName)

                // Execute tool with Tenant ID enforcement
                val (toolResult, evidence) = executeTool(tenantId, toolName, functionCall.args)
                if (evidence != null) evidenceList.add(evidence)

                // Send tool execution output back to Gemini
                conversationHistory.add(
                    GeminiContent(
                        role = "user",
                        parts = listOf(
                            GeminiPart(
                                functionResponse = GeminiFunctionResponse(
                                    name = toolName,
                                    response = mapOf("result" to toolResult)
                                )
                            )
                        )
                    )
                )
            } 
            // Case B: Model returned standard grounded text explanation
            else if (part.text != null) {
                finalAnswer = part.text
                break
            }
        }

        if (toolsUsed.contains("get_latest_forecast")) {
            warnings.add("Forecasts are advisory and should be reviewed by a planner.")
        }

        return ChatResponse(
            answer = finalAnswer,
            confidence = "HIGH",
            toolsUsed = toolsUsed.toList(),
            evidence = evidenceList,
            suggestedActions = listOf(
                SuggestedAction("OPEN_TRANSFER_RECOMMENDATION", "Review Transfer Proposals", requiresApproval = false)
            ),
            warnings = warnings
        )
    }

    /**
     * Internal Tool Routing Method (Team lead will replace mock returns with real DB/REST queries)
     */
    private fun executeTool(tenantId: String, toolName: String, args: Map<String, Any?>): Pair<Any, Evidence?> {
        return when (toolName) {
            "get_inventory_risks" -> {
                val warehouse = args["warehouseId"] ?: "ALL"
                val sku = args["skuId"] ?: "ALL"
                val mockResult = mapOf(
                    "tenantId" to tenantId,
                    "warehouseId" to warehouse,
                    "skuId" to sku,
                    "riskType" to "SAFETY_STOCK_BREACH",
                    "severity" to "HIGH",
                    "projectedStockoutDays" to 2.1,
                    "currentStock" to 140,
                    "safetyStockThreshold" to 300
                )
                Pair(mockResult, Evidence("INVENTORY_RISK", "RISK-8821", "Safety-stock breach"))
            }
            "get_latest_forecast" -> {
                val mockResult = mapOf(
                    "tenantId" to tenantId,
                    "warehouseId" to args["warehouseId"],
                    "skuId" to args["skuId"],
                    "forecastedDailyDemand" to 75,
                    "confidenceScore" to "0.88",
                    "modelUsed" to "Prophet-v2"
                )
                Pair(mockResult, Evidence("FORECAST", "FC-9910", "Prophet-v2 Model"))
            }
            else -> Pair(mapOf("error" to "Tool not implemented"), null)
        }
    }
}
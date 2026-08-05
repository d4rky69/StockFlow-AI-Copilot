package com.stockflow.copilot.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.validation.constraints.NotBlank

// --- API Request & Response Contracts ---

data class ChatRequest(
    @field:NotBlank val conversationId: String,
    @field:NotBlank val message: String,
    val currentWorkspace: String? = null,
    val selectedWarehouseId: String? = null,
    val selectedSkuId: String? = null
)

data class ChatResponse(
    val answer: String,
    val answerType: String = "GROUNDED_EXPLANATION",
    val confidence: String = "MEDIUM",
    val toolsUsed: List<String>,
    val evidence: List<Evidence>,
    val suggestedActions: List<SuggestedAction>,
    val warnings: List<String>
)

data class Evidence(
    val type: String,
    val id: String,
    val label: String
)

data class SuggestedAction(
    val type: String,
    val label: String,
    val requiresApproval: Boolean = false
)

// --- Gemini REST API Payloads ---

@JsonInclude(JsonInclude.Include.NON_NULL)
data class GeminiRequestBody(
    val contents: List<GeminiContent>,
    val systemInstruction: GeminiSystemInstruction? = null,
    val tools: List<GeminiToolDeclaration>? = null
)

data class GeminiSystemInstruction(
    val parts: List<Map<String, String>>
)

data class GeminiContent(
    val role: String,
    val parts: List<GeminiPart>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class GeminiPart(
    val text: String? = null,
    val functionCall: GeminiFunctionCall? = null,
    val functionResponse: GeminiFunctionResponse? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GeminiFunctionCall(
    val name: String,
    val args: Map<String, Any?> = emptyMap()
)

data class GeminiFunctionResponse(
    val name: String,
    val response: Map<String, Any?>
)

data class GeminiToolDeclaration(
    val functionDeclarations: List<Map<String, Any>>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GeminiResponseBody(
    val candidates: List<GeminiCandidate>? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GeminiCandidate(
    val content: GeminiContent? = null,
    val finishReason: String? = null
)
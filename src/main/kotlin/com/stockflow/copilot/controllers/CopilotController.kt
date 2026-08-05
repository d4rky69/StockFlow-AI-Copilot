package com.stockflow.copilot.controllers

import com.stockflow.copilot.models.ChatRequest
import com.stockflow.copilot.models.ChatResponse
import com.stockflow.copilot.services.CopilotOrchestratorService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/copilot")
class CopilotController(
    private val orchestratorService: CopilotOrchestratorService
) {

    @PostMapping("/chat")
    fun chat(
        @RequestHeader("X-Tenant-ID") tenantId: String,
        @Valid @RequestBody request: ChatRequest
    ): ResponseEntity<ChatResponse> {
        val response = orchestratorService.handleChat(tenantId, request)
        return ResponseEntity.ok(response)
    }
}
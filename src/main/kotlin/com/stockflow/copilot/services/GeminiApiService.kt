package com.stockflow.copilot.services

import com.stockflow.copilot.models.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class GeminiApiService(
    private val webClientBuilder: WebClient.Builder
) {
    @Value("\${gemini.api.key}")
    private lateinit var apiKey: String

    private val geminiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent"

    fun callGemini(contents: List<GeminiContent>, systemInstructionText: String): GeminiResponseBody? {
        val requestBody = GeminiRequestBody(
            contents = contents,
            systemInstruction = GeminiSystemInstruction(
                parts = listOf(mapOf("text" to systemInstructionText))
            ),
            tools = listOf(GeminiTools.declaration)
        )

        return webClientBuilder.build()
            .post()
            .uri("$geminiUrl?key=$apiKey")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(GeminiResponseBody::class.java)
            .block()
    }
}
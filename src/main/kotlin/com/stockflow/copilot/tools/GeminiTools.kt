package com.stockflow.copilot.tools

import com.stockflow.copilot.models.GeminiToolDeclaration

object GeminiTools {
    val declaration = GeminiToolDeclaration(
        functionDeclarations = listOf(
            mapOf(
                "name" to "get_inventory_risks",
                "description" to "Retrieve stockout, safety-stock, expiry, excess and data-gap risks.",
                "parameters" to mapOf(
                    "type" to "OBJECT",
                    "properties" to mapOf(
                        "warehouseId" to mapOf("type" to "STRING", "description" to "Target warehouse ID"),
                        "skuId" to mapOf("type" to "STRING", "description" to "Target SKU ID"),
                        "riskType" to mapOf("type" to "STRING"),
                        "severity" to mapOf("type" to "STRING"),
                        "limit" to mapOf("type" to "INTEGER")
                    )
                )
            ),
            mapOf(
                "name" to "get_latest_forecast",
                "description" to "Retrieve demand forecast, confidence, selected model, and projected stockout date.",
                "parameters" to mapOf(
                    "type" to "OBJECT",
                    "properties" to mapOf(
                        "warehouseId" to mapOf("type" to "STRING", "description" to "Warehouse ID"),
                        "skuId" to mapOf("type" to "STRING", "description" to "SKU ID"),
                        "horizonDays" to mapOf("type" to "INTEGER", "description" to "Forecast horizon in days")
                    ),
                    "required" to listOf("warehouseId", "skuId")
                )
            ),
            mapOf(
                "name" to "get_expiring_batches",
                "description" to "Retrieve expiry-sensitive inventory batches within a given day threshold.",
                "parameters" to mapOf(
                    "type" to "OBJECT",
                    "properties" to mapOf(
                        "warehouseId" to mapOf("type" to "STRING"),
                        "skuId" to mapOf("type" to "STRING"),
                        "withinDays" to mapOf("type" to "INTEGER", "description" to "Days until expiration")
                    )
                )
            )
        )
    )
}
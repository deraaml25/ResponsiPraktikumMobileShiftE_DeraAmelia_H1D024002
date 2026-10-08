package com.pemmob.cicipdiary.util

import com.pemmob.cicipdiary.data.model.Ingredient
import com.pemmob.cicipdiary.data.model.Meal

fun Meal.toIngredientList(): List<Ingredient> {
    val ingredients = mutableListOf<Ingredient>()
    val fields = this::class.java.declaredFields

    for (i in 1..20) {
        try {
            val ingredientField = fields.find { it.name == "strIngredient$i" }
            val measureField = fields.find { it.name == "strMeasure$i" }
            
            ingredientField?.isAccessible = true
            measureField?.isAccessible = true
            
            val ingredientName = ingredientField?.get(this) as? String
            val measure = measureField?.get(this) as? String
            
            if (!ingredientName.isNullOrBlank()) {
                ingredients.add(Ingredient(ingredientName.trim(), measure?.trim().orDash()))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return ingredients
}

fun String?.orDash(): String {
    return if (this.isNullOrBlank()) "-" else this
}

fun Meal.toInstructionSteps(): List<String> {
    val raw = strInstructions?.trim() ?: return emptyList()
    if (raw.isBlank()) return emptyList()

    val stepHeaderRegex = Regex("""^step\s*\d+[:.\-\s]*$""", RegexOption.IGNORE_CASE)
    val prefixRegex = Regex("""^(?:step\s*\d+[:.\-\s]*|\d+[\.\)]\s*)""", RegexOption.IGNORE_CASE)

    val lines = raw.replace("\r\n", "\n").replace("\r", "\n").split("\n")
    val steps = mutableListOf<String>()

    for (line in lines) {
        val trimmed = line.trim()
        if (trimmed.isEmpty()) continue

        if (stepHeaderRegex.matches(trimmed)) continue

        val cleaned = trimmed.replace(prefixRegex, "").trim()
        if (cleaned.isNotEmpty()) {
            steps.add(cleaned)
        }
    }

    if (steps.size == 1) {
        val inline = steps.first().split(Regex("""(?<=\S)\s+(?=(?:step\s*\d+|\d+[\.\)]))\s*""", RegexOption.IGNORE_CASE))
        if (inline.size > 1) {
            val parsedInline = inline.map { it.replace(prefixRegex, "").trim() }.filter { it.isNotEmpty() }
            if (parsedInline.isNotEmpty()) {
                return parsedInline
            }
        }
    }

    return steps.ifEmpty { listOf(raw) }
}


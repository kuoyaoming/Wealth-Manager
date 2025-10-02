package com.wealthmanager.security

import com.wealthmanager.debug.DebugLogManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeyValidator @Inject constructor(
    private val debugLogManager: DebugLogManager
) {

    companion object {
        private const val MIN_KEY_LENGTH = 16
        private const val MAX_KEY_LENGTH = 128
        private const val MIN_ENTROPY_SCORE = 3.0

        private val WEAK_PATTERNS = listOf(
            "test", "demo", "sample", "example", "default",
            "123456", "password", "admin", "user", "key",
            "api", "secret", "token", "auth"
        )

        private val REPEAT_PATTERNS = listOf(
            "111111", "222222", "333333", "444444", "555555",
            "aaaaaa", "bbbbbb", "cccccc", "dddddd", "eeeeee"
        )
    }

    /**
     * Validates API key strength.
     */
    fun validateApiKey(key: String, keyType: String): KeyValidationResult {
        debugLogManager.log("KEY_VALIDATOR", "Validating $keyType key")

        val issues = mutableListOf<String>()
        var score = 0.0

        if (key.length < MIN_KEY_LENGTH) {
            issues.add("Key too short (minimum $MIN_KEY_LENGTH characters)")
        } else if (key.length > MAX_KEY_LENGTH) {
            issues.add("Key too long (maximum $MAX_KEY_LENGTH characters)")
        } else {
            score += 1.0
        }

        val diversityScore = calculateCharacterDiversity(key)
        if (diversityScore < MIN_ENTROPY_SCORE) {
            issues.add("Key lacks character diversity (entropy: $diversityScore)")
        } else {
            score += 1.0
        }

        if (containsWeakPatterns(key)) {
            issues.add("Key contains weak patterns")
        } else {
            score += 1.0
        }

        if (containsRepeatPatterns(key)) {
            issues.add("Key contains repetitive patterns")
        } else {
            score += 1.0
        }

        val formatIssues = validateKeyFormat(key, keyType)
        issues.addAll(formatIssues)
        if (formatIssues.isEmpty()) {
            score += 1.0
        }

        val isValid = issues.isEmpty()
        val strength = when {
            score >= 4.5 -> KeyStrength.STRONG
            score >= 3.0 -> KeyStrength.MEDIUM
            else -> KeyStrength.WEAK
        }

        debugLogManager.log("KEY_VALIDATOR", "Validation result: $strength (score: $score)")

        return KeyValidationResult(
            isValid = isValid,
            strength = strength,
            issues = issues,
            score = score
        )
    }

    /**
     * Calculates character diversity score.
     */
    private fun calculateCharacterDiversity(key: String): Double {
        val charCounts = key.groupingBy { it }.eachCount()
        val uniqueChars = charCounts.size
        val totalChars = key.length

        var entropy = 0.0
        charCounts.values.forEach { count ->
            val probability = count.toDouble() / totalChars
            entropy -= probability * kotlin.math.log2(probability)
        }

        return entropy
    }

    /**
     * Checks for weak patterns.
     */
    private fun containsWeakPatterns(key: String): Boolean {
        val lowerKey = key.lowercase()
        return WEAK_PATTERNS.any { pattern ->
            lowerKey.contains(pattern)
        }
    }

    /**
     * Checks for repetitive patterns.
     */
    private fun containsRepeatPatterns(key: String): Boolean {
        return REPEAT_PATTERNS.any { pattern ->
            key.contains(pattern)
        }
    }

    /**
     * Validates key format based on key type.
     */
    private fun validateKeyFormat(key: String, keyType: String): List<String> {
        val issues = mutableListOf<String>()

        when (keyType.lowercase()) {
            "finnhub" -> {
                if (!key.matches(Regex("^[a-zA-Z0-9]{40}$"))) {
                    issues.add("Finnhub key should be 40-character alphanumeric string")
                }
            }
            "exchange" -> {
                if (!key.matches(Regex("^[a-zA-Z0-9]{16,64}$"))) {
                    issues.add("Exchange Rate API key should be 16-64 character alphanumeric string")
                }
            }
            "alpha_vantage" -> {
                if (!key.matches(Regex("^[a-zA-Z0-9]{16}$"))) {
                    issues.add("Alpha Vantage key should be 16-character alphanumeric string")
                }
            }
        }

        return issues
    }

    /**
     * Generates key strength suggestions.
     */
    fun generateKeyStrengthSuggestions(result: KeyValidationResult): List<String> {
        val suggestions = mutableListOf<String>()

        when (result.strength) {
            KeyStrength.WEAK -> {
                suggestions.add("Consider generating a new key with higher entropy")
                suggestions.add("Use a mix of uppercase, lowercase, numbers, and symbols")
                suggestions.add("Avoid common words and patterns")
            }
            KeyStrength.MEDIUM -> {
                suggestions.add("Key is acceptable but could be stronger")
                suggestions.add("Consider adding more character diversity")
            }
            KeyStrength.STRONG -> {
                suggestions.add("Key strength is excellent")
            }
        }

        if (result.issues.isNotEmpty()) {
            suggestions.add("Address the following issues:")
            result.issues.forEach { issue ->
                suggestions.add("• $issue")
            }
        }

        return suggestions
    }
}

/**
 * Key validation result.
 */
data class KeyValidationResult(
    val isValid: Boolean,
    val strength: KeyStrength,
    val issues: List<String>,
    val score: Double
)

/**
 * Key strength levels.
 */
enum class KeyStrength {
    WEAK, MEDIUM, STRONG
}


package com.amy.daily5vocab.data.words

/**
 * Local source of vocabulary words grouped by topic. The Today screen draws a random
 * sample from a topic's pool for the user's daily set.
 *
 * Topic keys match the options offered on the onboarding screen.
 */
object WordBank {

    val topicWords: Map<String, List<String>> = mapOf(
        "Technology" to listOf(
            "Algorithm", "Bandwidth", "Encryption", "Latency", "Protocol",
            "Scalable", "Interface", "Firmware", "Cache", "Runtime",
            "Throughput", "Redundancy",
        ),
        "Business" to listOf(
            "Resilient", "Ephemeral", "Pragmatic", "Leverage", "Synergy",
            "Acumen", "Liquidity", "Margin", "Stakeholder", "Diversify",
            "Incentive", "Forecast",
        ),
        "Travel" to listOf(
            "Itinerary", "Excursion", "Voyage", "Layover", "Expedition",
            "Nomadic", "Sojourn", "Wanderlust", "Terminal", "Customs",
            "Embark", "Detour",
        ),
        "Cooking" to listOf(
            "Marinate", "Caramelize", "Blanch", "Garnish", "Simmer",
            "Knead", "Saute", "Render", "Emulsify", "Braise",
            "Zest", "Deglaze",
        ),
        "Art" to listOf(
            "Chiaroscuro", "Composition", "Palette", "Texture", "Abstract",
            "Perspective", "Contour", "Hue", "Silhouette", "Medium",
            "Tessellation", "Gradient",
        ),
        "Science" to listOf(
            "Hypothesis", "Catalyst", "Entropy", "Photosynthesis", "Velocity",
            "Molecule", "Inertia", "Genome", "Quantum", "Osmosis",
            "Isotope", "Equilibrium",
        ),
    )

    /** Topic used when a user somehow has no saved topic yet. */
    const val DEFAULT_TOPIC = "Business"

    /** Returns up to [count] randomly chosen words for [topic]. */
    fun pickWords(topic: String, count: Int = 5): List<String> {
        val pool = topicWords[topic] ?: topicWords.values.flatten()
        return pool.shuffled().take(count)
    }
}

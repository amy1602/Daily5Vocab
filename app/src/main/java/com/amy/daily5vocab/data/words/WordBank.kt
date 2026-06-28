package com.amy.daily5vocab.data.words

/** A vocabulary entry: the English [term] and its Vietnamese [meaning]. */
data class VocabWord(val term: String, val meaning: String)

/**
 * Local source of vocabulary words grouped by topic. The Today screen draws a random
 * sample from a topic's pool for the user's daily set, and the daily reminder shows the
 * term/meaning pairs.
 *
 * Topic keys match the options offered on the onboarding and change-topic screens.
 */
object WordBank {

    val topicWords: Map<String, List<VocabWord>> = mapOf(
        "Technology" to listOf(
            VocabWord("Algorithm", "Thuật toán"),
            VocabWord("Bandwidth", "Băng thông"),
            VocabWord("Encryption", "Mã hóa"),
            VocabWord("Latency", "Độ trễ"),
            VocabWord("Protocol", "Giao thức"),
            VocabWord("Scalable", "Có khả năng mở rộng"),
            VocabWord("Interface", "Giao diện"),
            VocabWord("Firmware", "Phần sụn"),
            VocabWord("Cache", "Bộ nhớ đệm"),
            VocabWord("Runtime", "Thời gian chạy"),
            VocabWord("Throughput", "Thông lượng"),
            VocabWord("Redundancy", "Sự dư thừa"),
        ),
        "Business" to listOf(
            VocabWord("Resilient", "Kiên cường"),
            VocabWord("Ephemeral", "Chóng tàn"),
            VocabWord("Pragmatic", "Thực tế"),
            VocabWord("Leverage", "Tận dụng"),
            VocabWord("Synergy", "Sự cộng hưởng"),
            VocabWord("Acumen", "Sự nhạy bén"),
            VocabWord("Liquidity", "Tính thanh khoản"),
            VocabWord("Margin", "Biên lợi nhuận"),
            VocabWord("Stakeholder", "Bên liên quan"),
            VocabWord("Diversify", "Đa dạng hóa"),
            VocabWord("Incentive", "Sự khích lệ"),
            VocabWord("Forecast", "Dự báo"),
        ),
        "Travel" to listOf(
            VocabWord("Itinerary", "Lịch trình"),
            VocabWord("Excursion", "Chuyến tham quan"),
            VocabWord("Voyage", "Chuyến hải trình"),
            VocabWord("Layover", "Điểm dừng quá cảnh"),
            VocabWord("Expedition", "Cuộc thám hiểm"),
            VocabWord("Nomadic", "Du mục"),
            VocabWord("Sojourn", "Kỳ lưu trú"),
            VocabWord("Wanderlust", "Đam mê xê dịch"),
            VocabWord("Terminal", "Nhà ga"),
            VocabWord("Customs", "Hải quan"),
            VocabWord("Embark", "Khởi hành"),
            VocabWord("Detour", "Đường vòng"),
        ),
        "Cooking" to listOf(
            VocabWord("Marinate", "Ướp"),
            VocabWord("Caramelize", "Thắng caramel"),
            VocabWord("Blanch", "Chần"),
            VocabWord("Garnish", "Trang trí món ăn"),
            VocabWord("Simmer", "Ninh nhỏ lửa"),
            VocabWord("Knead", "Nhào bột"),
            VocabWord("Saute", "Xào áp chảo"),
            VocabWord("Render", "Thắng mỡ"),
            VocabWord("Emulsify", "Tạo nhũ tương"),
            VocabWord("Braise", "Om"),
            VocabWord("Zest", "Vỏ bào cam chanh"),
            VocabWord("Deglaze", "Khử cặn chảo"),
        ),
        "Art" to listOf(
            VocabWord("Chiaroscuro", "Tương phản sáng tối"),
            VocabWord("Composition", "Bố cục"),
            VocabWord("Palette", "Bảng màu"),
            VocabWord("Texture", "Kết cấu"),
            VocabWord("Abstract", "Trừu tượng"),
            VocabWord("Perspective", "Phối cảnh"),
            VocabWord("Contour", "Đường viền"),
            VocabWord("Hue", "Sắc màu"),
            VocabWord("Silhouette", "Hình bóng"),
            VocabWord("Medium", "Chất liệu"),
            VocabWord("Tessellation", "Hoa văn lát kín"),
            VocabWord("Gradient", "Chuyển sắc"),
        ),
        "Science" to listOf(
            VocabWord("Hypothesis", "Giả thuyết"),
            VocabWord("Catalyst", "Chất xúc tác"),
            VocabWord("Entropy", "Độ hỗn loạn"),
            VocabWord("Photosynthesis", "Quang hợp"),
            VocabWord("Velocity", "Vận tốc"),
            VocabWord("Molecule", "Phân tử"),
            VocabWord("Inertia", "Quán tính"),
            VocabWord("Genome", "Bộ gen"),
            VocabWord("Quantum", "Lượng tử"),
            VocabWord("Osmosis", "Sự thẩm thấu"),
            VocabWord("Isotope", "Đồng vị"),
            VocabWord("Equilibrium", "Trạng thái cân bằng"),
        ),
        "Literature" to listOf(
            VocabWord("Metaphor", "Ẩn dụ"),
            VocabWord("Allegory", "Truyện ngụ ngôn"),
            VocabWord("Prose", "Văn xuôi"),
            VocabWord("Protagonist", "Nhân vật chính"),
            VocabWord("Narrative", "Lối kể chuyện"),
            VocabWord("Foreshadow", "Báo trước"),
            VocabWord("Verse", "Câu thơ"),
            VocabWord("Soliloquy", "Độc thoại"),
            VocabWord("Motif", "Mô típ"),
            VocabWord("Satire", "Châm biếm"),
            VocabWord("Imagery", "Hình ảnh ẩn dụ"),
            VocabWord("Anthology", "Tuyển tập"),
        ),
        "Psychology" to listOf(
            VocabWord("Cognition", "Nhận thức"),
            VocabWord("Empathy", "Sự đồng cảm"),
            VocabWord("Resilience", "Khả năng phục hồi"),
            VocabWord("Perception", "Tri giác"),
            VocabWord("Bias", "Thiên kiến"),
            VocabWord("Catharsis", "Sự thanh lọc cảm xúc"),
            VocabWord("Conditioning", "Điều kiện hóa"),
            VocabWord("Introspection", "Sự nội quan"),
            VocabWord("Motivation", "Động lực"),
            VocabWord("Stimulus", "Tác nhân kích thích"),
            VocabWord("Disposition", "Khuynh hướng"),
            VocabWord("Reinforce", "Củng cố"),
        ),
    )

    /** Topic used when a user somehow has no saved topic yet. */
    const val DEFAULT_TOPIC = "Business"

    /** Returns up to [count] randomly chosen words for [topic]. */
    fun pickWords(topic: String, count: Int = 5): List<VocabWord> {
        val pool = topicWords[topic] ?: topicWords.values.flatten()
        return pool.shuffled().take(count)
    }
}

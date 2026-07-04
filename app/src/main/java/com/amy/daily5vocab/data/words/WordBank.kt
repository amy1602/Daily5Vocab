package com.amy.daily5vocab.data.words

/**
 * A vocabulary entry: the English [term], its Vietnamese [meaning], and the IPA
 * [phonetic] transcription shown on the word detail screen.
 */
data class VocabWord(val term: String, val meaning: String, val phonetic: String = "")

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
            VocabWord("Algorithm", "Thuật toán", "/ˈæl.ɡə.rɪ.ðəm/"),
            VocabWord("Bandwidth", "Băng thông", "/ˈbænd.wɪdθ/"),
            VocabWord("Encryption", "Mã hóa", "/ɪnˈkrɪp.ʃən/"),
            VocabWord("Latency", "Độ trễ", "/ˈleɪ.tən.si/"),
            VocabWord("Protocol", "Giao thức", "/ˈprəʊ.tə.kɒl/"),
            VocabWord("Scalable", "Có khả năng mở rộng", "/ˈskeɪ.lə.bəl/"),
            VocabWord("Interface", "Giao diện", "/ˈɪn.tə.feɪs/"),
            VocabWord("Firmware", "Phần sụn", "/ˈfɜːm.weə/"),
            VocabWord("Cache", "Bộ nhớ đệm", "/kæʃ/"),
            VocabWord("Runtime", "Thời gian chạy", "/ˈrʌn.taɪm/"),
            VocabWord("Throughput", "Thông lượng", "/ˈθruː.pʊt/"),
            VocabWord("Redundancy", "Sự dư thừa", "/rɪˈdʌn.dən.si/"),
        ),
        "Business" to listOf(
            VocabWord("Resilient", "Kiên cường", "/rɪˈzɪl.i.ənt/"),
            VocabWord("Ephemeral", "Chóng tàn", "/ɪˈfem.ər.əl/"),
            VocabWord("Pragmatic", "Thực tế", "/præɡˈmæt.ɪk/"),
            VocabWord("Leverage", "Tận dụng", "/ˈliː.vər.ɪdʒ/"),
            VocabWord("Synergy", "Sự cộng hưởng", "/ˈsɪn.ə.dʒi/"),
            VocabWord("Acumen", "Sự nhạy bén", "/ˈæk.jʊ.mən/"),
            VocabWord("Liquidity", "Tính thanh khoản", "/lɪˈkwɪd.ə.ti/"),
            VocabWord("Margin", "Biên lợi nhuận", "/ˈmɑː.dʒɪn/"),
            VocabWord("Stakeholder", "Bên liên quan", "/ˈsteɪkˌhəʊl.də/"),
            VocabWord("Diversify", "Đa dạng hóa", "/daɪˈvɜː.sɪ.faɪ/"),
            VocabWord("Incentive", "Sự khích lệ", "/ɪnˈsen.tɪv/"),
            VocabWord("Forecast", "Dự báo", "/ˈfɔː.kɑːst/"),
        ),
        "Travel" to listOf(
            VocabWord("Itinerary", "Lịch trình", "/aɪˈtɪn.ər.ər.i/"),
            VocabWord("Excursion", "Chuyến tham quan", "/ɪkˈskɜː.ʃən/"),
            VocabWord("Voyage", "Chuyến hải trình", "/ˈvɔɪ.ɪdʒ/"),
            VocabWord("Layover", "Điểm dừng quá cảnh", "/ˈleɪˌəʊ.və/"),
            VocabWord("Expedition", "Cuộc thám hiểm", "/ˌek.spəˈdɪʃ.ən/"),
            VocabWord("Nomadic", "Du mục", "/nəʊˈmæd.ɪk/"),
            VocabWord("Sojourn", "Kỳ lưu trú", "/ˈsɒdʒ.ɜːn/"),
            VocabWord("Wanderlust", "Đam mê xê dịch", "/ˈwɒn.də.lʌst/"),
            VocabWord("Terminal", "Nhà ga", "/ˈtɜː.mɪ.nəl/"),
            VocabWord("Customs", "Hải quan", "/ˈkʌs.təmz/"),
            VocabWord("Embark", "Khởi hành", "/ɪmˈbɑːk/"),
            VocabWord("Detour", "Đường vòng", "/ˈdiː.tʊə/"),
        ),
        "Cooking" to listOf(
            VocabWord("Marinate", "Ướp", "/ˈmær.ɪ.neɪt/"),
            VocabWord("Caramelize", "Thắng caramel", "/ˈkær.ə.mə.laɪz/"),
            VocabWord("Blanch", "Chần", "/blɑːntʃ/"),
            VocabWord("Garnish", "Trang trí món ăn", "/ˈɡɑː.nɪʃ/"),
            VocabWord("Simmer", "Ninh nhỏ lửa", "/ˈsɪm.ə/"),
            VocabWord("Knead", "Nhào bột", "/niːd/"),
            VocabWord("Saute", "Xào áp chảo", "/ˈsəʊ.teɪ/"),
            VocabWord("Render", "Thắng mỡ", "/ˈren.də/"),
            VocabWord("Emulsify", "Tạo nhũ tương", "/ɪˈmʌl.sɪ.faɪ/"),
            VocabWord("Braise", "Om", "/breɪz/"),
            VocabWord("Zest", "Vỏ bào cam chanh", "/zest/"),
            VocabWord("Deglaze", "Khử cặn chảo", "/diːˈɡleɪz/"),
        ),
        "Art" to listOf(
            VocabWord("Chiaroscuro", "Tương phản sáng tối", "/kiˌɑː.rəˈskʊə.rəʊ/"),
            VocabWord("Composition", "Bố cục", "/ˌkɒm.pəˈzɪʃ.ən/"),
            VocabWord("Palette", "Bảng màu", "/ˈpæl.ət/"),
            VocabWord("Texture", "Kết cấu", "/ˈteks.tʃə/"),
            VocabWord("Abstract", "Trừu tượng", "/ˈæb.strækt/"),
            VocabWord("Perspective", "Phối cảnh", "/pəˈspek.tɪv/"),
            VocabWord("Contour", "Đường viền", "/ˈkɒn.tʊə/"),
            VocabWord("Hue", "Sắc màu", "/hjuː/"),
            VocabWord("Silhouette", "Hình bóng", "/ˌsɪl.uˈet/"),
            VocabWord("Medium", "Chất liệu", "/ˈmiː.di.əm/"),
            VocabWord("Tessellation", "Hoa văn lát kín", "/ˌtes.əˈleɪ.ʃən/"),
            VocabWord("Gradient", "Chuyển sắc", "/ˈɡreɪ.di.ənt/"),
        ),
        "Science" to listOf(
            VocabWord("Hypothesis", "Giả thuyết", "/haɪˈpɒθ.ə.sɪs/"),
            VocabWord("Catalyst", "Chất xúc tác", "/ˈkæt.əl.ɪst/"),
            VocabWord("Entropy", "Độ hỗn loạn", "/ˈen.trə.pi/"),
            VocabWord("Photosynthesis", "Quang hợp", "/ˌfəʊ.təʊˈsɪn.θə.sɪs/"),
            VocabWord("Velocity", "Vận tốc", "/vəˈlɒs.ə.ti/"),
            VocabWord("Molecule", "Phân tử", "/ˈmɒl.ɪ.kjuːl/"),
            VocabWord("Inertia", "Quán tính", "/ɪˈnɜː.ʃə/"),
            VocabWord("Genome", "Bộ gen", "/ˈdʒiː.nəʊm/"),
            VocabWord("Quantum", "Lượng tử", "/ˈkwɒn.təm/"),
            VocabWord("Osmosis", "Sự thẩm thấu", "/ɒzˈməʊ.sɪs/"),
            VocabWord("Isotope", "Đồng vị", "/ˈaɪ.sə.təʊp/"),
            VocabWord("Equilibrium", "Trạng thái cân bằng", "/ˌiː.kwɪˈlɪb.ri.əm/"),
        ),
        "Literature" to listOf(
            VocabWord("Metaphor", "Ẩn dụ", "/ˈmet.ə.fɔː/"),
            VocabWord("Allegory", "Truyện ngụ ngôn", "/ˈæl.ə.ɡər.i/"),
            VocabWord("Prose", "Văn xuôi", "/prəʊz/"),
            VocabWord("Protagonist", "Nhân vật chính", "/prəˈtæɡ.ən.ɪst/"),
            VocabWord("Narrative", "Lối kể chuyện", "/ˈnær.ə.tɪv/"),
            VocabWord("Foreshadow", "Báo trước", "/fɔːˈʃæd.əʊ/"),
            VocabWord("Verse", "Câu thơ", "/vɜːs/"),
            VocabWord("Soliloquy", "Độc thoại", "/səˈlɪl.ə.kwi/"),
            VocabWord("Motif", "Mô típ", "/məʊˈtiːf/"),
            VocabWord("Satire", "Châm biếm", "/ˈsæt.aɪə/"),
            VocabWord("Imagery", "Hình ảnh ẩn dụ", "/ˈɪm.ɪdʒ.ri/"),
            VocabWord("Anthology", "Tuyển tập", "/ænˈθɒl.ə.dʒi/"),
        ),
        "Psychology" to listOf(
            VocabWord("Cognition", "Nhận thức", "/kɒɡˈnɪʃ.ən/"),
            VocabWord("Empathy", "Sự đồng cảm", "/ˈem.pə.θi/"),
            VocabWord("Resilience", "Khả năng phục hồi", "/rɪˈzɪl.i.əns/"),
            VocabWord("Perception", "Tri giác", "/pəˈsep.ʃən/"),
            VocabWord("Bias", "Thiên kiến", "/ˈbaɪ.əs/"),
            VocabWord("Catharsis", "Sự thanh lọc cảm xúc", "/kəˈθɑː.sɪs/"),
            VocabWord("Conditioning", "Điều kiện hóa", "/kənˈdɪʃ.ən.ɪŋ/"),
            VocabWord("Introspection", "Sự nội quan", "/ˌɪn.trəˈspek.ʃən/"),
            VocabWord("Motivation", "Động lực", "/ˌməʊ.tɪˈveɪ.ʃən/"),
            VocabWord("Stimulus", "Tác nhân kích thích", "/ˈstɪm.jə.ləs/"),
            VocabWord("Disposition", "Khuynh hướng", "/ˌdɪs.pəˈzɪʃ.ən/"),
            VocabWord("Reinforce", "Củng cố", "/ˌriː.ɪnˈfɔːs/"),
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

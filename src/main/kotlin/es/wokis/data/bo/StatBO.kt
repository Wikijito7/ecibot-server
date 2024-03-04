package es.wokis.data.bo

data class StatsBO(val type: StatsType, val stats: List<StatBO>)
data class StatBO(val description: String?, val quantity: Int)

enum class StatsType(val type: String) {
    SOUND_STAT("SOUND"),
    COMMAND_STAT("COMMAND"),
    USER_STAT("USER"),
    KIWI_STAT("KIWI")
}
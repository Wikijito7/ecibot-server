package es.wokis.data.dbo.radio

data class RadioPageDBO(
    val currentPage: Int,
    val maxPage: Int,
    val radios: List<RadioDBO>
)
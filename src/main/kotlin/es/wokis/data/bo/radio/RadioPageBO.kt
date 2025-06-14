package es.wokis.data.bo.radio

data class RadioPageBO(
    val currentPage: Int,
    val maxPage: Int,
    val radios: List<RadioBO>
)
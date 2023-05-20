package es.wokis.data.bo.user

data class TOTPResponseBO(
    val encodedSecret: String,
    val totpUrl: String,
    val words: List<String>
)
package hse24

data class AppConfig(

    val baseApiUrl: String,
    val baseImageRetrievalUrl: String,
    val deviceTypeHeader: String,
    val localeTypeHeader: String
)

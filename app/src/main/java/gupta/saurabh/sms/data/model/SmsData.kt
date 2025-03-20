package gupta.saurabh.sms.data.model

data class SmsData(

    val sender: String,

    val body: String,

    val timestamp: Long,

    val contentType: String,

    val isRead: Boolean
)
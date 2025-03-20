package gupta.saurabh.sms.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {

    fun formatTimestamp(timestamp: Long): String {

        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

        return sdf.format(Date(timestamp))
    }
}
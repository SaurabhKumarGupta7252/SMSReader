package gupta.saurabh.sms.data.repository

import android.content.Context
import android.net.Uri
import android.provider.Telephony
import gupta.saurabh.sms.data.model.SmsData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmsRepository @Inject constructor() {

    suspend fun readSms(context: Context) = withContext(Dispatchers.IO) {

        val list = ArrayList<SmsData>()

        val contentResolver = context.contentResolver

        val cursor = contentResolver.query(

            Telephony.Sms.CONTENT_URI,

            arrayOf(

                Telephony.Sms.ADDRESS,

                Telephony.Sms.BODY,

                Telephony.Sms.DATE,

                Telephony.Sms.READ
            ),

            null, null, Telephony.Sms.DEFAULT_SORT_ORDER
        )

        if (cursor != null && cursor.moveToFirst()) {

            do {

                val address = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS))

                val body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY))

                val date = cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE))

                val isRead = cursor.getInt(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE)) == 1

                list.add(SmsData(address, body, date, "text", isRead))

            } while (cursor.moveToNext())
        }

        val mmsCursor = contentResolver.query(

            Uri.parse("content://mms"),

            arrayOf("_id", "date", "read"),

            null,

            null,

            "date DESC"
        )

        if (mmsCursor != null && mmsCursor.moveToFirst()) {

            do {

                val mmsId = mmsCursor.getString(mmsCursor.getColumnIndexOrThrow("_id"))

                val date = mmsCursor.getLong(mmsCursor.getColumnIndexOrThrow("date"))

                val isRead = mmsCursor.getInt(mmsCursor.getColumnIndexOrThrow("read")) == 1

                val address = getMmsSender(context, mmsId)

                val body = getMmsBody(context, mmsId)

                if (body.isNotEmpty()) {

                    list.add(SmsData(address, body, date * 1000, "media", isRead))
                }

            } while (mmsCursor.moveToNext())

            mmsCursor.close()
        }

        return@withContext list.sortedByDescending { it.timestamp }

    }

    private fun getMmsSender(context: Context, mmsId: String): String {

        val uri = Uri.parse("content://mms/$mmsId/addr")

        val cursor = context.contentResolver.query(

            uri,

            arrayOf("address"),

            "type=137",

            null,

            null

        )

        return if (cursor != null && cursor.moveToFirst()) {

            val sender = cursor.getString(cursor.getColumnIndexOrThrow("address"))

            cursor.close()

            sender

        } else {

            "Unknown"
        }
    }

    private fun getMmsBody(context: Context, mmsId: String): String {

        val uri = Uri.parse("content://mms/$mmsId/part")

        val cursor = context.contentResolver.query(

            uri,

            arrayOf("_id", "ct", "text"),

            "ct='text/plain'",

            null,

            null
        )

        val body = StringBuilder()

        if (cursor != null && cursor.moveToFirst()) {

            do {

                val partId = cursor.getString(cursor.getColumnIndexOrThrow("_id"))

                val text = cursor.getString(cursor.getColumnIndexOrThrow("text"))

                if (text != null) {

                    body.append(text)

                } else {

                    body.append(readMmsTextFromFile(context, partId))
                }

            } while (cursor.moveToNext())

            cursor.close()

        }

        return body.toString()
    }

    private fun readMmsTextFromFile(context: Context, partId: String): String {

        val uri = Uri.parse("content://mms/part/$partId")

        return try {

            val inputStream = context.contentResolver.openInputStream(uri)

            inputStream?.bufferedReader().use { it?.readText() } ?: ""

        } catch (e: Exception) {

            ""
        }
    }
}
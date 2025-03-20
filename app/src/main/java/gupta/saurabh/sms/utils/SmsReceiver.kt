package gupta.saurabh.sms.utils


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION == intent.action) {

            for (smsMessage in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {

                val sender = smsMessage.originatingAddress ?: "Unknown"

                val messageBody = smsMessage.messageBody

                val timestamp = smsMessage.timestampMillis

                val smsIntent = Intent("SMS_RECEIVED").apply {

                    putExtra("sender", sender)

                    putExtra("message", messageBody)

                    putExtra("timestamp", timestamp)
                }

                LocalBroadcastManager.getInstance(context).sendBroadcast(smsIntent)
            }
        }
    }
}


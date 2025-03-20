package gupta.saurabh.sms.ui.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import gupta.saurabh.sms.R
import gupta.saurabh.sms.data.model.SmsData
import gupta.saurabh.sms.databinding.ActivityMainBinding
import gupta.saurabh.sms.ui.adapter.SmsAdapter
import gupta.saurabh.sms.ui.viewmodel.SmsViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val smsViewModel: SmsViewModel by viewModels()

    private lateinit var smsAdapter: SmsAdapter

    private val smsReceiver = object : BroadcastReceiver() {

        @SuppressLint("NotifyDataSetChanged")
        override fun onReceive(context: Context, intent: Intent) {

            val sender = intent.getStringExtra("sender") ?: "Unknown"

            val message = intent.getStringExtra("message") ?: ""

            val timestamp = intent.getLongExtra("timestamp", 0)

            smsViewModel.addSms(sender, message, timestamp)

            smsAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->

            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)

            insets
        }

        val mmsObserver = MmsObserver(Handler())

        contentResolver.registerContentObserver(

            Uri.parse("content://mms"),

            true,

            mmsObserver
        )

        smsAdapter = SmsAdapter()

        binding.rvSmsList.adapter = smsAdapter

        binding.etSender.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                smsViewModel.filterSmsBySender(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        checkAndRequestSmsPermissions()

        observerForSMSAndMMS()

        LocalBroadcastManager.getInstance(this).registerReceiver(

            smsReceiver, IntentFilter("SMS_RECEIVED")
        )
    }

    private fun observerForSMSAndMMS() {

        binding.animationView.visibility = View.VISIBLE

        smsViewModel.smsList.observe(this) { smsList ->

            binding.animationView.visibility = View.GONE

            smsAdapter.setList(smsList)
        }

        smsViewModel.filteredSmsList.observe(this) { filteredList ->

            binding.animationView.visibility = View.GONE

            smsAdapter.setList(filteredList)
        }
    }

    private fun checkAndRequestSmsPermissions() {

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_SMS
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            smsViewModel.readSms(this)

        } else {

            val smsPermissions = arrayOf(

                android.Manifest.permission.READ_SMS,

                android.Manifest.permission.RECEIVE_SMS,

                android.Manifest.permission.RECEIVE_MMS,
            )

            val permissionsNeeded = smsPermissions.filter {

                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }

            if (permissionsNeeded.isNotEmpty()) {

                ActivityCompat.requestPermissions(this, permissionsNeeded.toTypedArray(), 101)
            }
        }
    }

    override fun onRequestPermissionsResult(

        requestCode: Int,

        permissions: Array<out String>,

        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 101 && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {

            smsViewModel.readSms(this)

        } else {

            Toast.makeText(this, "SMS Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {

        super.onDestroy()

        LocalBroadcastManager.getInstance(this).unregisterReceiver(smsReceiver)
    }

    inner class MmsObserver(private val handler: Handler) : ContentObserver(handler) {

        override fun onChange(selfChange: Boolean) {

            super.onChange(selfChange)

            smsViewModel.readSms(context = this@MainActivity)
        }
    }

    companion object {

        fun launchActivity(activity: Activity) {

            activity.startActivity(Intent(activity, MainActivity::class.java))

            activity.finish()
        }
    }
}
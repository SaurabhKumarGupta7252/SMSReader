package gupta.saurabh.sms.ui.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gupta.saurabh.sms.data.model.SmsData
import gupta.saurabh.sms.data.repository.SmsRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SmsViewModel @Inject constructor(
    private val repository: SmsRepository
) : ViewModel() {

    val smsList = MutableLiveData<List<SmsData>>()

    val filteredSmsList = MutableLiveData<List<SmsData>>()

    fun readSms(context: Context) {

        viewModelScope.launch {

            smsList.postValue(repository.readSms(context))
        }
    }

    fun filterSmsBySender(senderId: String) {

        if (senderId.isEmpty()) {

            filteredSmsList.postValue(smsList.value)

        } else {

            filteredSmsList.postValue(smsList.value?.filter {

                it.sender.contains(

                    senderId,

                    true
                )

            } as ArrayList<SmsData>?)
        }
    }

    fun addSms(sender: String, body: String, timestamp: Long) {

        val newSms = SmsData(sender, body, timestamp, "text", false)

        smsList.value.orEmpty().toMutableList().add(0, newSms)
    }
}
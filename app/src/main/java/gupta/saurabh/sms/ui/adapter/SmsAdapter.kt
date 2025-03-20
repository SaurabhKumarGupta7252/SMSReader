package gupta.saurabh.sms.ui.adapter


import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import gupta.saurabh.sms.R
import gupta.saurabh.sms.data.model.SmsData
import gupta.saurabh.sms.databinding.ItemSmsBinding
import gupta.saurabh.sms.utils.DateUtils

class SmsAdapter : RecyclerView.Adapter<SmsAdapter.SmsViewHolder>() {

    private var list = listOf<SmsData>()

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<SmsData>) {

        this@SmsAdapter.list = list

        notifyDataSetChanged()
    }

    inner class SmsViewHolder(private val binding: ItemSmsBinding) :

        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(sms: SmsData) {

            binding.sender.text = "From: ${sms.sender}"

            binding.body.text = sms.body

            binding.time.text = DateUtils.formatTimestamp(sms.timestamp)

            binding.type.setImageResource(if (sms.contentType == "text") R.drawable.ic_text_sms else R.drawable.ic_image)

            if (sms.isRead) {

                binding.sender.setTextColor(Color.GRAY)

                binding.body.setTextColor(Color.GRAY)

            } else {

                binding.sender.setTextColor(Color.BLACK)

                binding.body.setTextColor(Color.BLACK)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmsViewHolder {

        val binding = ItemSmsBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return SmsViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SmsViewHolder, position: Int) {

        val sms = list[position]

        holder.bind(sms)
    }

    override fun getItemCount(): Int = list.size
}

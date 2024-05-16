package com.example.antitheifproject.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.antitheftalarm.dont.touch.phone.finder.databinding.ItemDetectionSoundGridBinding
import com.example.antitheifproject.model.SoundModel

class SoundSelectGridAdapter (
    private var clickItem: ((Int) -> Unit),private var soundData: ArrayList<SoundModel>
) : RecyclerView.Adapter<SoundSelectGridAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemDetectionSoundGridBinding) : RecyclerView.ViewHolder(binding.root)

    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            ItemDetectionSoundGridBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return soundData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.countryName.text = soundData[position].soundName
        holder.binding.radioButton.isChecked = soundData[position].isCheck

        holder.binding.radioButton.setOnClickListener {
            checkSingleBox(position)
            clickItem.invoke(position)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun selectSound(langPositionSelected: Int) {
        for (i in soundData.indices) {
            soundData[i].isCheck = i == langPositionSelected
        }

        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun checkSingleBox(pos: Int) {
        for (i in soundData.indices) {
            soundData[i].isCheck = i == pos
        }
        notifyDataSetChanged()
    }


}
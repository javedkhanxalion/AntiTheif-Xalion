package com.example.antitheifproject.adapter

import android.app.Activity
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.antitheftalarm.dont.touch.phone.finder.databinding.IntruderItemBinding
import com.example.antitheifproject.model.IntruderModels
import java.text.SimpleDateFormat
import java.util.Date

class IntruderAdapter(private val models: ArrayList<IntruderModels>?, val context: Activity, val onClick : ((IntruderModels,Uri)->Unit)) :
    RecyclerView.Adapter<IntruderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        return ViewHolder(
            IntruderItemBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {

        val intruderModels = models?.get(i)
        Glide.with(context).load(Uri.fromFile(intruderModels?.file)).into(viewHolder.binding.image)
        val date = intruderModels?.file?.lastModified()?.let { Date(it) }
        viewHolder.binding.date.text = SimpleDateFormat("MM/dd/yyyy").format(date)
        viewHolder.binding.time.text = SimpleDateFormat("HH:mm:aa").format(date)

        viewHolder.binding.intuderMain.setOnClickListener {
            onClick.invoke(intruderModels?:return@setOnClickListener,Uri.fromFile(intruderModels?.file))
        }

    }

    override fun getItemCount(): Int {
        return models?.size ?: return 0
    }

    class ViewHolder(var binding: IntruderItemBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}
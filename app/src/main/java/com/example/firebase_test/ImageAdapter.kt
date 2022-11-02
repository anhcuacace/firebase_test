package com.example.firebase_test

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firebase_test.databinding.LayoutImageBinding

class ImageAdapter : RecyclerView.Adapter<ImageAdapter.MyViewHotel>() {
    inner class MyViewHotel(private val item: LayoutImageBinding) :
        RecyclerView.ViewHolder(item.root) {
        fun bind(position: Int) {
            Glide.with(itemView.context).load(list!![position])
                .placeholder(R.drawable.ic_baseline_image_32)
                .error(R.drawable.ic_baseline_image_32)
                .into(item.appCompatImageView)
        }
    }

    var list: List<Uri>? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHotel {
        val binding = LayoutImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHotel(binding)
    }

    override fun onBindViewHolder(holder: MyViewHotel, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = list?.size ?: 0
}
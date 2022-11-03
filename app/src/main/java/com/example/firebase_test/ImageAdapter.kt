package com.example.firebase_test

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
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

            if (listItemChoices.any { it==list!![position] }){
                item.choice.setImageResource(R.drawable.ic_checked)
            }else{
                item.choice.setImageResource(R.drawable.ic_check)
            }
            item.choice.setOnClickListener{
                val img=list!![position]
                if (listItemChoices.size>=6){
                    if (listItemChoices.any{it==img}){
                        listItemChoices.remove(img)

                        notifyItemChanged(position)
                    }else{
                        Toast.makeText(itemView.context,"bạn chỉ được chọn tối đa 6 bức ảnh",Toast.LENGTH_LONG).show()
                    }
                }else{
                    if (listItemChoices.any{it==img}){
                        listItemChoices.remove(img)
                    }else{
                        listItemChoices.add(img)
                    }
                    notifyItemChanged(position)
                }
            }
        }
    }
    var listItemChoices= mutableListOf<Uri>()
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
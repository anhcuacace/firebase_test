package com.example.firebase_test

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase_test.databinding.ItemRoomsBinding
import kotlinx.coroutines.runBlocking

class RoomAdapter : RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    inner class RoomViewHolder(private val binding: ItemRoomsBinding) :RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
                val adapter = roomList!![position].img?.let { SliderAdapter(it) }
                binding.viewPager.adapter = adapter
                binding.room = roomList!![position]
                itemView.setOnClickListener {

                    onclickItem?.invoke(roomList!![position])
                }
            }
        }
    var onclickItem: ((Room) -> Unit)? = null
    var roomList: List<Room>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val binding = ItemRoomsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = roomList?.size ?: 0
}
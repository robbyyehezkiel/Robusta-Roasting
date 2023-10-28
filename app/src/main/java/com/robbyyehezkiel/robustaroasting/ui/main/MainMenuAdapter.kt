package com.robbyyehezkiel.robustaroasting.ui.main

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.robbyyehezkiel.robustaroasting.R
import com.robbyyehezkiel.robustaroasting.data.model.Menu
import com.robbyyehezkiel.robustaroasting.databinding.ItemListMenuBinding

class MainMenuAdapter(
    private val listMenu: List<Menu>,
    private val onItemClickCallback: (data: Menu) -> Unit
) : RecyclerView.Adapter<MainMenuAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemListMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (id, name, photo) = listMenu[position]
        holder.bind(name, photo)
    }

    override fun getItemCount(): Int = listMenu.size


    inner class ListViewHolder(private val binding: ItemListMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(name: String, photoResId: Int) {
            binding.menuTitle.text = name

            // Load the drawable from the resource ID
            val drawable: Drawable? = ContextCompat.getDrawable(itemView.context, photoResId)

            // Set the drawable as a compound drawable to the menuTitle TextView
            binding.menuTitle.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)

            itemView.setOnClickListener { onItemClickCallback.invoke(listMenu[adapterPosition]) }
        }
    }
}

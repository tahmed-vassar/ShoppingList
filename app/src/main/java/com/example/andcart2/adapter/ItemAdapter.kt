package com.example.andcart2.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.andcart2.ScrollingActivity
import com.example.andcart2.data.AppDatabase
import com.example.andcart2.data.AppDatabase.Companion.getInstance
import com.example.andcart2.data.Item
import com.example.andcart2.databinding.ItemRowBinding
import com.example.andcart2.touch.TouchHelperCallback
import java.text.NumberFormat


class ItemRecyclerAdapter : ListAdapter<Item, ItemRecyclerAdapter.ViewHolder>, TouchHelperCallback {

    private val context: Context

    constructor(context: Context) : super(ItemDiffCallback()) {

        this.context = context

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemRowBinding = ItemRowBinding.inflate(
            LayoutInflater.from(context),
            parent, false
        )
        return ViewHolder(itemRowBinding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(holder.adapterPosition)
        holder.bind(currentItem)


        holder.itemRowBinding.tvName.text = currentItem.name
        holder.itemRowBinding.cbBought.isChecked = currentItem.bought
        holder.itemRowBinding.tvCategory.text = currentItem.getCategory(currentItem.categoryIDX)
        holder.itemRowBinding.img.setImageResource(currentItem.getImageResource(currentItem))


        holder.itemRowBinding.tvPrice.text = formatCurrency(currentItem.price)

        holder.itemRowBinding.btnDelete.setOnClickListener {
            deleteItem(holder.adapterPosition)
        }

        holder.itemRowBinding.btnEdit.setOnClickListener {
            (context as ScrollingActivity).showEditDialog(getItem(holder.adapterPosition))
        }

        holder.itemRowBinding.cbBought.setOnClickListener {
            currentItem.bought = holder.itemRowBinding.cbBought.isChecked
        }

        holder.itemRowBinding.btnDetails.setOnClickListener {
            (context as ScrollingActivity).showDetailsDialog(getItem(holder.adapterPosition))
        }




    }

    fun formatCurrency(price: Float?): String {
        val numberFormat = NumberFormat.getCurrencyInstance()
        numberFormat.maximumFractionDigits = 2
        return numberFormat.format(price)
    }


    private fun deleteItem(position: Int) {
        val dbThread = Thread {
            getInstance(context).itemDAO().deleteItem(getItem(position))

        }
        dbThread.start()

    }


    override fun onDismissed(position: Int) {
        deleteItem(position)
    }


    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        notifyItemMoved(fromPosition, toPosition)
    }


    inner class ViewHolder(val itemRowBinding: ItemRowBinding) :
        RecyclerView.ViewHolder(itemRowBinding.root) {
        fun bind(item: Item) {
            val tvName: TextView = itemRowBinding.tvName
            val cbBought: CheckBox = itemRowBinding.cbBought
            val tvPrice: TextView = itemRowBinding.tvPrice
            val btnDelete: Button = itemRowBinding.btnDelete
            val btnEdit: Button = itemRowBinding.btnEdit
        }
    }

}


class ItemDiffCallback : DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem._id == newItem._id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem == newItem
    }
}



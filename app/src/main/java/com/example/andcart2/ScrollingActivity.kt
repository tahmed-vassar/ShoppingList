package com.example.andcart2

import android.app.PendingIntent.getActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.andcart2.adapter.ItemRecyclerAdapter
import com.example.andcart2.data.AppDatabase
import com.example.andcart2.data.Item
import com.example.andcart2.databinding.ActivityScrollingBinding
import com.example.andcart2.item_dialog.ItemDialog
import com.example.andcart2.touch.RecyclerTouchCallback
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import kotlin.concurrent.thread


class ScrollingActivity : AppCompatActivity(), ItemDialog.ItemHandler {

    companion object {
        const val KEY_ITEM_EDIT = "KEY_ITEM_EDIT"
        const val PREF_DEFAULT = "PREF_DEFAULT"
        const val KEY_STARTED = "KEY_STARTED"
        const val KEY_ITEM_DETAILS = "KEY_ITEM_DETAILS"
    }


    private lateinit var binding: ActivityScrollingBinding
    private lateinit var adapter: ItemRecyclerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        actionBar?.show()

        binding = ActivityScrollingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))
        binding.toolbarLayout.title = title
        binding.fab.setOnClickListener { view ->
            ItemDialog().show(supportFragmentManager, "ITEM_DIALOG")
        }



        initItemRecyclerView()

        if (!wasAlreadyStarted()) {
            MaterialTapTargetPrompt.Builder(this@ScrollingActivity)
                .setTarget(binding.fab)
                .setPrimaryText(getString(R.string.new_item))
                .setSecondaryText(getString(R.string.add_new_item))
                .show()

            saveThatAppWasStarted()
        }

        val recyclerView = binding.recyclerItem
        recyclerView.itemAnimator = SlideInUpAnimator(OvershootInterpolator(1f))


        binding.btnDeleteAll.setOnClickListener {
            deleteAllItems()

        }

    }


    private fun initItemRecyclerView() {
        adapter = ItemRecyclerAdapter(this)
        binding.recyclerItem.adapter = adapter

        var liveDataItems = AppDatabase.getInstance(this).itemDAO().getAllItem()
        liveDataItems.observe(this, Observer { items ->
            adapter.submitList(items)

        })

        calcPrice()

        val touchCallbackList = RecyclerTouchCallback(adapter)
        val itemTouchHelper = ItemTouchHelper(touchCallbackList)
        itemTouchHelper.attachToRecyclerView(binding.recyclerItem)
    }


    private fun saveThatAppWasStarted() {
        val sharedPref = getSharedPreferences(PREF_DEFAULT, MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean(KEY_STARTED, true)
        editor.commit()
    }

    private fun wasAlreadyStarted() =
        getSharedPreferences(PREF_DEFAULT, MODE_PRIVATE).getBoolean(
            KEY_STARTED, false
        )


    public fun showEditDialog(itemToEdit: Item) {
        val editDialog = ItemDialog()

        val bundle = Bundle()
        bundle.putSerializable(KEY_ITEM_EDIT, itemToEdit)
        editDialog.arguments = bundle

        editDialog.show(supportFragmentManager, "TAG_ITEM_EDIT")
    }

    public fun showDetailsDialog(item: Item){
        val detailsDialog = ItemDialog()

        val bundle = Bundle()
        bundle.putSerializable(KEY_ITEM_DETAILS, item)
        detailsDialog.arguments = bundle
        detailsDialog.show(supportFragmentManager, "TAG_ITEM_DETAILS")

    }


    override fun itemCreated(newItem: Item) {
        val dbThread = Thread {
            AppDatabase.getInstance(this).itemDAO().addItem(newItem)
        }
        dbThread.start()
    }

    override fun itemUpdated(editedItem: Item) {
        val dbThread = Thread {
            AppDatabase.getInstance(this).itemDAO().updateItem(editedItem)
        }
        dbThread.start()
    }

    private fun deleteAllItems() {
        val dbThread = Thread {
            AppDatabase.getInstance(this).itemDAO().deleteAllItems()
        }
        dbThread.start()
    }


    private fun calcPrice() {
        var sum = AppDatabase.getInstance(this).itemDAO().calcPrice()

        sum.observe(this, Observer {
            if (sum.value != null) {
                binding.tvSum.text = adapter.formatCurrency(sum.value)
            } else {
                binding.tvSum.text = adapter.formatCurrency(0.0F)
            }
        })

    }


}
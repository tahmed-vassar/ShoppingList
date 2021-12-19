package com.example.andcart2.item_dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.afollestad.materialdialogs.MaterialDialog
import com.example.andcart2.R
import com.example.andcart2.ScrollingActivity
import com.example.andcart2.data.Item
import com.example.andcart2.databinding.ItemDialogBinding


class ItemDialog : DialogFragment(), AdapterView.OnItemSelectedListener {

    interface ItemHandler {

        fun itemCreated(newItem: Item)

        fun itemUpdated(editedItem: Item)
    }

    lateinit var itemHandler: ItemHandler

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is ItemHandler) {
            itemHandler = context
        } else {
            throw RuntimeException(
                "The Activity is not implementing the ItemHandler interface."
            )
        }
    }

    private lateinit var dialogBinding: ItemDialogBinding

    private var isEditMode = false
    private var isDetailsMode = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialogBuilder = AlertDialog.Builder(requireContext())

        if (arguments != null && requireArguments().containsKey(ScrollingActivity.KEY_ITEM_EDIT))
         {
             isEditMode = true
             dialogBuilder.setTitle(getString(R.string.edit_item))
        } else if (arguments != null && requireArguments().containsKey(ScrollingActivity.KEY_ITEM_DETAILS)) {
            isDetailsMode = true
            dialogBuilder.setTitle(getString(R.string.view_item_details))

        }
        else {
            dialogBuilder.setTitle(getString(R.string.new_item))
        }

        dialogBinding = ItemDialogBinding.inflate(activity!!.layoutInflater)
        dialogBuilder.setView(dialogBinding.root)


        setUpSpinner()


        if (isEditMode){
            editMode()
        }
        else if (isDetailsMode){
            detailsMode()
        }


        dialogBuilder.setPositiveButton("OK") { _, _ ->
        }

        dialogBuilder.setNegativeButton("Cancel") { _, _ ->
        }

        return dialogBuilder.create()
    }


    private fun setUpSpinner(){
        val categoryAdapter = ArrayAdapter.createFromResource(
            activity as Context,
            R.array.category_array,
            android.R.layout.simple_spinner_item
        )
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dialogBinding.spinnerCategory.adapter = categoryAdapter
        dialogBinding.spinnerCategory.onItemSelectedListener = this

    }

    private fun editMode(){
        val itemToEdit: Item = requireArguments().getSerializable(ScrollingActivity.KEY_ITEM_EDIT) as Item

        dialogBinding.etItemName.setText(itemToEdit.name)
        dialogBinding.etPrice.setText(itemToEdit.price.toInt().toString())
        dialogBinding.spinnerCategory.setSelection(itemToEdit.categoryIDX)
        dialogBinding.cbBought.isChecked = itemToEdit.bought
        dialogBinding.etDescription.setText(itemToEdit.description)

    }

    private fun detailsMode(){
        val itemToEdit: Item = requireArguments().getSerializable(ScrollingActivity.KEY_ITEM_DETAILS) as Item

        dialogBinding.etItemName.setText(itemToEdit.name)
        dialogBinding.etPrice.setText(itemToEdit.price.toInt().toString())
        dialogBinding.spinnerCategory.setSelection(itemToEdit.categoryIDX)
        dialogBinding.cbBought.isChecked = itemToEdit.bought
        dialogBinding.etDescription.setText(itemToEdit.description)

        dialogBinding.etItemName.isFocusable = false
        dialogBinding.etPrice.isFocusable = false
        dialogBinding.spinnerCategory.isEnabled = false
        dialogBinding.etDescription.isFocusable = false
        dialogBinding.cbBought.isClickable = false


    }


    override fun onResume() {
        super.onResume()

        val dialog = dialog as AlertDialog
        val positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE)

        positiveButton.setOnClickListener {
            if (dialogBinding.etItemName.text.isNotEmpty()) {
                if (dialogBinding.etPrice.text.isNotEmpty()) {
                    if (isEditMode) {
                        handleItemEdit()
                    } else if (isDetailsMode){
                        handleItemView()
                    }
                    else {
                        handleItemCreate()
                    }
                    dialog.dismiss()
                } else {
                    dialogBinding.etPrice.error = getString(R.string.error_msg)

                }
            } else {
                dialogBinding.etItemName.error = getString(R.string.error_msg)
            }
        }
    }

    private fun handleItemCreate() {

        with(itemHandler) {
            itemCreated(
                Item(
                    null,
                    dialogBinding.etItemName.text.toString(),
                    dialogBinding.spinnerCategory.selectedItemPosition,
                    dialogBinding.etPrice.text.toString().toFloat(),
                    dialogBinding.cbBought.isChecked,
                    dialogBinding.etDescription.text.toString()
                )

            )
        }
    }

    private fun handleItemView(){
        dialogBinding.etItemName.isFocusable = true
        dialogBinding.etPrice.isFocusable = true
        dialogBinding.spinnerCategory.isEnabled = true
        dialogBinding.etDescription.isFocusable = true
        dialogBinding.cbBought.isClickable = true
    }


    private fun handleItemEdit() {
        val itemToEdit = (arguments?.getSerializable(
            ScrollingActivity.KEY_ITEM_EDIT
        ) as Item).copy(
            name = dialogBinding.etItemName.text.toString(),
            bought = dialogBinding.cbBought.isChecked,
            price = dialogBinding.etPrice.text.toString().toFloat(),
            categoryIDX = dialogBinding.spinnerCategory.selectedItemPosition,
            description = dialogBinding.etDescription.text.toString()
        )

        itemHandler.itemUpdated(itemToEdit)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }


}
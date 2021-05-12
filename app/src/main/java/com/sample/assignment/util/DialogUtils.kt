package com.sample.assignment.util

import android.content.Context
import android.content.DialogInterface.BUTTON_NEGATIVE
import android.content.DialogInterface.BUTTON_POSITIVE
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import com.sample.assignment.R
import com.sample.assignment.util.dialogs.AlertDialogListListener
import com.sample.assignment.util.dialogs.AlertDialogListener
import com.sample.core.utility.extensions.empty
import com.sample.core.utility.logger.AppLogger

object DialogUtils {

    private const val OK = "OK"
    private const val CANCEL = "Cancel"

    // TODO | THIS SHOULDN'T BE IN STATIC/OBJECT SCOPE, SUGGEST A BETTER SOLUTION
    private var alertDialog: AlertDialog? = null

    fun twoButtonDialog(
        context: Context?,
        title: String = String.empty,
        message: String,
        alertDialogListener: AlertDialogListener,
        positiveButtonText: String = context?.getString(R.string.dialog_ok)
            ?: OK,
        negativeButtonText: String = context?.getString(R.string.dialog_cancel)
            ?: CANCEL,
        cancelable: Boolean
    ) {
        dismiss()

        context?.let {
            alertDialog = AlertDialog
                .Builder(it)
                .setTitle(title.trim())
                .setMessage(message)
                .setCancelable(cancelable)
                .setPositiveButton(positiveButtonText) { _, _ ->
                    alertDialogListener.onPositive()
                }

                .setNegativeButton(negativeButtonText) { _, _ ->
                    alertDialogListener.onNegative()
                }
                .create()
                .apply {
                    show()
                    getButton(BUTTON_POSITIVE).isAllCaps = false
                    getButton(BUTTON_NEGATIVE).isAllCaps = false
                }
        }
    }

    fun showInfoDialog(
        context: Context?,
        title: String = String.empty,
        message: String = String.empty,
        cancelable: Boolean = true,
        alertDialogListener: AlertDialogListener = object :
            AlertDialogListener {
            override fun onNegative() {}
            override fun onPositive() {}
        },
        buttonName: String = context?.getString(android.R.string.ok) ?: OK
    ) {
        dismiss()

        context?.let {
            alertDialog = AlertDialog
                .Builder(it)
                .setTitle(title.trim())
                .setMessage(message)
                .setCancelable(cancelable)
                .setPositiveButton(buttonName) { _, _ ->
                    alertDialogListener.onPositive()
                }
                .show()
        }
    }

    fun showListDialog(
        context: Context?,
        optionsList: MutableList<String>,
        alertDialogListListener: AlertDialogListListener = object :
            AlertDialogListListener {
            override fun onOptionSelected(selectedOption: Int) {}
        },
        title: String = String.empty
    ) {
        dismiss()

        context?.let {
            val arrayAdapter = ArrayAdapter<String>(
                context,
                R.layout.select_item_dialog_material_row
            )
            arrayAdapter.addAll(optionsList)

            alertDialog = AlertDialog
                .Builder(context)
                .setTitle(title.trim())
                .setOnCancelListener {
                    alertDialogListListener.onCancelled()
                }
                .setAdapter(arrayAdapter) { _, which ->
                    alertDialogListListener.onOptionSelected(selectedOption = which)
                }.show()
        }
    }

    fun dismiss() {
        try {
            alertDialog?.dismiss()
        } catch (e: Exception) {
            AppLogger.e(
                "Exception",
                "Ignore the dialog dismiss when there is no window"
            )
        }
    }

}
package com.sample.assignment.util.dialogs

interface AlertDialogListListener {

    fun onOptionSelected(selectedOption: Int)
    fun onCancelled() {}
}
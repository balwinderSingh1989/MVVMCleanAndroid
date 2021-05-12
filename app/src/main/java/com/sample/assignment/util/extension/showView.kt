package com.sample.assignment.util.extension

import android.view.View

fun View.showView(show: Boolean) {
    this.visibility = when (show) {
        true -> View.VISIBLE
        false -> View.GONE
    }
}
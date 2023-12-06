package com.example.myumkm.util

import android.app.Activity
import android.widget.Toast

fun Activity.toast(message: String?) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun String.isValidEmail() =
    isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
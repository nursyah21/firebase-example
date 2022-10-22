package com.nursyah.firebase.utils

import android.content.Context
import android.widget.Toast

object Utils {
  private var toast: Toast?= null

  fun notify(context: Context, str: String){
    toast?.cancel()
    toast = Toast.makeText(context, str, Toast.LENGTH_SHORT)
    toast?.show()
  }

  const val SHARED_PREFERENCES = "SHARED_PREFERENCES"
  const val SHARED_EMAIL = "SHARED_EMAIL"
  const val SHARED_TOKEN = "SHARED_TOKEN"
}
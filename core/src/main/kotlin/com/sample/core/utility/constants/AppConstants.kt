package com.sample.core.utility.constants

import android.os.Looper
import java.util.*


val JWT_AUTH_HEADER = "JWT_AUTH"


fun isOnMainThread() = Looper.myLooper() == Looper.getMainLooper()


package com.clife.smartutil.binder

import android.graphics.Bitmap
import android.os.Binder

class ImageBinder(val bitmap: Bitmap) : Binder() {

    fun getImage(): Bitmap {
        return bitmap
    }
}
package com.shree.clerkieandroid

import android.app.Activity
import android.content.res.Resources
import android.util.TypedValue
import com.google.android.material.snackbar.Snackbar

val Number.toPx get() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    Resources.getSystem().displayMetrics)


fun Int.pxToDp(px: Int): Int {
    return (px / Resources.getSystem().displayMetrics.density).toInt()
}


fun Activity.showSnackBar(msg:String){
    //val parentLayout = findViewById<View>(android.R.id.content)
    Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG)
        .setAction("CLOSE") { }
        .setActionTextColor(resources.getColor(android.R.color.holo_red_light))
        .show()
}
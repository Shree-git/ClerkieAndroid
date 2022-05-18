package com.shree.clerkieandroid

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import org.json.JSONObject


class Constants {
    companion object{

        @JvmStatic
        fun assignTextViewCommonValues(textView: TextView, rootObj: JSONObject ,context: Context) {
            textView.text = rootObj.optString("text" ,"Lorem Ipsum")
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, rootObj.optInt("font_size", 20).toFloat())

            textView.setTextColor(Color.BLACK)
            when(rootObj.optString("color" ,"")){
                "black"->textView.setTextColor(Color.BLACK)
                "green"->textView.setTextColor(Color.GREEN)
                "red"->textView.setTextColor(Color.RED)
                "gray"->textView.setTextColor(context.resources.getColor(R.color.gray))
            }

            val params: LinearLayout.LayoutParams? = textView.layoutParams as? LinearLayout.LayoutParams
            var typeface = ResourcesCompat.getFont(context, R.font.normal)
            when(rootObj.optString("font_weight" ,"")){
                "normal" -> typeface = ResourcesCompat.getFont(context, R.font.normal)
                "bold" -> typeface = ResourcesCompat.getFont(context, R.font.little_bold)
                "bolder" -> typeface = ResourcesCompat.getFont(context, R.font.bold)
            }
            when(rootObj.optString("alignment" ,"")){
                "left"->textView.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                "center"->textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                "right"->textView.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
            }
            when(rootObj.optString("view_alignment" ,"")){
                "left"->params?.gravity = Gravity.LEFT
                "center"->params?.gravity = Gravity.CENTER
                "right"->params?.gravity = Gravity.RIGHT
            }
            textView.setTypeface(typeface)
            textView.layoutParams = params
        }

        @JvmStatic
        fun assignImageViewCommonValues(imageView: ImageView, rootObj: JSONObject,context: Context) {
            rootObj.optString("h2w_ratio" ,"").takeIf { it.isNotEmpty() }?.let {  }
            val params: LinearLayout.LayoutParams? = imageView.layoutParams as? LinearLayout.LayoutParams

            rootObj.optDouble("width_percent" ,0.0).takeIf { it!= 0.0 }?.let {
                params?.weight = it.toFloat();
            }
            when(rootObj.optString("view_alignment" ,"")){
                "left"->params?.gravity = Gravity.LEFT
                "center"->params?.gravity = Gravity.CENTER
                "right"->params?.gravity = Gravity.RIGHT
            }
            imageView.layoutParams = params
        }
    }
}
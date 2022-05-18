package com.shree.clerkieandroid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.shree.clerkieandroid.Constants.Companion.assignTextViewCommonValues
import com.shree.clerkieandroid.databinding.ActivityMainBinding
import com.shree.clerkieandroid.databinding.ImageWithTextBinding
import org.json.JSONArray
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        supportActionBar?.hide()
        inits()

    }

    private fun inits() {
        resources.openRawResource(R.raw.sample_json).reader().use { it.readText().let {
            var viewDynamic = JSONArray(it)
            for (i in 0 until viewDynamic.length()) {
                var child = viewDynamic.optJSONObject(i)
                child?.let { rootObj ->
                    when {
                        rootObj.optString("type") == "text" -> {
                            var textView = TextView(this)
                            textView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                            assignTextViewCommonValues(textView ,rootObj ,this)
                            binding?.rootView?.addView(textView)
                            actionData(textView , rootObj )
                        }
                        rootObj.optString("type") == "text_with_image" -> {
                            var view = LayoutInflater.from(this).inflate(R.layout.image_with_text ,binding?.root ,false)
                            val imageWithView = ImageWithTextBinding.bind(view)//inflate(LayoutInflater.from(this), binding?.root, true)

                            val titleChild = rootObj.optJSONObject("title")
                            titleChild?.let {
                                assignTextViewCommonValues(imageWithView.titleText ,titleChild,this)
                            }
                            val subTitleChild = rootObj.optJSONObject("subtitle")
                            subTitleChild?.let {
                                assignTextViewCommonValues(imageWithView.subTitleText ,subTitleChild,this)
                            }
                            val imageIcon = rootObj.optJSONObject("image")
                            imageIcon?.let{
                            Glide.with(this).load(it.optString("src" ,"http://")).into(imageWithView.logo)
                            }
                            if (rootObj.has("subtitle")){
                                imageWithView.subTitleText.visibility = View.VISIBLE
                            }else{
                                imageWithView.subTitleText.visibility = View.GONE
                            }
                            if (rootObj.has("height")){
                                rootObj.optInt("height" ,100).let {
                                    imageWithView.root.layoutParams?.height = it.toPx.toInt()
                                }
                            }

                            actionData(imageWithView.root , rootObj )
                            binding?.rootView?.addView(view)
                        }
                        rootObj.optString("type") == "image" -> {
                            var imageView = ImageView(this)
                            imageView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                            imageView.adjustViewBounds = true
                            Glide.with(this).load(rootObj.optString("src" ,"http://")).into(imageView)
//                        Constants.assignImageViewCommonValues(textView, innerObj, requireContext())
                            binding?.rootView?.addView(imageView)
                            actionData(imageView , rootObj )
                        }
                        rootObj.optString("type") == "space" -> {
                            val textView = TextView(this)
                            rootObj.optInt("height" ,100).let {
                                textView.layoutParams = LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT , it.toPx.toInt())
                            }

                            binding?.rootView?.addView(textView)
                        }
                        else -> {}
                    }

                }
            }
        } }
    }

    private fun actionData(root: View, rootObj: JSONObject) {
        when{
            rootObj.optString("click_action","") =="present_popup" ->{
                root.setOnClickListener {
                    rootObj.optJSONObject("click_action_data")?.let{
                        val fm = supportFragmentManager
                        val ft = fm.beginTransaction()
                        val prev = fm.findFragmentByTag("PopUpDialogue")
                        if (prev != null) supportFragmentManager.beginTransaction().remove(prev).commit()
                        val frag = PopUpDialogue.newInstance ({
                        },it.toString())
                        frag.show(ft, "PopUpDialogue")
                    }
                }

            }
            rootObj.optString("click_action","") =="present_fullscreen" ->{
                root.setOnClickListener {
                    rootObj.optJSONObject("click_action_data")?.let{
                        val fm = supportFragmentManager
                        val ft = fm.beginTransaction()
                        val prev = fm.findFragmentByTag("FullScreenDialogue")
                        if (prev != null) supportFragmentManager.beginTransaction().remove(prev).commit()
                        val frag = FullScreenDialogue.newInstance ({
                        },it.toString())
                        frag.show(ft, "FullScreenDialogue")
                    }
                }
            }
            else->{}
        }
    }


}
package com.shree.clerkieandroid

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.shree.clerkieandroid.databinding.DialogueFragmentPopupBinding
import org.json.JSONObject


class PopUpDialogue : DialogFragment() {
    var binding: DialogueFragmentPopupBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataString = arguments?.getString("dataString") ?:""
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialogue_fragment_popup, container, false)
        binding = DialogueFragmentPopupBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inits(view)
    }

    override fun onStart() {
        super.onStart()
        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        width = displayMetrics.widthPixels
        dialog?.window?.setLayout((width * 0.7).toInt()/*width*//*ViewGroup.LayoutParams.MATCH_PARENT*/, /*height */ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.setCanceledOnTouchOutside(true)
    }

    private fun inits(view: View) {
        binding?.closeBtn?.setOnClickListener { dismiss() }
        try {
            Log.i("test" ,"dataString  ${dataString}")
            var obj = JSONObject(dataString)
            var jsonArray = obj.getJSONArray("data")
            for (i in 0 until jsonArray.length()){
                var innerObj = jsonArray.getJSONObject(i)
                when(innerObj.optString("type","")){
                    "text"->{
                        var textView = TextView(requireContext())
                        textView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        Constants.assignTextViewCommonValues(textView, innerObj, requireContext())
                        binding?.container?.addView(textView)
                        actionData(textView , innerObj )
                    }
                    "image"->{
                        var imageView = ImageView(requireContext())
                        imageView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        imageView.adjustViewBounds = true
                        Glide.with(this).load(innerObj.optString("src" ,"http://")).into(imageView)
//                        Constants.assignImageViewCommonValues(textView, innerObj, requireContext())
                        binding?.container?.addView(imageView)
                        actionData(imageView , innerObj )
                    }
                }
            }
        } catch (e: Exception) {
        }
    }

    private fun actionData(root: View, rootObj: JSONObject) {
        when{
            rootObj.optString("click_action","") =="present_popup" ->{
                root.setOnClickListener {
                    rootObj.optJSONObject("click_action_data")?.let{
                        val fm = childFragmentManager
                        val ft = fm.beginTransaction()
                        val prev = fm.findFragmentByTag("PopUpDialogue")
                        if (prev != null) childFragmentManager.beginTransaction().remove(prev).commit()
                        val frag = PopUpDialogue.newInstance ({
                        },it.toString())
                        frag.show(ft, "PopUpDialogue")
                    }
                }

            }
            rootObj.optString("click_action","") =="present_fullscreen" ->{
                root.setOnClickListener {
                    rootObj.optJSONObject("click_action_data")?.let{
                        val fm = childFragmentManager
                        val ft = fm.beginTransaction()
                        val prev = fm.findFragmentByTag("FullScreenDialogue")
                        if (prev != null) childFragmentManager.beginTransaction().remove(prev).commit()
                        val frag = FullScreenDialogue.newInstance ({
                        },it.toString())
                        frag.show(ft, "FullScreenDialogue")
                    }
                }
            }
            else->{}
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = super.onCreateDialog(savedInstanceState)

        // request a window without the title
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        hideNavItem(dialog)
//        hideStatusBar(dialog)
        return dialog
        //return super.onCreateDialog(savedInstanceState)
    }

    private fun hideStatusBar(dialog: Dialog) {
        if (Build.VERSION.SDK_INT < 16) {
            dialog.window!!.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            val decorView = dialog.window!!.decorView
            val uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
            decorView.systemUiVisibility = uiOptions
        }
    }

    fun hideNavItem(dialog: Dialog) {
        val currentApiVersion = Build.VERSION.SDK_INT
        val flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        // This work only for android 4.4+
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {
            dialog.window?.decorView?.systemUiVisibility = flags
            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            val decorView = dialog.window?.decorView
            decorView?.setOnSystemUiVisibilityChangeListener {
                dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
                /*if ((it and View.SYSTEM_UI_FLAG_FULLSCREEN) == View.VISIBLE) {*/
                decorView.systemUiVisibility = flags
                /*}*/
            }
        }
    }

    var dataString:String =""
    companion object {
        var width: Int = 0

        var callBack: (() -> Unit)? = null
        fun newInstance(callBack: (() -> Unit) , dataString:String): PopUpDialogue {
            val args = Bundle()
            args.putString("dataString",dataString)
            this.callBack = callBack
            val fragment = PopUpDialogue()
            fragment.arguments = args
            return fragment
        }
    }
}

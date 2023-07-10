package com.rohnsha.stocksense

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class customToast(context: Context): Toast(context) {

    companion object{
        fun makeText(context: Context, message: String, level: Int): customToast{

            val toast= customToast(context)
            toast.duration= Toast.LENGTH_LONG

            val inflater= context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view= inflater.inflate(R.layout.custom_toast, null)
            toast.view= view

            view.findViewById<TextView>(R.id.toastMSG).text= message

            val dp2px= context.resources.displayMetrics.density
            val yOffset= (17*dp2px).toInt()
            toast.setGravity(Gravity.TOP, 0, yOffset)

            view.findViewById<ImageView>(R.id.toastCancel).setOnClickListener {
                toast.cancel()
            }

            val toastLogoBG= view.findViewById<ImageView>(R.id.toastLogo)
            toastLogoBG.setColorFilter(context.resources.getColor(android.R.color.white))
            if (level==1){
                toastLogoBG.setImageResource(R.drawable.baseline_check_24)
                toastLogoBG.backgroundTintList= ColorStateList.valueOf(Color.parseColor("#75BF72"))
            } else if (level==2){
                toastLogoBG.setImageResource(R.drawable.baseline_error_outline_24)
                toastLogoBG.backgroundTintList= ColorStateList.valueOf(Color.parseColor("#FFA500"))
            } else if (level==3){
                toastLogoBG.setImageResource(R.drawable.baseline_clear_24)
                toastLogoBG.backgroundTintList= ColorStateList.valueOf(Color.parseColor("#DF5060"))
            }

            return toast
        }
    }

}
package com.example.quiz.util

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.example.quiz.R
import java.nio.channels.spi.AbstractSelectionKey

class SharedPreferenceManger(context: Context) {
    private val preference:SharedPreferences = context.getSharedPreferences(
        context.getString(R.string.app_name),
        AppCompatActivity.MODE_PRIVATE
    )
    private val editor :SharedPreferences.Editor = preference.edit()


    fun getLevelState(key:String) : Boolean {
        return preference.getBoolean(key, false)

    }
    fun setLevelState(key: String, value:Boolean){
        editor.putBoolean(key, value)
        editor.commit()
    }

}

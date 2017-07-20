package com.distribution.buzztime.coffeedistribution

import android.R.id.edit
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager



/**
 * Created by sjg on 2017/7/19.
 */
class PrefUtils {
    /**
     * 清除所有数据
     * @param context
     * *
     * @return
     */
    fun clear(context: Context): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = pref.edit()
        editor.clear()
        return editor.commit()
    }

    /**
     * 移除某项数据
     * @param context
     * *
     * @param key
     * *
     * @return
     */
    fun remove(context: Context, key: String): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = pref.edit()
        editor.remove(key)
        return editor.commit()
    }

    /**
     * 获取数据
     */
    fun getInt(context: Context, key: String, defValue: Int): Int {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, defValue)
    }

    fun getLong(context: Context, key: String, defValue: Long): Long {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(key, defValue)
    }

    fun getString(context: Context, key: String, defValue: String): String {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, defValue)
    }

    fun getBoolean(context: Context, key: String, defValue: Boolean): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, defValue)
    }

    /**
     * 存储数据
     */
    fun putInt(context: Context, key: String, value: Int): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = pref.edit()
        editor.putInt(key, value)
        return editor.commit()
    }

    fun putLong(context: Context, key: String, value: Long): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = pref.edit()
        editor.putLong(key, value)
        return editor.commit()
    }

    fun putString(context: Context, key: String, value: String): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = pref.edit()
        editor.putString(key, value)
        return editor.commit()
    }

    fun putBoolean(context: Context, key: String, value: Boolean): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = pref.edit()
        editor.putBoolean(key, value)
        return editor.commit()
    }
}
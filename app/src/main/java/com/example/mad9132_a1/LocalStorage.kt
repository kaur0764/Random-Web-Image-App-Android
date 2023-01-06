package com.example.mad9132_a1

import android.content.Context
import android.content.SharedPreferences

/*
 * Created by Tony Davidson on November 05, 2022
 *
 *
 * Note: All changes you make in a SharedPreferences editor are batched,
 * they are not copied back to the original SharedPreferences until you call commit() or apply()
 * commit is immediate and can cause blocking
 * apply runs in the background therefore it is non-blocking
 *
 * All modifications to the preferences must go through an Editor object so that the preference
 * values remain in a consistent state and control when they are committed to storage.
 * Objects that are returned from the various get methods must be treated as immutable by the App
 *
*/


class LocalStorage(context: Context = TheApp.context) {

    // region Properties

    private val preferencesName = context.getString(R.string.app_name) // use the app name
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        preferencesName,
        Context.MODE_PRIVATE
    )

    // endregion

    // region Methods

    fun contains(KEY_NAME: String): Boolean {
        return sharedPreferences.contains(KEY_NAME)
    }

    @Suppress("UNUSED")
    fun removeValue(KEY_NAME: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.remove(KEY_NAME)
        editor.apply()
    }

    @Suppress("UNUSED")
    fun clearSharedPreference() {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    @Suppress("UNUSED")
    fun getAll(): Map<String, *> {
        return sharedPreferences.all
    }


    // endregion

    // region Set methods

    @Suppress("UNUSED")
    fun save(KEY_NAME: String, text: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(KEY_NAME, text)
        editor.apply()
    }

    @Suppress("UNUSED")
    fun save(KEY_NAME: String, text: Int) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putInt(KEY_NAME, text)
        editor.apply()
    }

    @Suppress("UNUSED")
    fun save(KEY_NAME: String, text: Long) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putLong(KEY_NAME, text)
        editor.apply()
    }

    @Suppress("UNUSED")
    fun save(KEY_NAME: String, text: Float) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putFloat(KEY_NAME, text)
        editor.apply()
    }

    @Suppress("UNUSED")
    fun save(KEY_NAME: String, text: Boolean) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean(KEY_NAME, text)
        editor.apply()
    }

    @Suppress("UNUSED")
    fun save(KEY_NAME: String, text: Set<String>) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putStringSet(KEY_NAME, text)
        editor.apply()
    }

    // endregion

    // region Get methods
    fun getValueString(KEY_NAME: String): String? {
        return sharedPreferences.getString(KEY_NAME, null)
    }

    @Suppress("UNUSED")
    fun getValueInt(KEY_NAME: String): Int {
        return sharedPreferences.getInt(KEY_NAME, 0)
    }

    @Suppress("UNUSED")
    fun getValueLong(KEY_NAME: String): Long {
        return sharedPreferences.getLong(KEY_NAME, 0)
    }

    @Suppress("UNUSED")
    fun getValueFloat(KEY_NAME: String): Float {
        return sharedPreferences.getFloat(KEY_NAME, 0.0f)
    }

    @Suppress("UNUSED")
    fun getValueBoolean(KEY_NAME: String): Boolean {
        return sharedPreferences.getBoolean(KEY_NAME, false)
    }

    @Suppress("UNUSED")
    fun getValueStringSet(KEY_NAME: String): Set<String>? {
        return sharedPreferences.getStringSet(KEY_NAME, null)
    }


    // endregion

}
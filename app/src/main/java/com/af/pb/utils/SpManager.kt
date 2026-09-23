package com.af.pb.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.af.pb.App
import com.af.pb.R
import com.af.pb.data.model.Language

class SpManager(private val preferences: SharedPreferences) {
    companion object {
        private var instance: SpManager? = null

        fun getInstance(context: Context): SpManager {
            if (instance == null) {
                instance = SpManager(PreferenceManager.getDefaultSharedPreferences(context))
            }
            return instance!!
        }

        fun getInstance(): SpManager {
            if (instance == null) {
                instance = App.context?.let {
                    PreferenceManager.getDefaultSharedPreferences(
                        it
                    )
                }?.let { SpManager(it) }
            }
            return instance!!
        }
    }

    fun putBoolean(key: String, value: Boolean) {
        preferences.edit {
            putBoolean(key, value)
        }
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        preferences.getBoolean(key, defaultValue)

    fun getInt(key: String, defaultValue: Int): Int = preferences.getInt(key, defaultValue)

    fun putInt(key: String, value: Int) {
        preferences.edit {
            putInt(key, value)
        }
    }

    fun getLong(key: String, defaultValue: Long): Long =
        preferences.getLong(key, defaultValue)

    fun putLong(key: String, value: Long) {
        preferences.edit {
            putLong(key, value)
        }
    }

    fun getString(key: String, defaultValue: String): String? =
        preferences.getString(key, defaultValue)

    fun putString(key: String, value: String) {
        preferences.edit {
            putString(key, value)
        }
    }

    fun saveLanguage(language: Language) {
        preferences.edit {
            putString(Constant.KEY_SP_CURRENT_LANGUAGE, language.toJson())
        }
    }

    fun getLanguage(): Language {
        return preferences.getString(Constant.KEY_SP_CURRENT_LANGUAGE, "")?.toLanguageModel()
            ?: Language("en", R.string.english)
    }

    fun setLanguageChosen() {
        preferences.edit { putBoolean(Constant.KEY_SP_LANGUAGE_CHOSEN, true) }
    }

    fun isLanguageChosen(): Boolean {
        return preferences.getBoolean(Constant.KEY_SP_LANGUAGE_CHOSEN, false)
    }

    fun setPurchased(isPurchased: Boolean) {
        preferences.edit { putBoolean(Constant.KEY_SP_IS_PURCHASED, isPurchased) }
    }

    fun isPurchased(): Boolean {
        return preferences.getBoolean(Constant.KEY_SP_IS_PURCHASED, false)
    }

    fun getSelectedApps(): Set<String> {
        return preferences.getStringSet("key_selected_apps", null) ?: emptySet()
    }

    fun saveSelectedApps(packages: Set<String>) {
        preferences.edit {
            putStringSet("key_selected_apps", packages)
        }
    }

    fun isFlashAlertEnabled(type: Int): Boolean {
        return preferences.getBoolean("key_flash_alert_type_$type", false)
    }

    fun setFlashAlertEnabled(type: Int, enabled: Boolean) {
        preferences.edit {
            putBoolean("key_flash_alert_type_$type", enabled)
        }
    }

}
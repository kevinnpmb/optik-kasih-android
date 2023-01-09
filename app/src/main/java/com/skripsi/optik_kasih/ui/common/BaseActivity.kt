package com.skripsi.optik_kasih.ui.common

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.skripsi.optik_kasih.utils.PreferencesHelper
import com.skripsi.optik_kasih.utils.Utilities
import javax.inject.Inject


private const val TAG = "BaseActivity"

open class BaseActivity : AppCompatActivity() {
    @Inject
    lateinit var preferencesHelper: PreferencesHelper
    lateinit var loadingDialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadingDialog = Utilities.createLoadingDialog(this)
    }
}
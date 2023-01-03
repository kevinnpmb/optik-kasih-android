package com.skripsi.optik_kasih.utils

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.skripsi.optik_kasih.R
import com.skripsi.optik_kasih.databinding.ToastBinding
import com.skripsi.optik_kasih.vo.User

object Utilities {
    fun com.skripsi.optik_kasih.fragment.User.toUser(accessToken: String) = User(
        id,
        customer_name,
        customer_gender,
        customer_birthday.toString(),
        phone_number,
        customer_email,
        accessToken,
    )

    fun createLoadingDialog(activity: Activity): AlertDialog {
        val dialog = AlertDialog.Builder(activity, R.style.BackgroundTransparentDialog)
        dialog.setView(activity.layoutInflater.inflate(R.layout.loading_dialog, null))
        dialog.setCancelable(false)
        return dialog.create()
    }

    fun showToast(
        activity: Activity,
        view: View,
        text: String?,
        icon: Int = R.drawable.ic_warning,
        backgroundColor: Int = R.color.primaryColor,
        contentColor: Int = R.color.white,
        duration: Int = Snackbar.LENGTH_LONG,
    ) {
        val toastView = ToastBinding.inflate(activity.layoutInflater)
        val snackbar = Snackbar.make(view, "", duration)
        val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout

        val rBackgroundColor = ContextCompat.getColor(activity, backgroundColor)
        val rContentColor = ContextCompat.getColor(activity, contentColor)

        toastView.ivClose.setOnClickListener {
            snackbar.dismiss()
        }

        toastView.tvMain.text = text

        toastView.ivMain.setImageResource(icon)

        toastView.root.setCardBackgroundColor(rBackgroundColor)
        toastView.tvMain.setTextColor(rContentColor)
        toastView.ivMain.setColorFilter(rContentColor)
        toastView.ivClose.setColorFilter(rContentColor)

        snackbarLayout.setBackgroundColor(Color.TRANSPARENT)
        snackbarLayout.setPadding(8, 8, 8, 8)
        snackbarLayout.addView(toastView.root, 0)

        snackbar.show()
    }
}
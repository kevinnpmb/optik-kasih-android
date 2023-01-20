package com.skripsi.optik_kasih.utils

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.skripsi.optik_kasih.OptikKasihApp
import com.skripsi.optik_kasih.R
import com.skripsi.optik_kasih.SplashScreen
import com.skripsi.optik_kasih.databinding.ToastBinding
import com.skripsi.optik_kasih.fragment.Address
import com.skripsi.optik_kasih.fragment.Customer
import com.skripsi.optik_kasih.fragment.Product
import com.skripsi.optik_kasih.vo.AddressPref
import com.skripsi.optik_kasih.vo.Cart
import com.skripsi.optik_kasih.vo.ProductSnapshot
import com.skripsi.optik_kasih.vo.User
import com.squareup.picasso.Picasso
import java.io.IOException
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

object Utilities {
    fun Product.toCart(quantity: Int) = Cart(
        id, product_name, product_image, product_price, discount, product_description, quantity
    )

    fun Address.toAddressPref() = AddressPref(
        id, label, address, kecamatan, kelurahan, postal
    )

    fun AddressPref.toAddress(preferencesHelper: PreferencesHelper) = Address(
        id, label, address, kecamatan, kelurahan, postal, Address.Customer(
            "Customer",
            preferencesHelper.user!!.run {
                Customer(
                    id, name, gender, birthday, phone_number, email
                )
            }
        )
    )

    val Address.addressDetail
        get() = (if (address.isNotBlank()) "$address," else "") +
                (if (kelurahan.isNotBlank()) " $kelurahan," else "") +
                (if (kecamatan.isNotBlank()) " $kecamatan," else "") +
                if (postal.isNotBlank()) " $postal" else ""

    val AddressPref.addressDetail
        get() = (if (address.isNotBlank()) "$address," else "") +
                (if (kelurahan.isNotBlank()) " $kelurahan," else "") +
                (if (kecamatan.isNotBlank()) " $kecamatan," else "") +
                if (postal.isNotBlank()) " $postal" else ""

    fun com.skripsi.optik_kasih.fragment.Customer.toUser(accessToken: String) = User(
        id,
        customer_name,
        customer_gender,
        customer_birthday.toString(),
        phone_number,
        customer_email,
        accessToken,
    )

    val Number.dpToPx
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            Resources.getSystem().displayMetrics
        )

    fun TextInputLayout.validate(
        type: TextFieldType,
        errorText: String
    ): Boolean {
        when (type) {
            TextFieldType.EMAIL -> return if (!editText?.text.isNullOrBlank() && Patterns.EMAIL_ADDRESS.matcher(
                    editText?.text.toString()
                ).matches()
            ) {
                error = null
                true
            } else {
                error = errorText
                false
            }
            TextFieldType.PHONE -> return if (!editText?.text.isNullOrBlank() && Patterns.PHONE.matcher(
                    editText?.text.toString()
                ).matches()
            ) {
                error = null
                true
            } else {
                error = errorText
                false
            }
            TextFieldType.OTHER -> return if (!editText?.text.isNullOrBlank()) {
                error = null
                true
            } else {
                error = errorText
                false
            }
        }
    }

    fun TextInputLayout.registerClearText() {
        val clearTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                error = null
            }

            override fun afterTextChanged(p0: Editable?) {}
        }
        editText?.addTextChangedListener(clearTextChangedListener)
    }

    fun initToolbar(
        activity: AppCompatActivity,
        toolbar: MaterialToolbar,
        title: String? = null,
        hideBack: Boolean = false,
        hideCart: Boolean = true,
        onBackPressedListener: View.OnClickListener? = null
    ) {
        activity.setSupportActionBar(toolbar as Toolbar)
        toolbar.menu.findItem(R.id.cart).isVisible = !hideCart
        toolbar.setTitleTextColor(
            ContextCompat.getColor(
                activity,
                if (hideBack) R.color.primaryColor else R.color.black
            )
        )
        if (onBackPressedListener == null) {
            toolbar.setNavigationOnClickListener {
                activity.onBackPressedDispatcher.onBackPressed()
            }
        } else {
            toolbar.setNavigationOnClickListener(onBackPressedListener)
        }

        val actionBar = activity.supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(!hideBack)
            if (!hideBack) {
                toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back)
            }
            actionBar.title = title
        }
    }

    fun createLoadingDialog(activity: Activity): AlertDialog {
        val dialog = AlertDialog.Builder(activity, R.style.BackgroundTransparentDialog)
        dialog.setView(activity.layoutInflater.inflate(R.layout.loading_layout, null))
        dialog.setCancelable(false)
        return dialog.create()
    }

    fun showToast(
        activity: Activity,
        view: View,
        text: String?,
        type: ToastType = ToastType.OTHER,
        icon: Int = R.drawable.ic_warning,
        backgroundColor: Int = R.color.primaryColor,
        contentColor: Int = R.color.white,
        duration: Int = Snackbar.LENGTH_LONG,
    ) {
        val toastView = ToastBinding.inflate(activity.layoutInflater)
        val snackbar = Snackbar.make(view, "", duration)
        val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout

        val rBackgroundColor = ContextCompat.getColor(
            activity, when (type) {
                ToastType.ERROR -> R.color.danger
                ToastType.SUCCESS -> R.color.success
                ToastType.WARNING -> R.color.warning
                ToastType.OTHER -> backgroundColor
            }
        )
        val rContentColor = ContextCompat.getColor(activity, contentColor)

        toastView.ivClose.setOnClickListener {
            snackbar.dismiss()
        }

        toastView.tvMain.text = text

        toastView.ivMain.setImageResource(
            when (type) {
                ToastType.ERROR -> R.drawable.ic_error
                ToastType.SUCCESS -> R.drawable.ic_success
                ToastType.WARNING -> R.drawable.ic_warning
                ToastType.OTHER -> icon
            }
        )

        toastView.root.setCardBackgroundColor(rBackgroundColor)
        toastView.tvMain.setTextColor(rContentColor)
        toastView.ivMain.setColorFilter(rContentColor)
        toastView.ivClose.setColorFilter(rContentColor)

        snackbarLayout.setBackgroundColor(Color.TRANSPARENT)
        snackbarLayout.setPadding(8, 8, 8, 8)
        snackbarLayout.addView(toastView.root, 0)

        snackbar.show()
    }

    fun convertPrice(paramString: String?): String {
        try {
            val localBigDecimal = BigDecimal(paramString)
            val localDecimalFormatSymbols = DecimalFormatSymbols.getInstance()
            localDecimalFormatSymbols.groupingSeparator = '.'
            val dfnd = DecimalFormat("###,###.##", localDecimalFormatSymbols).format(
                localBigDecimal.toLong()
            )
            val currency = "Rp. "
            return currency + dfnd
        } catch (localException: Exception) {
            localException.printStackTrace()
        }
        return "Rp. 0"
    }

    fun showInvalidApiKeyAlert(activity: Activity) {
        val pref = PreferencesHelper(activity)
        pref.signOut()
        (activity.application as OptikKasihApp).setAccessTokenToHeader()
        val dialog: AlertDialog
        val builder = MaterialAlertDialogBuilder(activity, R.style.MaterialAlertDialog_Rounded)
        builder.setTitle(activity.resources.getString(R.string.alert))
            .setMessage(activity.resources.getString(R.string.unauthorized))
            .setCancelable(false)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton("OK") { dialogInterface, _ ->
//                    CoroutineScope(Dispatchers.IO).launch {
//                        database.clearAllTables()
//                    }
                val intent = Intent(activity, SplashScreen::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                activity.startActivity(intent)
                activity.finish()
                dialogInterface.dismiss()
            }
        dialog = builder.create()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    fun formatToDateISO8601(dateSrc: String): Date? {
        return try {
            Date.from(Instant.parse(dateSrc))
        } catch (e: java.lang.Exception) {
            null
        }
    }

    fun formatToDate(dateSrc: String): Date? {
        return try {
            val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("Asia/Jakarta")
            }
            df.parse(dateSrc)
        } catch (e: java.lang.Exception) {
            null
        }
    }

    fun showSimpleAlertDialog(
        activity: Activity,
        title: String?,
        msg: String?,
        positiveMessage: String = activity.getString(android.R.string.ok),
        positiveListener: DialogInterface.OnClickListener? = null,
        negativeMessage: String = activity.getString(android.R.string.cancel),
        negativeListener: DialogInterface.OnClickListener? = null,
        isCancelable: Boolean = true
    ) {
        val dialog: AlertDialog
        val builder = MaterialAlertDialogBuilder(activity, R.style.MaterialAlertDialog_Rounded)
        if (title != null) {
            builder.setTitle(title)
        }
        builder.setMessage(msg)
            .setCancelable(false)
            .setIcon(R.drawable.ic_launcher_foreground)
        if (positiveListener != null) {
            builder.setPositiveButton(positiveMessage, positiveListener)
        } else {
            builder.setPositiveButton(positiveMessage) { dialog, _ -> dialog.dismiss() }
        }
        if (negativeListener != null) {
            builder.setNegativeButton(negativeMessage, negativeListener)
        } else {
            builder.setNegativeButton(negativeMessage) { dialog, _ -> dialog.dismiss() }
        }
        dialog = builder.create()
        dialog.setCancelable(isCancelable)
        dialog.setCanceledOnTouchOutside(isCancelable)
        if (!activity.isFinishing) {
            dialog.show()
        }
    }

    fun formatISO8601ToDateString(dateSrc: String, dateType: DateType = DateType.API): String? {
        return try {
            val dateFormat = SimpleDateFormat(
                when (dateType) {
                    DateType.API -> "yyyy-MM-dd"
                    DateType.VIEW -> "EEEE, d MMMM yyyy"
                    DateType.SIMPLE -> "dd-MM-yyyy"
                }, Locale.getDefault()
            )
            dateFormat.format(Date.from(Instant.parse(dateSrc)))
        } catch (e: java.lang.Exception) {
            null
        }
    }

    fun formatToDateString(date: Date?, dateType: DateType = DateType.API): String? {
        return try {
            val dateFormat = SimpleDateFormat(
                when (dateType) {
                    DateType.API -> "yyyy-MM-dd"
                    DateType.VIEW -> "EEEE, d MMMM yyyy"
                    DateType.SIMPLE -> "dd-MM-yyyy"
                }, Locale.getDefault()
            )
            date?.let { dateFormat.format(date) }
        } catch (e: java.lang.Exception) {
            null
        }
    }

    fun loadImageUrl(url: String?, iv: ImageView) {
        if (url != null && url != "") {
            Picasso.get().load(url).error(R.drawable.ic_no_images).into(iv)
        } else {
            iv.setImageResource(R.drawable.ic_no_images)
        }
    }

    fun zeroPadding(id: Int) = (if (id < 10) "00"
    else {
        if (id < 100) "0" else ""
    }) + id

    fun getJsonDataFromAsset(context: Context, fileName: String): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }

    fun List<Boolean>.validateAll(callbackIfAllValid: () -> Unit) {
        if (all { it }) {
            callbackIfAllValid.invoke()
        }
    }

    inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
        SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }

    inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
        SDK_INT >= 33 -> getParcelable(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelable(key) as? T
    }

    enum class ToastType {
        ERROR, SUCCESS, WARNING, OTHER
    }

    enum class TextFieldType {
        EMAIL, PHONE, OTHER
    }

    enum class DateType {
        API, VIEW, SIMPLE
    }
}
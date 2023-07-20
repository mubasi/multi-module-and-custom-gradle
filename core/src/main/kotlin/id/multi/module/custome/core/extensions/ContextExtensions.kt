package id.multi.module.custome.core.extensions

import android.app.Activity
import android.content.Context
import android.content.pm.PackageInfo
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager

fun Context.hideSoftKeyboard(view: View) {
    val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

private fun Context.getPackageinfo():PackageInfo? {
    return try {
        this.packageManager.getPackageInfo(packageName, 0)
    } catch (e: Exception) {
        null
    }
}

fun Context.getVersionCode() :Long{
    return getPackageinfo()?.let {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            it.longVersionCode
        } else {
            it.versionCode.toLong()
        }
    } ?: kotlin.run{ -1 }
}
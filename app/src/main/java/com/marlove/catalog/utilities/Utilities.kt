package com.marlove.catalog.utilities

import android.content.Context
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment


object Utilities
{
    //show a toast message of long duration
    fun showLongToastMessage(context:Context, message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun Fragment.showLongToastMessage(message: String?) =
            showLongToastMessage(this.requireContext(), message)

    //show a toast message of short duration
    fun showShortToastMessage(context:Context, message: String?) =
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

    fun Fragment.showShortToastMessage(message: String?) =
        showShortToastMessage(this.requireContext(), message)


    //is the current thread the Main android thread?
    val Thread.isMain
        get() = Looper.getMainLooper().thread == Thread.currentThread()



    fun Fragment.initToolbar(title: String) {

        // initialize toolbar
        (getActivity() as AppCompatActivity).supportActionBar?.apply()
        {
            setDisplayShowTitleEnabled(true)
            setTitle(title)
            //setIcon(getActivity()?.getDrawable(R.drawable.main))
        }
        setHasOptionsMenu(false)
    }
}
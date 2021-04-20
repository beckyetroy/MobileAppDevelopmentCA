package org.wit.movie.activities

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import java.lang.IllegalStateException

class ConfirmationDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let{
            val alertBuilder = AlertDialog.Builder(it)

            alertBuilder.setTitle("Are you sure you want to remove this movie?")
            alertBuilder.setMessage("This action cannot be undone.")

            alertBuilder.setPositiveButton("OK", DialogInterface.OnClickListener{dialog, id ->
                Log.d("mydialoglog", "OK Pressed")
            }).setNegativeButton("Cancel", DialogInterface.OnClickListener{dialog, id ->
                Log.d("mydialoglog", "Cancel Pressed")
            })
            alertBuilder.create()
        } ?: throw IllegalStateException("Exception !! Activity is null !!")
    }
}
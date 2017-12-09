package de.interoberlin.lymbo.view.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.view.View
import android.widget.TextView
import de.interoberlin.lymbo.App
import de.interoberlin.lymbo.R
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class ConfirmationDialog : DialogFragment() {
    companion object {
        // val TAG = ConfirmationDialog::class.toString()
    }

    private var dialogTitle = ""
    private var text = ""
    private var action = ""
    private lateinit var value: Any

    val resultSubject: Subject<Any> = PublishSubject.create()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogTitle = arguments.getString(App.context.resources.getString(R.string.bundle_title))
        text = arguments.getString(App.context.resources.getString(R.string.bundle_text))
        action = arguments.getString(App.context.resources.getString(R.string.bundle_action))
        value = arguments.getString(App.context.resources.getString(R.string.bundle_value))

        val v = View.inflate(activity, R.layout.dialog_confirmation, null)
        val builder = AlertDialog.Builder(activity)
        builder.setView(v)
        builder.setTitle(dialogTitle)

        val tvText = v.findViewById(R.id.tvText) as TextView

        tvText.text = text

        builder.setPositiveButton(action, { _, _ ->
            resultSubject.onNext(value)
            dismiss()
        })

        builder.setNegativeButton(R.string.cancel, { _, _ -> dismiss() })

        return builder.create()
    }
}
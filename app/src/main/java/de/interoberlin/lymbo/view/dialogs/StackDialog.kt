package de.interoberlin.lymbo.view.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.google.gson.Gson
import de.interoberlin.lymbo.App
import de.interoberlin.lymbo.R
import de.interoberlin.lymbo.model.DialogType
import de.interoberlin.lymbo.model.Stack
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.*

class StackDialog : DialogFragment() {
    companion object {
        // val TAG = StackDialog::class.toString()
    }

    private var mode = DialogType.NONE
    private var dialogTitle = ""
    private var positiveButton = 0

    val stackAddSubject: Subject<Stack> = PublishSubject.create()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bundleStack = arguments.getString(App.context.resources.getString(R.string.bundle_stack))
        var stack = Gson().fromJson(bundleStack, Stack::class.java)

        if (stack != null) {
            mode = DialogType.UPDATE
            dialogTitle = App.context.resources.getString(R.string.update_stack)
            positiveButton = R.string.update_stack
        } else {
            mode = DialogType.ADD
            dialogTitle = App.context.resources.getString(R.string.add_stack)
            positiveButton = R.string.add_stack

            stack = Stack()
            stack.id = UUID.randomUUID().toString()
        }

        val v = View.inflate(activity, R.layout.dialog_stack, null)
        val builder = AlertDialog.Builder(activity)
        builder.setView(v)
        builder.setTitle(dialogTitle)

        val etTitle = v.findViewById(R.id.etTitle) as EditText

        etTitle.setText(stack.title)

        builder.setPositiveButton(positiveButton, { _, _ ->
            val title = etTitle.text.toString().trim { it <= ' ' }

            when {
                title.isEmpty() -> etTitle.error = activity.resources.getString(R.string.field_must_not_be_empty)
                else -> {
                    stack.title = title

                    stackAddSubject.onNext(stack)
                    dismiss()
                }
            }
        })

        builder.setNegativeButton(R.string.cancel, { _, _ -> dismiss() })

        return builder.create()
    }
}
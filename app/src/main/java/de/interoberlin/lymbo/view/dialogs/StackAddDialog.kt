package de.interoberlin.lymbo.view.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.view.View
import android.widget.EditText
import de.interoberlin.lymbo.R
import de.interoberlin.lymbo.model.Stack
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.*

class StackAddDialog : DialogFragment() {
    companion object {
        // val TAG = StackAddDialog::class.toString()
    }

    val dialogTitle = "Add Stack"
    val stackAddSubject: Subject<Stack> = PublishSubject.create()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val v = View.inflate(activity, R.layout.dialog_stack_add, null)

        val builder = AlertDialog.Builder(activity)
        builder.setView(v)
        builder.setTitle(dialogTitle)

        builder.setPositiveButton(R.string.lbl_add_stack, { dialog, _ ->
            val view = dialog as AlertDialog
            val etTitle = view.findViewById(R.id.etTitle) as EditText

            val title = etTitle.text.toString().trim { it <= ' ' }

            when {
                title.isEmpty() -> etTitle.error = activity.resources.getString(R.string.msg_field_must_not_be_empty)
                else -> {
                    val stack = Stack()

                    stack.id = UUID.randomUUID().toString()
                    stack.title = title

                    stackAddSubject.onNext(stack)
                    dismiss()
                }
            }
        })

        builder.setNegativeButton(R.string.lbl_cancel, { _, _ -> dismiss() })

        return builder.create()
    }
}
package de.interoberlin.lymbo.view.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ListView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.interoberlin.lymbo.App
import de.interoberlin.lymbo.R
import de.interoberlin.lymbo.model.DialogType
import de.interoberlin.lymbo.model.Stack
import de.interoberlin.lymbo.model.Tag
import de.interoberlin.lymbo.view.adapters.EditableTagsArrayAdapter
import de.interoberlin.lymbo.view.adapters.SelectableTagsArrayAdapter
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.*

class StackDialog : DialogFragment() {
    companion object {
        val TAG = StackDialog::class.toString()
    }

    private var mode = DialogType.NONE
    private var dialogTitle = ""
    private var positiveButton = 0

    val stackAddSubject: Subject<Stack> = PublishSubject.create()

    inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object : TypeToken<T>() {}.type)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bundleStack = arguments.getString(App.context.resources.getString(R.string.bundle_stack))
        val bundleTags = arguments.getString(App.context.resources.getString(R.string.bundle_tags))

        var stack = Gson().fromJson(bundleStack, Stack::class.java)
        val existingTags = Gson().fromJson<MutableList<Tag>>(bundleTags)
        val newTags: MutableList<Tag> = ArrayList()

        if (stack != null) {
            mode = DialogType.UPDATE
            dialogTitle = App.context.resources.getString(R.string.update_stack)
            positiveButton = R.string.update_stack

            // Check tags that the card contains
            existingTags.forEach { it.checked = false }
            existingTags.forEach { et ->
                stack.tags.forEach { t ->
                    if (et.value == t.value) {
                        et.checked = true
                    }
                }
            }
        } else {
            mode = DialogType.ADD
            dialogTitle = App.context.resources.getString(R.string.add_stack)
            positiveButton = R.string.add_stack

            stack = Stack()
            stack.id = UUID.randomUUID().toString()

            existingTags.forEach { it.checked = false }
        }

        val v = View.inflate(activity, R.layout.dialog_stack, null)
        val builder = AlertDialog.Builder(activity)
        builder.setView(v)
        builder.setTitle(dialogTitle)

        val selectableTagsAdapter = SelectableTagsArrayAdapter(activity, R.layout.selectable_tag, existingTags)
        val editableTagsAdapter = EditableTagsArrayAdapter(activity, R.layout.editable_tag, newTags)

        val etTitle = v.findViewById(R.id.etTitle) as EditText
        val lvSelectableTags = v.findViewById(R.id.lvSelectableTags) as ListView
        val lvEditableTags = v.findViewById(R.id.lvEditableTags) as ListView

        etTitle.setText(stack.title)
        lvSelectableTags.adapter = selectableTagsAdapter
        lvEditableTags.adapter = editableTagsAdapter

        builder.setPositiveButton(positiveButton, { _, _ ->
            val title = etTitle.text.toString().trim { it <= ' ' }
            val tags = existingTags.filter({ it.checked })
                    .plus(newTags.filter({ it.checked }))
                    .toMutableList()
            when {
                title.isEmpty() -> etTitle.error = activity.resources.getString(R.string.field_must_not_be_empty)
                else -> {
                    stack.title = title
                    stack.tags = tags

                    if (mode == DialogType.ADD) {
                        stack.fileName = title.toLowerCase().replace(" ", "_").plus(".lymbo")
                    }

                    stackAddSubject.onNext(stack)
                    dismiss()
                }
            }
        })

        builder.setNegativeButton(R.string.cancel, { _, _ -> dismiss() })

        return builder.create()
    }
}
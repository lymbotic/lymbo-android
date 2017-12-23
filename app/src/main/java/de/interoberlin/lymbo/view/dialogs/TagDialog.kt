package de.interoberlin.lymbo.view.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.view.View
import android.widget.ListView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.interoberlin.lymbo.App
import de.interoberlin.lymbo.R
import de.interoberlin.lymbo.model.Tag
import de.interoberlin.lymbo.view.adapters.SelectableTagsArrayAdapter
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class TagDialog : DialogFragment() {
    companion object {
        val TAG = TagDialog::class.toString()
    }

    private var dialogTitle = App.context.resources.getString(R.string.select_tags)
    private var positiveButton = R.string.select_tags

    val tagsSelectedSubject: Subject<MutableList<Tag>> = PublishSubject.create()

    inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object : TypeToken<T>() {}.type)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bundleTags = arguments.getString(App.context.resources.getString(R.string.bundle_tags))
        val tags = Gson().fromJson<MutableList<Tag>>(bundleTags)

        val v = View.inflate(activity, R.layout.dialog_tag, null)
        val builder = AlertDialog.Builder(activity)
        builder.setView(v)
        builder.setTitle(dialogTitle)

        val selectableTagsAdapter = SelectableTagsArrayAdapter(activity, R.layout.selectable_tag, tags)
        val lvSelectableTags = v.findViewById(R.id.lvSelectableTags) as ListView

        lvSelectableTags.adapter = selectableTagsAdapter

        builder.setPositiveButton(positiveButton, { _, _ ->
            tagsSelectedSubject.onNext(tags)
            dismiss()
        })

        builder.setNegativeButton(R.string.cancel, { _, _ -> dismiss() })

        return builder.create()
    }
}
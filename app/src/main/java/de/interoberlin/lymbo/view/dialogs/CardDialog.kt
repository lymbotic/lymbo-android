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
import de.interoberlin.lymbo.model.Card
import de.interoberlin.lymbo.model.DialogType
import de.interoberlin.lymbo.model.Side
import de.interoberlin.lymbo.model.Tag
import de.interoberlin.lymbo.view.adapters.EditableTagsArrayAdapter
import de.interoberlin.lymbo.view.adapters.SelectableTagsArrayAdapter
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.*
import kotlin.collections.ArrayList

class CardDialog : DialogFragment() {
    companion object {
        val TAG = CardDialog::class.toString()
    }

    private var mode = DialogType.NONE
    private var dialogTitle = ""
    private var positiveButton = 0

    val cardAddSubject: Subject<Card> = PublishSubject.create()

    inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object : TypeToken<T>() {}.type)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bundleCard = arguments.getString(App.context.resources.getString(R.string.bundle_card))
        val bundleTags = arguments.getString(App.context.resources.getString(R.string.bundle_tags))

        var card = Gson().fromJson(bundleCard, Card::class.java)
        val existingTags = Gson().fromJson<MutableList<Tag>>(bundleTags)
        val newTags: MutableList<Tag> = ArrayList()

        if (card != null) {
            mode = DialogType.UPDATE
            dialogTitle = App.context.resources.getString(R.string.update_card)
            positiveButton = R.string.update_card

            // Check tags that the card contains
            existingTags.forEach { it.checked = false }
            existingTags.forEach { et ->
                card.tags.forEach { t ->
                    if (et.value == t.value) {
                        et.checked = true
                    }
                }
            }
        } else {
            mode = DialogType.ADD
            dialogTitle = App.context.resources.getString(R.string.add_card)
            positiveButton = R.string.add_card

            card = Card()
            card.id = UUID.randomUUID().toString()
            card.sides.add(Side())
            card.sides.add(Side())

            existingTags.forEach { it.checked = false }
        }

        val v = View.inflate(activity, R.layout.dialog_card, null)
        val builder = AlertDialog.Builder(activity)
        builder.setView(v)
        builder.setTitle(dialogTitle)

        val selectableTagsAdapter = SelectableTagsArrayAdapter(activity, R.layout.selectable_tag, existingTags)
        val editableTagsAdapter = EditableTagsArrayAdapter(activity, R.layout.editable_tag, newTags)

        val etFrontTitle = v.findViewById(R.id.etFrontTitle) as EditText
        val etBackTitle = v.findViewById(R.id.etBackTitle) as EditText
        val lvSelectableTags = v.findViewById(R.id.lvSelectableTags) as ListView
        val lvEditableTags = v.findViewById(R.id.lvEditableTags) as ListView

        etFrontTitle.setText(card.sides[0].title)
        etBackTitle.setText(card.sides[1].title)
        lvSelectableTags.adapter = selectableTagsAdapter
        lvEditableTags.adapter = editableTagsAdapter

        builder.setPositiveButton(positiveButton, { _, _ ->
            val frontTitle = etFrontTitle.text.toString().trim { it <= ' ' }
            val backTitle = etBackTitle.text.toString().trim { it <= ' ' }
            val tags = existingTags.filter({ it.checked })
                    .plus(newTags.filter({ it.checked }))
                    .toMutableList()

            when {
                frontTitle.isEmpty() -> etFrontTitle.error = activity.resources.getString(R.string.field_must_not_be_empty)
                backTitle.isEmpty() -> etBackTitle.error = activity.resources.getString(R.string.field_must_not_be_empty)
                else -> {
                    card.sides[0].title = frontTitle
                    card.sides[1].title = backTitle
                    card.tags = tags

                    cardAddSubject.onNext(card)
                    dismiss()
                }
            }
        })

        builder.setNegativeButton(R.string.cancel, { _, _ -> dismiss() })

        return builder.create()
    }
}
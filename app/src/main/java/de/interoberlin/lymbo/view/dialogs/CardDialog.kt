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
import de.interoberlin.lymbo.model.Card
import de.interoberlin.lymbo.model.DialogType
import de.interoberlin.lymbo.model.Side
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.*

class CardDialog : DialogFragment() {
    companion object {
        val TAG = CardDialog::class.toString()
    }

    private var mode = DialogType.NONE
    private var dialogTitle = ""
    private var positiveButton = 0

    val cardAddSubject: Subject<Card> = PublishSubject.create()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bundleCard = arguments.getString(App.context.resources.getString(R.string.bundle_card))
        var card = Gson().fromJson(bundleCard, Card::class.java)

        if (card != null) {
            mode = DialogType.UPDATE
            dialogTitle = App.context.resources.getString(R.string.update_card)
            positiveButton = R.string.lbl_update_card
        } else {
            mode = DialogType.ADD
            dialogTitle = App.context.resources.getString(R.string.add_card)
            positiveButton = R.string.lbl_add_card

            card = Card()
            card.id = UUID.randomUUID().toString()
            card.sides.add(Side())
            card.sides.add(Side())
        }

        val v = View.inflate(activity, R.layout.dialog_card_add, null)
        val builder = AlertDialog.Builder(activity)
        builder.setView(v)
        builder.setTitle(dialogTitle)

        val etFrontTitle = v.findViewById(R.id.etFrontTitle) as EditText
        val etBackTitle = v.findViewById(R.id.etBackTitle) as EditText

        etFrontTitle.setText(card.sides[0].title)
        etBackTitle.setText(card.sides[1].title)

        builder.setPositiveButton(positiveButton, { _, _ ->
            val frontTitle = etFrontTitle.text.toString().trim { it <= ' ' }
            val backTitle = etBackTitle.text.toString().trim { it <= ' ' }

            when {
                frontTitle.isEmpty() -> etFrontTitle.error = activity.resources.getString(R.string.msg_field_must_not_be_empty)
                backTitle.isEmpty() -> etBackTitle.error = activity.resources.getString(R.string.msg_field_must_not_be_empty)
                else -> {
                    card.sides[0].title = frontTitle
                    card.sides[1].title = backTitle

                    cardAddSubject.onNext(card)
                    dismiss()
                }
            }
        })

        builder.setNegativeButton(R.string.lbl_cancel, { _, _ -> dismiss() })

        return builder.create()
    }
}
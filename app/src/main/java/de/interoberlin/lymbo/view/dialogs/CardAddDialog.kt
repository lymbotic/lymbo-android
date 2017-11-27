package de.interoberlin.lymbo.view.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.view.View
import android.widget.EditText
import de.interoberlin.lymbo.R
import de.interoberlin.lymbo.model.Card
import de.interoberlin.lymbo.model.Side
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.*

class CardAddDialog : DialogFragment() {
    companion object {
        val TAG = CardAddDialog::class.toString()
    }

    val title = "Add card"
    val cardAddSubject: Subject<Card> = PublishSubject.create()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val v = View.inflate(activity, R.layout.dialog_card_add, null)

        val builder = AlertDialog.Builder(activity)
        builder.setView(v)
        builder.setTitle(title)

        builder.setPositiveButton(R.string.lbl_add_card, { dialog, _ ->
            val view = dialog as AlertDialog
            val etFrontTitle = view.findViewById(R.id.etFrontTitle) as EditText
            val etBackTitle = view.findViewById(R.id.etBackTitle) as EditText

            val frontTitle = etFrontTitle.text.toString().trim { it <= ' ' }
            val backTitle = etBackTitle.text.toString().trim { it <= ' ' }

            when {
                frontTitle.isEmpty() -> etFrontTitle.error = activity.resources.getString(R.string.msg_field_must_not_be_empty)
                backTitle.isEmpty() -> etBackTitle.error = activity.resources.getString(R.string.msg_field_must_not_be_empty)
                else -> {
                    val card = Card()
                    val front = Side()
                    val back = Side()

                    front.title = frontTitle
                    back.title = backTitle

                    card.id = UUID.randomUUID().toString()
                    card.sides.add(front)
                    card.sides.add(back)

                    cardAddSubject.onNext(card)
                    dismiss()
                }
            }
        })

        builder.setNegativeButton(R.string.lbl_cancel, { _, _ -> dismiss() })

        return builder.create()
    }
}
package de.interoberlin.lymbo.view.adapters

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import com.google.gson.Gson
import de.interoberlin.lymbo.App.Companion.context
import de.interoberlin.lymbo.R
import de.interoberlin.lymbo.controller.CardsController
import de.interoberlin.lymbo.model.Card
import de.interoberlin.lymbo.model.Tag
import de.interoberlin.lymbo.view.components.TagView
import de.interoberlin.lymbo.view.dialogs.CardDialog
import de.interoberlin.lymbo.view.dialogs.ConfirmationDialog
import de.interoberlin.lymbo.view.helper.ItemTouchHelperAdapter


class CardsRecyclerViewAdapter(items: MutableList<Card>) :
        RecyclerView.Adapter<CardsRecyclerViewAdapter.ViewHolder>(),
        ItemTouchHelperAdapter,
        Filterable {
    companion object {
        val TAG = CardsRecyclerViewAdapter::class.toString()
        val controller = CardsController.instance
    }

    private var originalItems: MutableList<Card> = ArrayList()
    private var filteredList: MutableList<Card> = ArrayList()
    private var vi: LayoutInflater

    private var activeSideIndex: Int = 0

    init {
        this.originalItems = items
        this.filteredList = items
        this.vi = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        this.filter.filter("")
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var view: View? = null

        // Background
        var rlBackground: RelativeLayout? = null

        // Foreground
        var rlForeground: RelativeLayout? = null
        var rlContent: RelativeLayout? = null

        var llTags: LinearLayout? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card, parent, false)
        val rlBackground = view.findViewById(R.id.rlBackground) as RelativeLayout
        val rlForeground = view.findViewById(R.id.rlForeground) as RelativeLayout
        val rlContent = view.findViewById(R.id.rlContent) as RelativeLayout
        val llTags = view.findViewById(R.id.llTags) as LinearLayout

        val holder = ViewHolder(view)
        holder.view = view
        holder.rlBackground = rlBackground
        holder.rlForeground = rlForeground
        holder.rlContent = rlContent
        holder.llTags = llTags

        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val card = filteredList[position]

        holder.rlContent?.setOnCreateContextMenuListener { contextMenu: ContextMenu, _, _ ->
            contextMenu.add(0, 0, 0, context.resources.getString(R.string.edit))
                    .setOnMenuItemClickListener { _ ->
                        val dialog = CardDialog()
                        val bundle = Bundle()
                        bundle.putString(context.resources.getString(R.string.bundle_card), Gson().toJson(card))
                        bundle.putString(context.resources.getString(R.string.bundle_tags), Gson().toJson(controller.tags))
                        dialog.arguments = bundle
                        dialog.isCancelable = false
                        dialog.cardAddSubject.subscribe { card ->
                            controller.updateCard(position, card)
                        }
                        dialog.show((holder.view?.context as Activity).fragmentManager, CardDialog.TAG)
                        false
                    }
            contextMenu.add(0, 0, 1, context.resources.getString(R.string.delete))
                    .setOnMenuItemClickListener { _ ->
                        val dialog = ConfirmationDialog()
                        val bundle = Bundle()
                        bundle.putString(context.resources.getString(R.string.bundle_title), context.resources.getString(R.string.delete_card))
                        bundle.putString(context.resources.getString(R.string.bundle_text), context.resources.getString(R.string.delete_card_question))
                        bundle.putString(context.resources.getString(R.string.bundle_action), context.resources.getString(R.string.delete))
                        bundle.putString(context.resources.getString(R.string.bundle_value), Gson().toJson(card))
                        dialog.arguments = bundle
                        dialog.isCancelable = false
                        dialog.resultSubject.subscribe { result ->
                            if (result != null)
                                controller.deleteCard(position)
                        }
                        dialog.show((holder.view?.context as Activity).fragmentManager, CardDialog.TAG)
                        false
                    }
        }
        holder.rlContent?.removeAllViews()

        card.sides.forEach { s ->
            val li = LayoutInflater.from(context)
            val llSide = li.inflate(R.layout.side, holder.rlContent, false) as LinearLayout
            val tvTitle = llSide.findViewById(R.id.tvTitle) as TextView

            llSide.visibility = INVISIBLE
            tvTitle.text = s.title
            holder.rlContent?.addView(llSide)
        }

        holder.rlContent?.getChildAt(activeSideIndex)?.visibility = VISIBLE
        holder.rlContent?.setOnClickListener { this.flipCard(card, holder) }
        holder.view?.setOnClickListener { this.flipCard(card, holder) }

        holder.llTags?.removeAllViews()
        card.tags.forEach { t ->
            holder.llTags?.addView(TagView(context, t))
        }
    }

    override fun getItemCount(): Int = filteredList.size

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Log.i(TAG, "onItemMove")
        return true
    }

    override fun onItemDismiss(viewHolder: RecyclerView.ViewHolder, position: Int, direction: Int) {
        Log.i(TAG, "onItemDismiss")

        if (viewHolder is ViewHolder) {
            if (direction == ItemTouchHelper.START) {
                controller.putCardToEnd(position, filteredList[position])
            } else if (direction == ItemTouchHelper.END) {
                controller.putCardAside(position, filteredList[position])
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = Filter.FilterResults()

                val filtered: MutableList<Card> = ArrayList()
                originalItems.forEach { c ->
                    if (!c.checked && matchesTags(c, controller.tags))
                        filtered.add(c)
                }

                results.values = filtered
                results.count = filtered.size

                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as MutableList<Card>
                controller.cardsSubject.onNext(0)
            }
        }
    }

    private fun matchesTags(card: Card, tags: MutableList<Tag>): Boolean {
        if (card.tags.isEmpty()) return true

        tags.filter({ it.checked }).forEach { t ->
            card.tags.forEach { ct ->
                if (t.value == ct.value) {
                    return true
                }
            }
        }

        return false
    }

    /**
     * Applies filter using a constraint
     *
     * @param constraint constraint
     */
    fun applyFilter(constraint: String) {
        filter.filter(constraint)
    }

    private fun flipCard(card: Card, holder: ViewHolder) {
        holder.rlContent?.getChildAt(activeSideIndex)?.visibility = INVISIBLE

        this.activeSideIndex++
        if (this.activeSideIndex >= card.sides.size) {
            this.activeSideIndex = 0
        }

        holder.rlContent?.getChildAt(activeSideIndex)?.visibility = VISIBLE
    }
}
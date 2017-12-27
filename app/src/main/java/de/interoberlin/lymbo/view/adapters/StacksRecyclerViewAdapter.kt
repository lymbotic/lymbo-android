package de.interoberlin.lymbo.view.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.LinearLayout
import android.widget.TextView
import com.google.gson.Gson
import de.interoberlin.lymbo.App.Companion.context
import de.interoberlin.lymbo.R
import de.interoberlin.lymbo.controller.CardsController
import de.interoberlin.lymbo.controller.StacksController
import de.interoberlin.lymbo.model.Stack
import de.interoberlin.lymbo.model.Tag
import de.interoberlin.lymbo.view.activities.CardsActivity
import de.interoberlin.lymbo.view.components.TagView
import de.interoberlin.lymbo.view.dialogs.CardDialog
import de.interoberlin.lymbo.view.dialogs.ConfirmationDialog
import de.interoberlin.lymbo.view.dialogs.StackDialog

class StacksRecyclerViewAdapter(items: MutableList<Stack>) :
        RecyclerView.Adapter<StacksRecyclerViewAdapter.ViewHolder>(),
        Filterable {
    companion object {
        // val TAG = StacksRecyclerViewAdapter::class.toString()
        val controller = StacksController.instance
        val cardsController = CardsController.instance
    }

    private var originalItems: MutableList<Stack> = ArrayList()
    private var filteredList: MutableList<Stack> = ArrayList()
    private var vi: LayoutInflater

    init {
        this.originalItems = items
        this.filteredList = items
        this.vi = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        this.filter.filter("")
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var view: LinearLayout = itemView as LinearLayout
        var tvTitle: TextView? = null
        var tvSubTitle: TextView? = null

        var llTags: LinearLayout? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.stack, parent, false)
        val tvTitle = view.findViewById(R.id.tvTitle) as TextView
        val tvSubTitle = view.findViewById(R.id.tvSubTitle) as TextView
        val llTags = view.findViewById(R.id.llTags) as LinearLayout

        val holder = ViewHolder(view)
        holder.tvTitle = tvTitle
        holder.tvSubTitle = tvSubTitle
        holder.llTags = llTags

        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val stack = filteredList[position]

        holder.view.setOnCreateContextMenuListener { contextMenu: ContextMenu, _, _ ->
            if (!stack.fileName.isEmpty()) {
                contextMenu.add(0, 0, 0, context.resources.getString(R.string.delete))
                        .setOnMenuItemClickListener { _ ->
                            val dialog = ConfirmationDialog()
                            val bundle = Bundle()
                            bundle.putString(context.resources.getString(R.string.bundle_title), context.resources.getString(R.string.delete_stack))
                            bundle.putString(context.resources.getString(R.string.bundle_text), context.resources.getString(R.string.delete_stack_question))
                            bundle.putString(context.resources.getString(R.string.bundle_action), context.resources.getString(R.string.delete))
                            bundle.putString(context.resources.getString(R.string.bundle_value), Gson().toJson(stack))
                            dialog.arguments = bundle
                            dialog.isCancelable = false
                            dialog.resultSubject.subscribe { result ->
                                if (result != null)
                                    controller.deleteStack(position, stack)
                            }
                            dialog.show((holder.view.context as Activity).fragmentManager, CardDialog.TAG)
                            false
                        }
            }

            if (!stack.fileName.isEmpty()) {
                contextMenu.add(0, 0, 1, context.resources.getString(R.string.edit))
                        .setOnMenuItemClickListener { _ ->
                            val dialog = StackDialog()
                            val bundle = Bundle()
                            bundle.putString(context.resources.getString(R.string.bundle_stack), Gson().toJson(stack))
                            bundle.putString(context.resources.getString(R.string.bundle_tags), Gson().toJson(controller.tags))
                            dialog.arguments = bundle
                            dialog.isCancelable = false
                            dialog.stackAddSubject.subscribe { stack ->
                                controller.updateStack(position, stack)
                            }
                            dialog.show((holder.view.context as Activity).fragmentManager, CardDialog.TAG)
                            false
                        }
            }
        }

        holder.view.setOnClickListener({
            cardsController.stack = filteredList[position]
            cardsController.cards = filteredList[position].cards

            val activity = Intent(context, CardsActivity::class.java)
            context.startActivity(activity)
        })
        holder.tvTitle?.text = stack.title
        holder.tvSubTitle?.text = "${stack.cards.size} cards"

        holder.llTags?.removeAllViews()
        stack.tags.forEach { t ->
            holder.llTags?.addView(TagView(context, t))
        }
    }

    override fun getItemCount(): Int = filteredList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = Filter.FilterResults()

                val filtered: MutableList<Stack> = ArrayList()
                originalItems.forEach { s ->
                    if (matchesTags(s, controller.tags))
                        filtered.add(s)
                }

                results.values = filtered
                results.count = filtered.size

                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as MutableList<Stack>
                controller.stacksSubject.onNext(0)
            }
        }
    }

    private fun matchesTags(stack: Stack, tags: MutableList<Tag>): Boolean {
        if (stack.tags.isEmpty()) return true

        tags.filter({ it.checked }).forEach { t ->
            stack.tags.forEach { st ->
                if (t.value == st.value) {
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
}
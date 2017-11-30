package de.interoberlin.lymbo.view.adapters

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import de.interoberlin.lymbo.App.Companion.context
import de.interoberlin.lymbo.R
import de.interoberlin.lymbo.controller.CardsController
import de.interoberlin.lymbo.model.Stack
import de.interoberlin.lymbo.view.activities.CardsActivity


class StacksRecyclerViewAdapter(items: MutableList<Stack>) : RecyclerView.Adapter<StacksRecyclerViewAdapter.ViewHolder>() {
    companion object {
        // val TAG = StacksRecyclerViewAdapter::class.toString()
        val controller = CardsController.instance
    }

    private var items: MutableList<Stack> = ArrayList()
    private var vi: LayoutInflater

    init {
        this.items = items
        this.vi = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var view: LinearLayout = itemView as LinearLayout
        var tvTitle: TextView? = null
        var tvSubTitle: TextView? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.stack, parent, false)
        val tvTitle = view.findViewById(R.id.tvTitle) as TextView
        val tvSubTitle = view.findViewById(R.id.tvSubTitle) as TextView

        val holder = ViewHolder(view)
        holder.tvTitle = tvTitle
        holder.tvSubTitle = tvSubTitle

        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val stack = items[position]

        holder.view.setOnClickListener({ _ ->
            controller.stackTitle = items[position].title
            controller.cards = items[position].cards

            val activity = Intent(context, CardsActivity::class.java)
            context.startActivity(activity)
        })
        holder.tvTitle?.text = stack.title
        holder.tvSubTitle?.text = "${stack.cards.size} cards"
    }

    override fun getItemCount(): Int = items.size
}
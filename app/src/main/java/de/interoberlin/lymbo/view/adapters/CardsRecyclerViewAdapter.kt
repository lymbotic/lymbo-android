package de.interoberlin.lymbo.view.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import de.interoberlin.lymbo.App.Companion.context
import de.interoberlin.lymbo.R
import de.interoberlin.lymbo.model.Card


class CardsRecyclerViewAdapter(items: MutableList<Card>) : RecyclerView.Adapter<CardsRecyclerViewAdapter.ViewHolder>() {
    companion object {
        // val TAG = CardsRecyclerViewAdapter::class.toString()
        // val controller = CardsController.instance
    }

    private var items: MutableList<Card> = ArrayList()
    private var vi: LayoutInflater

    private var activeSideIndex = 0

    init {
        this.items = items
        this.vi = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var rlContent: RelativeLayout? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card, parent, false)
        val rlContent = view.findViewById(R.id.rlContent) as RelativeLayout

        val holder = ViewHolder(view)
        holder.rlContent = rlContent

        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val card = items[position]

        card.sides.forEach { s ->
            val li = LayoutInflater.from(context)
            val llSide = li.inflate(R.layout.side, holder.rlContent, false) as LinearLayout
            val tvTitle = llSide.findViewById(R.id.tvTitle) as TextView

            llSide.visibility = INVISIBLE
            tvTitle.text = s.title
            holder.rlContent?.addView(llSide)
        }

        holder.rlContent?.getChildAt(activeSideIndex)?.visibility = VISIBLE
        holder.rlContent?.setOnClickListener { _ ->
            this.flipCard(card, holder)
        }
    }

    override fun getItemCount(): Int = items.size

    private fun flipCard(card: Card, holder: ViewHolder) {
        holder.rlContent?.getChildAt(activeSideIndex)?.visibility = INVISIBLE

        this.activeSideIndex++
        if (this.activeSideIndex >= card.sides.size) {
            this.activeSideIndex = 0
        }

        holder.rlContent?.getChildAt(activeSideIndex)?.visibility = VISIBLE
    }
}
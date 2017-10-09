package de.interoberlin.lymbo.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import de.interoberlin.lymbo.R
import de.interoberlin.lymbo.model.Card

class CardsListAdapter
(context: Context, private var resource: Int, items: List<Card>) : ArrayAdapter<Card>(context, resource, items) {
    private var items: List<Card> = ArrayList()
    private var vi: LayoutInflater

    private var activeSideIndex = 0

    init {
        this.items = items
        this.vi = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    internal class ViewHolder {
        var rlContent: RelativeLayout? = null
        var position: Int = 0
    }

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): Card = items[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val card = getItem(position)
        val holder: ViewHolder
        val retView: View

        if (convertView == null) {
            retView = vi.inflate(resource, null)

            holder = ViewHolder()
            holder.rlContent = retView.findViewById(R.id.rlContent) as RelativeLayout

            card.sides.forEach { s ->
                val li = LayoutInflater.from(context)
                val llSide = li.inflate(R.layout.side, parent, false) as LinearLayout
                val tvTitle = llSide.findViewById(R.id.tvTitle) as TextView

                llSide.visibility = INVISIBLE
                tvTitle.text = s.title
                holder.rlContent?.addView(llSide)
            }

            holder.rlContent?.getChildAt(activeSideIndex)?.visibility = VISIBLE
            holder.position = position

            retView.tag = holder
            retView.setOnClickListener { _ ->
                this.flipCard(card, holder)
            }
        } else {
            // holder = convertView.tag as ViewHolder
            retView = convertView
        }

        return retView
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
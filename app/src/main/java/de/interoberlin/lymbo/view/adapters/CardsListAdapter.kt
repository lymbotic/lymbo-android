package de.interoberlin.lymbo.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import de.interoberlin.lymbo.R
import de.interoberlin.lymbo.model.Card

class CardsListAdapter
(context: Context, private var resource: Int, items: List<Card>) : ArrayAdapter<Card>(context, resource, items) {
    private var items: List<Card> = ArrayList()
    private var vi: LayoutInflater

    init {
        this.items = items
        this.vi = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    internal class ViewHolder {
        var tvTitle: TextView? = null
        var position: Int = 0
    }

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): Card? = items[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val card = getItem(position)
        val holder: ViewHolder
        val retView: View

        if (convertView == null) {
            retView = vi.inflate(resource, null)
            holder = ViewHolder()

            holder.tvTitle = retView.findViewById(R.id.tvTitle) as TextView
            holder.tvTitle!!.text = card!!.title
            holder.position = position

            retView.tag = holder
        } else {
            // holder = convertView.tag as ViewHolder
            retView = convertView
        }

        return retView
    }
}
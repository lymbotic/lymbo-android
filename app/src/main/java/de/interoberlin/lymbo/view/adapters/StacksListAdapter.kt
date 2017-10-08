package de.interoberlin.lymbo.view.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import de.interoberlin.lymbo.R
import de.interoberlin.lymbo.controller.CardsController
import de.interoberlin.lymbo.model.Stack
import de.interoberlin.lymbo.view.activities.CardsActivity

class StacksListAdapter
(context: Context, private var resource: Int, items: List<Stack>) : ArrayAdapter<Stack>(context, resource, items) {
    private var items: List<Stack> = ArrayList()
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

    override fun getItem(position: Int): Stack? = items[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val stack = getItem(position)
        val holder: ViewHolder
        val retView: View

        if (convertView == null) {
            retView = vi.inflate(resource, null)
            holder = ViewHolder()

            holder.tvTitle = retView.findViewById(R.id.tvTitle) as TextView
            holder.tvTitle!!.text = stack!!.title
            (holder.tvTitle as TextView).setOnClickListener({ _ ->
                CardsController.instance.stack = stack

                val activity = Intent(context, CardsActivity::class.java)
                context.startActivity(activity)
            })
            holder.position = position

            retView.tag = holder
        } else {
            // holder = convertView.tag as ViewHolder
            retView = convertView
        }

        return retView
    }
}
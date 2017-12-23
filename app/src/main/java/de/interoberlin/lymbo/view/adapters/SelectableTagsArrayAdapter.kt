package de.interoberlin.lymbo.view.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import de.interoberlin.lymbo.App
import de.interoberlin.lymbo.R
import de.interoberlin.lymbo.model.Tag

class SelectableTagsArrayAdapter(context: Context, resource: Int, items: MutableList<Tag>) :
        ArrayAdapter<Tag>(context, resource, items) {
    companion object {
        // val TAG = SelectableTagsArrayAdapter::class.toString()
    }

    private var items: MutableList<Tag> = ArrayList()
    private var vi: LayoutInflater

    init {
        this.items = items
        this.vi = App.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var view: LinearLayout = itemView as LinearLayout
        var cbChecked: CheckBox? = null
        var tvValue: TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val tag = getItem(position)

        val holder: ViewHolder
        val view: View

        if (convertView == null) {
            view = vi.inflate(R.layout.selectable_tag, null)
            holder = ViewHolder(view)
            holder.cbChecked = view.findViewById(R.id.cbChecked) as CheckBox
            holder.tvValue = view.findViewById(R.id.tvValue) as TextView

            view.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
            view = convertView
        }

        holder.cbChecked?.isChecked = tag.checked
        holder.cbChecked?.setOnClickListener { tag.checked = holder.cbChecked?.isChecked ?: false }
        holder.tvValue?.text = tag.value

        return view
    }

    override fun getItem(position: Int): Tag = this.items[position]

    override fun getCount(): Int = items.size
}
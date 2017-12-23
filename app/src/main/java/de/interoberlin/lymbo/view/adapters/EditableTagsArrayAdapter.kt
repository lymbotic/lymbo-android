package de.interoberlin.lymbo.view.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import de.interoberlin.lymbo.App
import de.interoberlin.lymbo.R
import de.interoberlin.lymbo.model.Tag

class EditableTagsArrayAdapter(context: Context, resource: Int, items: MutableList<Tag>) :
        ArrayAdapter<Tag>(context, resource, items) {
    companion object {
        val TAG = EditableTagsArrayAdapter::class.toString()
    }

    private var items: MutableList<Tag> = ArrayList()
    private var c: Int = 0
    private var vi: LayoutInflater

    init {
        this.items = items
        this.c = items.size
        this.vi = App.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        addAll(Tag("", false))
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var view: LinearLayout = itemView as LinearLayout
        var cbChecked: CheckBox? = null
        var etValue: EditText? = null
        var pos: Int = 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val tag = getItem(position)

        val holder: ViewHolder
        val view: View

        if (convertView == null) {
            view = vi.inflate(R.layout.editable_tag, null)
            holder = ViewHolder(view)
            holder.cbChecked = view.findViewById(R.id.cbChecked) as CheckBox
            holder.etValue = view.findViewById(R.id.etValue) as EditText
            holder.pos = position

            view.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
            view = convertView
        }

        holder.pos = position

        holder.cbChecked?.isChecked = tag.checked
        holder.cbChecked?.setOnClickListener { tag.checked = holder.cbChecked?.isChecked ?: false }
        holder.etValue?.setText(tag.value)
        holder.etValue?.isFocusable = position >= c
        holder.etValue?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(e: Editable?) {
                val tagValue = e.toString().trim()

                if (holder.etValue?.isFocused == true && !tagValue.isEmpty()) {
                    getItem(holder.pos).value = tagValue
                }

                if (items.none({ it.value.trim().isEmpty() })) {
                    add(Tag("", false))
                }
            }

            override fun beforeTextChanged(c: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(c: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        return view
    }

    override fun getItem(position: Int): Tag = this.items[position]

    override fun getCount(): Int = items.size
}
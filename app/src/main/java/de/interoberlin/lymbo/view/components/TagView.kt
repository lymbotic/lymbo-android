package de.interoberlin.lymbo.view.components

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

import de.interoberlin.lymbo.R
import de.interoberlin.lymbo.model.Tag

class TagView(context: Context, t: Tag) : LinearLayout(context) {
    init {
        View.inflate(context, R.layout.tag, this)

        val tvValue = findViewById(R.id.tvValue) as TextView

        tvValue.text = t.value
    }
}

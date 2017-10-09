package de.interoberlin.lymbo.model

data class Card(
        var id: String = "",
        var sides: MutableList<Side> = ArrayList()
)
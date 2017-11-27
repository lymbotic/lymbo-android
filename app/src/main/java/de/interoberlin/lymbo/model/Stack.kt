package de.interoberlin.lymbo.model

data class Stack(
        var id: String = "",
        var title: String = "",
        var cards: MutableList<Card> = ArrayList()
)

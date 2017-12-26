package de.interoberlin.lymbo.model

import java.util.*

data class Stack(
        var id: String = "",
        var title: String = "",
        var cards: MutableList<Card> = ArrayList(),
        var tags: MutableList<Tag> = ArrayList(),

        var fileName: String = "",
        var creationDate: GregorianCalendar = GregorianCalendar(),
        var modificationDate: GregorianCalendar = GregorianCalendar()
)

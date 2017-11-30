package de.interoberlin.lymbo.controller

import de.interoberlin.lymbo.model.Card
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.*

class CardsController private constructor() {
    private object Holder {
        val INSTANCE = CardsController()
    }

    companion object {
        // val TAG = CardsController::class.toString()

        val instance: CardsController by lazy { Holder.INSTANCE }
    }

    var stackTitle = ""
    var cards: MutableList<Card> = ArrayList()
    var cardsSubject: Subject<Int> = PublishSubject.create()

    /**
     * Adds a card to the current stack
     *
     * @param card card to be added
     */
    fun addCard(card: Card) {
        cards.add(card)
        cardsSubject.onNext(cards.size)
    }

    /**
     * Updates an existing card
     *
     * @param card card to be updated
     */
    fun updateCard(position: Int, card: Card) {
        cards.removeAt(position)
        cards.add(position, card)
        cardsSubject.onNext(position)
    }

    /**
     * Deletes an existing card
     *
     * @param card card to be deleted
     */
    fun deleteCard(position: Int, card: Card) {
        cards.removeAt(position)
        cardsSubject.onNext(position)
    }
}
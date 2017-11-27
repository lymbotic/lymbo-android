package de.interoberlin.lymbo.controller

import de.interoberlin.lymbo.model.Card
import de.interoberlin.lymbo.model.Stack
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class CardsController private constructor() {
    private object Holder {
        val INSTANCE = CardsController()
    }

    companion object {
        // val TAG = CardsController::class.toString()

        val instance: CardsController by lazy { Holder.INSTANCE }
    }

    var stack: Stack = Stack()
    var cardsSubject: Subject<List<Card>> = PublishSubject.create()

    /**
     * Adds a card to the current stack
     *
     * @param card card to be added
     */
    fun addCard(card: Card) {
        stack.cards.add(card)
        cardsSubject.onNext(stack.cards)
    }
}
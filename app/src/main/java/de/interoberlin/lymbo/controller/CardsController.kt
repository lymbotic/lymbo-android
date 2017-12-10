package de.interoberlin.lymbo.controller

import de.interoberlin.lymbo.model.Card
import de.interoberlin.lymbo.model.Stack
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
        val stacksController: StacksController = StacksController.instance
    }

    lateinit var stack: Stack
    var cards: MutableList<Card> = ArrayList()
    var cardsSubject: Subject<Int> = PublishSubject.create()
    var cardsFilterSubject: Subject<Int> = PublishSubject.create()

    /**
     * Adds a card to the current stack
     *
     * @param card card to be added
     */
    fun addCard(card: Card) {
        cards.add(card)
        cardsFilterSubject.onNext(cards.size)

        saveStack()
    }

    /**
     * Updates an existing card
     *
     * @param position position of card to be updated
     * @param card card to be updated
     */
    fun updateCard(position: Int, card: Card) {
        cards.removeAt(position)
        cards.add(position, card)
        cardsFilterSubject.onNext(position)

        saveStack()
    }

    /**
     * Deletes an existing card
     *
     * @param position position of card to be deleted
     */
    fun deleteCard(position: Int) {
        cards.removeAt(position)
        cardsFilterSubject.onNext(position)

        saveStack()
    }

    /**
     * Puts card aside
     *
     * @param position position of card to be put aside
     * @param card card to be put aside
     */
    fun putCardAside(position: Int, card: Card) {
        card.checked = true
        cardsFilterSubject.onNext(position)
    }

    /**
     * Puts card to to the end
     *
     * @param position position of card to to the end
     * @param card card to to the end
     */
    fun putCardToEnd(position: Int, card: Card) {
        cards.removeAt(position)
        cards.add(card)
        cardsFilterSubject.onNext(position)
    }

    private fun saveStack() {
        stack.cards = cards
        stacksController.writeFile(stack)
    }
}
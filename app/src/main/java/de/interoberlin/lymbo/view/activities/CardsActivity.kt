package de.interoberlin.lymbo.view.activities

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import de.interoberlin.lymbo.R
import de.interoberlin.lymbo.controller.CardsController
import de.interoberlin.lymbo.view.adapters.CardsListAdapter

class CardsActivity : AppCompatActivity() {
    companion object {
        // val TAG = CardsActivity::class.toString()
    }

    private var controller = CardsController.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cards)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        val lvCards = findViewById(R.id.lvCards) as ListView

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Lymbo | ${controller.stack.title}"

        val cardsAdapter = CardsListAdapter(this, R.layout.card, controller.stack.cards)
        lvCards.adapter = cardsAdapter


        controller.cardsSubject.subscribe { _ ->
            cardsAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_settings) {
            val main = findViewById(R.id.layoutMain)
            showSnackbar(main, "Clicked on menu item Setting")
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showSnackbar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
    }
}
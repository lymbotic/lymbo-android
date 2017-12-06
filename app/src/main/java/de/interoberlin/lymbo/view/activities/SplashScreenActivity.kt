package de.interoberlin.lymbo.view.activities

import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import de.interoberlin.lymbo.App
import de.interoberlin.lymbo.R

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
    }

    override fun onResume() {
        super.onResume()

        val clContent = findViewById(R.id.clContent) as ConstraintLayout
        clContent.setOnClickListener { _ ->
            val activity = Intent(App.context, StacksActivity::class.java)
            App.context.startActivity(activity)
        }
    }
}

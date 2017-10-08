package de.interoberlin.lymbo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ListView
import de.interoberlin.lymbo.controller.StacksController
import de.interoberlin.lymbo.view.adapters.StacksListAdapter

class StacksActivity : AppCompatActivity() {
    companion object {
        val TAG = StacksActivity::class.toString()
    }

    private var controller = StacksController.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stacks)

        val main = findViewById(R.id.layoutMain)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        val ivSearch = findViewById(R.id.ivSearch) as ImageView
        val lvStacks = findViewById(R.id.lvStacks) as ListView

        setSupportActionBar(toolbar)
        setTitle(R.string.app_name)
        requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, 0)

        ivSearch.setOnClickListener({ _ ->
            controller.scan()
            showSnackbar(main, "Started scan")
        })

        val stacksAdapter = StacksListAdapter(this, R.layout.stack, controller.stacks)
        lvStacks.emptyView = findViewById(R.id.ivSearch)
        lvStacks.adapter = stacksAdapter

        controller.stacksSubject.subscribe { _ ->
            stacksAdapter.notifyDataSetChanged()
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

    /**
     * Asks user for permission
     *
     * @param permission permission to ask for
     * @param callBack   callback
     */
    private fun requestPermission(permission: String, callBack: Int) {
        if (ContextCompat.checkSelfPermission(this,
                permission) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "Permission not granted")

            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    permission)) {
                ActivityCompat.requestPermissions(this,
                        arrayOf(permission),
                        callBack)
            }
        } else {
            Log.i(TAG, "Permission granted")
        }
    }

    private fun showSnackbar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
    }
}
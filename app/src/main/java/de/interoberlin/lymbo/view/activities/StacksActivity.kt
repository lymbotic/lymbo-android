package de.interoberlin.lymbo.view.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import de.interoberlin.lymbo.R
import de.interoberlin.lymbo.controller.StacksController
import de.interoberlin.lymbo.view.adapters.StacksRecyclerViewAdapter
import de.interoberlin.lymbo.view.dialogs.CardAddDialog
import de.interoberlin.lymbo.view.dialogs.StackAddDialog

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
        val fab = findViewById(R.id.fab) as FloatingActionButton
        val ivSearch = findViewById(R.id.ivSearch) as ImageView
        val rvStacks = findViewById(R.id.rvStacks) as RecyclerView

        setSupportActionBar(toolbar)
        setTitle(R.string.app_name)
        requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, 0)

        fab.setOnClickListener { _ ->
            val dialog = StackAddDialog()
            val bundle = Bundle()
            dialog.arguments = bundle
            dialog.isCancelable = false
            dialog.stackAddSubject.subscribe { stack ->
                controller.addStack(stack)
            }
            dialog.show(fragmentManager, CardAddDialog.TAG)
        }

        ivSearch.setOnClickListener({ _ ->
            controller.clearStacks()
            controller.scanFilesystem()
            controller.scanAssets()
            showSnackbar(main, "Started scan")
        })

        val stacksAdapter = StacksRecyclerViewAdapter(controller.stacks)
        rvStacks.layoutManager = LinearLayoutManager(this)
        rvStacks.adapter = stacksAdapter

        controller.stacksSubject.subscribe { _ ->
            if (stacksAdapter.itemCount == 0) {
                ivSearch.visibility = View.VISIBLE
            } else {
                ivSearch.visibility = View.GONE
            }
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

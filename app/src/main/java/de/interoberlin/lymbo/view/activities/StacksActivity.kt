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
import com.google.gson.Gson
import de.interoberlin.lymbo.App
import de.interoberlin.lymbo.BuildConfig
import de.interoberlin.lymbo.R
import de.interoberlin.lymbo.controller.StacksController
import de.interoberlin.lymbo.view.adapters.StacksRecyclerViewAdapter
import de.interoberlin.lymbo.view.dialogs.CardDialog
import de.interoberlin.lymbo.view.dialogs.StackDialog
import de.interoberlin.lymbo.view.dialogs.TagDialog

class StacksActivity : AppCompatActivity() {
    companion object {
        val TAG = StacksActivity::class.toString()
        lateinit var stacksAdapter: StacksRecyclerViewAdapter
    }

    private var controller = StacksController.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_stacks)
        title = App.context.resources.getString(R.string.app_name)
        requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, 0)
        requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 1)

        controller.updateTags()
    }

    override fun onResume() {
        super.onResume()

        val main = findViewById(R.id.layoutMain)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        val fab = findViewById(R.id.fab) as FloatingActionButton
        val ivSearch = findViewById(R.id.ivSearch) as ImageView
        val rvStacks = findViewById(R.id.rvStacks) as RecyclerView

        setSupportActionBar(toolbar)

        stacksAdapter = StacksRecyclerViewAdapter(controller.stacks)
        rvStacks.layoutManager = LinearLayoutManager(this)
        rvStacks.adapter = stacksAdapter

        fab.setOnClickListener { _ ->
            val dialog = StackDialog()
            val bundle = Bundle()
            bundle.putString(App.context.resources.getString(R.string.bundle_stack), null)
            bundle.putString(App.context.resources.getString(R.string.bundle_tags), Gson().toJson(controller.tags))
            dialog.arguments = bundle
            dialog.isCancelable = false
            dialog.stackAddSubject.subscribe { stack ->
                controller.createStack(stack)
            }
            dialog.show(fragmentManager, StackDialog.TAG)
        }

        ivSearch.setOnClickListener({ _ ->
            controller.clearStacks()
            controller.scanFilesystem()
            if (BuildConfig.DEBUG) {
                controller.scanAssets()
            }
            showSnackbar(main, "Started scan")
        })

        if (controller.stacks.size > 0) {
            ivSearch.visibility = View.GONE
        } else {
            ivSearch.visibility = View.VISIBLE
        }

        controller.stacksSubject.subscribe { _ ->
            rvStacks.adapter = null
            rvStacks.layoutManager = null
            rvStacks.adapter = stacksAdapter
            rvStacks.layoutManager = LinearLayoutManager(this)

            stacksAdapter.notifyDataSetChanged()

            if (controller.stacks.size > 0) {
                ivSearch.visibility = View.GONE
            } else {
                ivSearch.visibility = View.VISIBLE
            }
        }

        controller.stacksFilterSubject.subscribe { _ ->
            controller.updateTags()
            stacksAdapter.applyFilter("")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_stacks, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId


        when (id) {
            R.id.action_tags -> {
                val dialog = TagDialog()
                val bundle = Bundle()
                bundle.putString(App.context.resources.getString(R.string.bundle_tags), Gson().toJson(controller.tags))
                dialog.arguments = bundle
                dialog.isCancelable = true
                dialog.tagsSelectedSubject.subscribe { tags ->
                    controller.tags = tags
                    stacksAdapter.applyFilter("")
                }
                dialog.show(fragmentManager, TagDialog.TAG)
            }
            R.id.action_settings -> {
                val main = findViewById(R.id.layoutMain)
                showSnackbar(main, "Clicked on menu item Settings")
            }
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

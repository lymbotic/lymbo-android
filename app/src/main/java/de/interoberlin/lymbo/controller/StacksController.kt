package de.interoberlin.lymbo.controller

import android.content.Context
import android.os.Environment
import android.util.Log
import com.google.gson.Gson
import de.interoberlin.lymbo.App
import de.interoberlin.lymbo.R
import de.interoberlin.lymbo.model.Stack
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.apache.commons.io.filefilter.RegexFileFilter
import org.apache.commons.io.filefilter.TrueFileFilter
import java.io.*
import java.util.*

class StacksController private constructor() {
    private object Holder {
        val INSTANCE = StacksController()
    }

    companion object {
        val TAG = StacksController::class.toString()

        val LYMBO_FILE_EXTENSION: String = App.instance.resources.getString(R.string.lymbo_file_extension)
        val LYMBO_LOOKUP_PATH: String = App.instance.resources.getString(R.string.lymbo_lookup_path)

        val instance: StacksController by lazy { Holder.INSTANCE }
    }

    var stacks: MutableList<Stack> = ArrayList()
    var stacksSubject: Subject<Stack> = PublishSubject.create()

    /**
     * Clears stacks
     */
    private fun clearStacks() {
        stacks.clear()
    }

    /**
     * Adds a stack
     *
     * @param stack   stack to be added
     */
    private fun addStack(stack: Stack) {
        stacks.add(stack)
        stacksSubject.onNext(stack)
    }

    /**
     * Scans for lymbo files in storage
     */
    fun scan() {
        clearStacks()
        findFiles(LYMBO_LOOKUP_PATH, LYMBO_FILE_EXTENSION)?.forEach { f ->
            val stack = getStackFromFile(App.instance, f)

            if (stack != null)
                addStack(stack)
        }
    }

    /**
     * Finds all files that match a certain pattern in a specific directory on the internal storage
     *
     * @param dir        directory to look for files
     * @return collection of files
     */
    private fun findFiles(dir: String, extension: String): MutableCollection<File>? = if (checkStorage()) {
        FileUtils.listFiles(File(Environment.getExternalStorageDirectory().absoluteFile.toString() + "/" + dir), RegexFileFilter(".*$extension"), TrueFileFilter.TRUE)
    } else {
        ArrayList()
    }

    /**
     * Checks if storage is available
     *
     * @return true if storage is available
     */
    private fun checkStorage(): Boolean {
        val externalStorageAvailable: Boolean
        val externalStorageWritable: Boolean

        val state = Environment.getExternalStorageState()

        when (state) {
            Environment.MEDIA_MOUNTED -> {
                externalStorageWritable = true
                externalStorageAvailable = externalStorageWritable
            }
            Environment.MEDIA_MOUNTED_READ_ONLY -> {
                externalStorageAvailable = true
                externalStorageWritable = false
            }
            else -> {
                externalStorageWritable = false
                externalStorageAvailable = externalStorageWritable
            }
        }

        return externalStorageAvailable && externalStorageWritable
    }

    /**
     * Load a stack from a *.lymbo file
     *
     * @param context context
     * @param file    *.lymbo file
     * @return stack
     */
    private fun getStackFromFile(context: Context, file: File?): Stack? {
        return try {
            if (file != null && file.absolutePath.endsWith(context.resources.getString(R.string.lymbo_file_extension)) && file.exists()) {
                getStackFromInputStream(FileInputStream(file))
            } else {
                null
            }
        } catch (e: FileNotFoundException) {
            Log.e(TAG, e.toString())
            e.printStackTrace()
            null
        }
    }

    private fun getStackFromInputStream(inputStream: InputStream): Stack? {
        try {
            val content = IOUtils.toString(inputStream, "UTF-8")
            return Gson().fromJson(content, Stack::class.java)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }
}
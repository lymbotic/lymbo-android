package de.interoberlin.lymbo.controller

import android.content.Context
import android.os.Environment
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import de.interoberlin.lymbo.App
import de.interoberlin.lymbo.App.Companion.context
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

        val LYMBO_FILE_EXTENSION: String = App.context.resources.getString(R.string.lymbo_file_extension)
        val LYMBO_LOOKUP_PATH: String = App.context.resources.getString(R.string.lymbo_lookup_path)

        val instance: StacksController by lazy { Holder.INSTANCE }
    }

    var stacks: MutableList<Stack> = ArrayList()
    var stacksSubject: Subject<Int> = PublishSubject.create()

    /**
     * Clears stacks
     */
    fun clearStacks() {
        stacks.clear()
    }

    /**
     * Creates a stack
     *
     * @param stack   stack to be created
     */
    fun createStack(stack: Stack) {
        addStack(stack)
        writeFile(stack)
    }

    /**
     * Adds a stack
     *
     * @param stack   stack to be added
     */
    private fun addStack(stack: Stack) {
        stacks.add(stack)
        stacksSubject.onNext(stacks.size - 1)
    }

    /**
     * Updates an existing stack
     *
     * @param stack stack to be updated
     */
    fun updateStack(position: Int, stack: Stack) {
        stacks.removeAt(position)
        stacks.add(position, stack)
        stacksSubject.onNext(position)
        writeFile(stack)
    }

    /**
     * Deletes an existing stack
     *
     * @param position position of stack to be deleted
     */
    fun deleteStack(position: Int, stack: Stack) {
        stacks.removeAt(position)
        stacksSubject.onNext(position)
        deleteFile(stack)
    }

    /**
     * Scans for lymbo files in storage
     */
    fun scanFilesystem() {
        findFiles(LYMBO_LOOKUP_PATH, LYMBO_FILE_EXTENSION)?.forEach { f ->
            val stack = getStackFromFile(App.context, f)

            if (stack != null)
                addStack(stack)
        }
    }

    fun scanAssets() {
        try {
            findAssets().forEach { asset ->
                val stack = getStackFromAsset(context, asset)

                if (stack != null)
                    addStack(stack)
            }
        } catch (ioe: IOException) {
            Log.e(TAG, ioe.toString())
        }
    }

    private fun getStackFromAsset(context: Context, fileName: String): Stack? {
        if (fileName.endsWith(context.resources.getString(R.string.lymbo_file_extension))) {
            try {
                val inputStream = context.assets.open(fileName)
                val stack = getStackFromInputStream(inputStream)
                if (stack != null) {
                    return stack
                }
            } catch (e: IOException) {
                Log.e(TAG, e.toString())
                e.printStackTrace()
            }
        }

        return null
    }

    /**
     * Finds all files that match a certain pattern in a specific directory on the external storage
     *
     * @param dir        directory to look for files
     * @return collection of files
     */
    private fun findFiles(dir: String, extension: String): MutableCollection<File>? = if (checkStorage() && File(Environment.getExternalStorageDirectory().absoluteFile.toString() + "/" + dir).exists()) {
        FileUtils.listFiles(
                File(Environment.getExternalStorageDirectory().absoluteFile.toString() + "/" + dir),
                RegexFileFilter(".*$extension"),
                TrueFileFilter.TRUE)
    } else {
        ArrayList()
    }

    private fun findAssets(): Array<String> = App.context.assets.list("")

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
                val stack = getStackFromInputStream(FileInputStream(file))
                stack?.fileName = file.name.toString()
                stack
            } else {
                null
            }
        } catch (e: FileNotFoundException) {
            Log.e(TAG, e.toString())
            e.printStackTrace()
            null
        }
    }

    private fun writeFile(stack: Stack): Boolean {
        if (checkStorage() && !stack.fileName.isEmpty()) {
            // Create save directory
            val saveDirectory = Environment.getExternalStorageDirectory().absoluteFile.toString() + "/" + LYMBO_LOOKUP_PATH
            File(saveDirectory).mkdirs()
            stack.modificationDate = GregorianCalendar()

            return try {
                val file = File("$saveDirectory/${stack.fileName}")
                val fw = FileWriter(file)
                val content = GsonBuilder().setPrettyPrinting().create().toJson(stack, Stack::class.java)

                fw.write(content)
                fw.flush()
                fw.close()
                true
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }
        } else {
            return false
        }
    }

    private fun deleteFile(stack: Stack): Boolean {
        if (checkStorage() && !stack.fileName.isEmpty()) {
            return try {
                val saveDirectory = Environment.getExternalStorageDirectory().absoluteFile.toString() + "/" + LYMBO_LOOKUP_PATH
                File("$saveDirectory/${stack.fileName}").delete()
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }
        } else {
            return false
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
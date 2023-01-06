package com.example.mad9132_a1

/*
 * Completed by Jasreet Kaur on November 19, 2022
 */

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.io.File

class GlideImage {

    // region Properties

    private val listOfImageUrls = mutableListOf(
        "https://source.unsplash.com/collection/630950/720x405",
        "https://www.fillmurray.com/720/405",
        "https://source.unsplash.com/random/720x405",
        "https://picsum.photos/seed/picsum/720/405",
        "https://loremflickr.com/720/405",
        "https://placekitten.com/720/405",
        "https://placedog.net/720/405",
        "https://www.placecage.com/720/405",
        "https://placebear.com/720/405",
        "https://placebeard.it/740x405",
        "https://picsum.photos/id/237/720/405",
        "https://source.unsplash.com/ACoZwVwjElU/720x405",
        "https://source.unsplash.com/5NQNh72djGo/720x405",
        "https://source.unsplash.com/hAhzuF-yeEk/720x405",
        "https://source.unsplash.com/vUNQaTtZeOo/720x405",
        "https://source.unsplash.com/yzgF-AQt1sQ/720x405",
        "https://source.unsplash.com/koy6FlCCy5s/720x405",
        "https://source.unsplash.com/-heLWtuAN3c/720x405",
        "https://source.unsplash.com/9pvTSsNV2T4/720x405",
        "https://source.unsplash.com/NjT4O7WYmwk/720x405",
        "https://source.unsplash.com/OrwkD-iWgqg/720x405",
        "https://placezombie.com/720x405",
    )

    private var listCounter = 0

    @Suppress("PRIVATE")
    var lastURL = ""   // read only property
        private set

    private val diskCacheStrategy = DiskCacheStrategy.ALL

    val localStorage = LocalStorage()

    // endregion

    // region Methods

    fun loadImageFromURL(
        imageView: ImageView,
        context: Context,
        progressBar: ProgressBar,
        url: String = getRandomImageURL()
    ) {

        progressBar.visibility = View.VISIBLE

        Glide.with(context)
            .load(url)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = View.GONE
                    toast("Load Failed: $url")
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = View.GONE
//                    toast("Load success")
                    lastURL = url
                    localStorage.save(TheApp.context.getString(R.string.last_url_key), lastURL)
                    return false
                }
            })
            .diskCacheStrategy(diskCacheStrategy)
            .into(imageView)

    }

    private fun getRandomImageURL(): String {

        lastURL = listOfImageUrls[listCounter]

        listCounter++

        if (listCounter == listOfImageUrls.size) {

            listOfImageUrls.shuffle()
            listCounter = 0
        }

        return lastURL
    }

    fun emptyCache(context: Context) {
        val asyncGlide = AsyncGlide(context)
        asyncGlide.execute() // clear the image cache in the background
    }

    // region load image from internal storage

    fun loadImageFromInternalStorage(
        imageView: ImageView,
        context: Context
    ) {
        val filePath =
            "${context.filesDir}${File.separator}${context.getString(R.string.last_image_file_name)}"
        Glide.with(context)
            .load(File(filePath))
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(imageView)
    }

    // endregion

    // region Nested Class

    // Nested class visible to outer class but outer class is not visible to nested class
    // therefore add keyword inner

    //inner async class used to delete existing Glide image cache
    private inner class AsyncGlide(val context: Context) : CoroutinesAsyncTask<Any, Any, Any>() {
        override fun doInBackground(vararg params: Any?) {
            Glide.get(context).clearDiskCache()
        }

        override fun onPostExecute(result: Any?) {
            toast("Image cache deleted")
            super.onPostExecute(result)
        }
    }

    // endregion

    fun toast(message: String) {
        Toast.makeText(TheApp.context, message, Toast.LENGTH_SHORT).show()
    }


    init {
        listOfImageUrls.shuffle()
    }

    // endregion

}
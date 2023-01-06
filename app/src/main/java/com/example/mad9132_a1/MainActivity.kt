package com.example.mad9132_a1

/*
 * Completed by Jasreet Kaur on November 19, 2022
 */

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GestureDetectorCompat
import com.example.mad9132_a1.databinding.ActivityMainBinding
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import kotlin.math.abs

class MainActivity : AppCompatActivity(),
    GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
    // Implementing the Listener interfaces for Gestures and for double tap detection

    // region Properties

    private val glideImage = GlideImage()

    private lateinit var gestureDetector: GestureDetectorCompat

    private lateinit var binding: ActivityMainBinding

    private var showingSystemUI = true

    private val requestCode = 42

    private var permissionTOWriteToExternalStorage = false

    // endregion

    // region Methods

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // set a systemUIVisibility listener

        window.addSystemUIVisibilityListener {
            showingSystemUI = it
        }

        gestureDetector = GestureDetectorCompat(this, this)
        gestureDetector.setOnDoubleTapListener(this)

        val internetConnection = InternetConnection(this)

        if (!internetConnection.isConnected) {
            AlertDialog.Builder(this)
                .setTitle(R.string.message_title)
                .setMessage(R.string.message_text)
                .setIcon(R.drawable.ic_baseline_network_check_24)
                .setNegativeButton(R.string.quit) { _, _ ->
                    finish()
                }
                .setCancelable((false))
                .show()
        } else {

            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
                setupPermissions()
            }

            glideImage.emptyCache(this)


            val fileName = getString(R.string.last_image_file_name)
            val file = getFileStreamPath(fileName)

            if (file.exists()) {
                glideImage.loadImageFromInternalStorage(binding.imageView1, this)  //  Load the the last image
            } else {

                val localStorage = LocalStorage()

                val lastUrl: String? =
                    localStorage.getValueString(this.getString(R.string.last_url_key))

                lastUrl?.let {
                    glideImage.loadImageFromURL(binding.imageView1, this, binding.progressBar, it)
                } ?: glideImage.loadImageFromURL(binding.imageView1, this, binding.progressBar)
            }
        }
    }

    // onStop is where we should save application data
    override fun onStop() {
        super.onStop()

        // Save the current(i.e, last) image from the imageView control
        if (binding.imageView1.drawable != null) {
            val bitmap = binding.imageView1.drawable.toBitmap()
            val asyncStorageIO = AsyncStorageIO(bitmap, true)
            asyncStorageIO.execute()
        }

    }

    @Suppress("UNUSED")
    fun toast(message: String) {
        Toast.makeText(TheApp.context, message, Toast.LENGTH_SHORT).show()
    }

    // region Gesture Methods

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    override fun onDown(e: MotionEvent): Boolean {
        return true
    }

    override fun onShowPress(e: MotionEvent) {

    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return true
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return true
    }

    override fun onLongPress(e: MotionEvent) {
        glideImage.emptyCache(this)
    }

    override fun onFling(
        e1: MotionEvent, // The first down motion event that started the fling
        e2: MotionEvent, // The move motion event that triggered the current onFling
        velocityX: Float, // The velocity of this fling measured in pixels per second along the x axis
        velocityY: Float // The velocity of this fling measured in pixels per second along the y axis
    ): Boolean {

        /*
        Swipe Distance threshold indicates the difference between the initial and final position
        of a touch event in any of the four possible directions.
        Swipe Velocity threshold indicates how quickly it was swiped.
        */

        val swipeDistanceThreshold = 150 // If at least 150 pixels per second travelled it will be considered a gesture
        val swipeVelocityThreshold = 200
        val yDifference = e2.y - e1.y
        val xDifference = e2.x - e1.x

        if (abs(xDifference) > abs(yDifference)) { // Likely operating on the x-axis

            if (abs(xDifference) > swipeDistanceThreshold && abs(velocityX) > swipeVelocityThreshold) {
                if (xDifference < 0) { // Swiped Left
                    glideImage.loadImageFromURL(binding.imageView1, this, binding.progressBar)
                }
            }

        } else if (abs(yDifference) > swipeDistanceThreshold && abs(velocityY) > swipeVelocityThreshold) {
            if (yDifference > 0) { // Swiped down
                // You don't need permission from user to save photos sdk 29 and up
                if (permissionTOWriteToExternalStorage || android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {

                    // Save the current image from the imageView control to the gallery
                    if (binding.imageView1.drawable != null) {
                        val bitmap = binding.imageView1.drawable.toBitmap()
                        val asyncStorageIO = AsyncStorageIO(bitmap)
                        asyncStorageIO.execute()
                    }

                } else {
                    toast(getString(R.string.save_permission_denied))
                }

            }
        }
        return true
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        return true
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        showingSystemUI = if (showingSystemUI) {
            hideSystemUI()
            false
        } else {
            showSystemUI()
            true
        }
        return true
    }

    override fun onDoubleTapEvent(e: MotionEvent): Boolean {
        return true
    }

    // endregion

    // region Hide/Show systemUI

    private fun Window.addSystemUIVisibilityListener(visibilityListener: (Boolean) -> Unit) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            decorView.setOnApplyWindowInsetsListener { view, insets ->
                val suppliedInsets = view.onApplyWindowInsets(insets)
                // only check for statusBars() and navigationBars(),
                // because captionBar() is not always
                // available and isVisible() could return false, although showSystemUI() had been called
                visibilityListener(suppliedInsets.isVisible(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars()))
                suppliedInsets
            }
        } else {
            @Suppress("DEPRECATION")
            decorView.setOnSystemUiVisibilityChangeListener {
                visibilityListener((it and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0)
            }
        }
    }

    private fun showSystemUI() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            // UI SDK 30 and up
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.show(WindowInsets.Type.systemBars())
            supportActionBar?.show()
        } else {
            // UI SDK 29 and lower
            // Shows the system bars by removing all the flags
            // except for the ones that make the content appear under the system bars.
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        }
    }

    private fun hideSystemUI() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            // UI SDK 30 and up
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let {
                it.hide(
                    WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars()
                            or WindowInsets.Type.systemBars()
                )
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                supportActionBar?.hide()
            }
        } else {
            // UI SDK 29 and lower
            @Suppress("DEPRECATION")
            // Enables regular immersive mode
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                    // Set the content to appear under the system bars so that the
                    // content doesn't resize when the system bars hide and show.
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    // Hide the nav bar and status bar
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }
    }

    // endregion

    // region Permissions

    private fun makeRequest() {

        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), requestCode
        )
    }

    private fun setupPermissions() {
        val permission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            toast("Permission Denied!")
            makeRequest()
        } else {
            toast("Permission already granted...")
            permissionTOWriteToExternalStorage = true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            this.requestCode -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    toast("Permission denied by user!")
                } else {
                    toast("Permission granted by user")
                    permissionTOWriteToExternalStorage = true
                }
            }
        }
    }

    // endregion

    // endregion

}
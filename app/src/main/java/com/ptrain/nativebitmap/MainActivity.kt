package com.ptrain.nativebitmap

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import com.mico.libx.hook.BuildConfig
import com.mico.libx.hook.nativebitmap.NativeBitmap
import com.mico.libx.hook.thread.ThreadHook


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ct = System.currentTimeMillis()
        NativeBitmap.init(BuildConfig.DEBUG)
        ThreadHook.init(BuildConfig.DEBUG, 128 * 1024)
        Log.d("TAG", "nativehook cost=${System.currentTimeMillis() - ct}")
        val imageContainer = findViewById<ViewGroup>(R.id.image_container)

        val img = ImageView(this)
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.android_app)
        img.scaleType = ImageView.ScaleType.CENTER_CROP
        img.setImageBitmap(bitmap)
        imageContainer.addView(
            img, LinearLayoutCompat.LayoutParams(
                LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                600
            )
        )


        findViewById<View>(R.id.release_btn).setOnClickListener {
            imageContainer.removeAllViews()
        }

        findViewById<View>(R.id.add_btn).setOnClickListener {
            val img = ImageView(this)
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.android_app)
//        bitmaps.add(bitmap)
            img.scaleType = ImageView.ScaleType.CENTER_CROP
            img.setImageBitmap(bitmap)
            imageContainer.addView(
                img, LinearLayoutCompat.LayoutParams(
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                    600
                )
            )
        }
    }

}
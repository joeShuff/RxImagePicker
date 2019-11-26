package com.mlsdev.sample

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mlsdev.rximagepicker.RxImageConverters
import com.mlsdev.rximagepicker.RxImagePicker
import com.mlsdev.rximagepicker.Sources
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var ivPickedImage: ImageView
    private lateinit var converterRadioGroup: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ivPickedImage = findViewById(R.id.iv_picked_image)
        val fabCamera = findViewById<FloatingActionButton>(R.id.fab_pick_camera)
        val fabGallery = findViewById<FloatingActionButton>(R.id.fab_pick_gallery)
        val fabDocuments = findViewById<FloatingActionButton>(R.id.fab_pick_documents)
        val fabChooser = findViewById<FloatingActionButton>(R.id.fab_pick_chooser)
        converterRadioGroup = findViewById(R.id.radio_group)
        converterRadioGroup.check(R.id.radio_uri)

        fab_pick_files.setOnClickListener {
            pickFiles()
        }
        fabCamera.setOnClickListener { pickImageFromSource(Sources.CAMERA) }
        fabGallery.setOnClickListener { pickImageFromSource(Sources.GALLERY) }
        fabDocuments.setOnClickListener { pickImageFromSource(Sources.DOCUMENTS) }
        fabChooser.setOnClickListener { pickImageFromSource(Sources.CHOOSER) }
    }

    private fun pickImageFromSource(source: Sources) {
        RxImagePicker.with(supportFragmentManager).requestImage(source, getString(R.string.label_pick_image))
            .subscribe({
                onImagePicked(it)
            }, {
                Toast.makeText(this@MainActivity, java.lang.String.format("Error: %s", it), Toast.LENGTH_LONG).show()
            })
    }

    private fun pickFiles() {
        RxImagePicker.with(supportFragmentManager)
                .requestFiles(true)
                .subscribe({
                    Log.i("RESPONSE", "Uri received: $it")
                    Toast.makeText(this, "File: $it chosen", Toast.LENGTH_LONG).show()
                },
                {
                    Log.i("RESPONSE", "Files broke")
                    it.printStackTrace()
                    Toast.makeText(this, "Files went wrong", Toast.LENGTH_LONG).show()
                })
    }

    private fun onImagePicked(result: Any) {
        Toast.makeText(this, java.lang.String.format("Result: %s", result), Toast.LENGTH_LONG).show()
        if (result is Bitmap) {
            ivPickedImage.setImageBitmap(result)
        } else {
            Glide.with(this)
                    .load(result) // works for File or Uri
                    .transition(DrawableTransitionOptions().crossFade())
                    .into(ivPickedImage)
        }
    }

    private fun createTempFile(): File {
        return File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), System.currentTimeMillis().toString() + "_image.jpeg")
    }
}

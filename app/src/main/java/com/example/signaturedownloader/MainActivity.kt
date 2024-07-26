package com.example.signaturedownloader

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import kotlin.math.sign

class MainActivity : AppCompatActivity() {

    private lateinit var canvasView: CanvasView
    private lateinit var buttonSave: Button
    private lateinit var buttonClear: Button
    private lateinit var spinnerColor: Spinner
    private lateinit var spinnerWidth: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),1)
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)

        canvasView = findViewById(R.id.canvas)
        buttonSave = findViewById(R.id.button_save)
        buttonClear = findViewById(R.id.button_clear)
        spinnerColor = findViewById(R.id.spinner_color)
        spinnerWidth = findViewById(R.id.spinner_width)

        val colorAdapter = ArrayAdapter.createFromResource(this, R.array.colors, android.R.layout.simple_spinner_item)
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerColor.adapter = colorAdapter

        val widthAdapter = ArrayAdapter.createFromResource(this, R.array.stroke_widths, android.R.layout.simple_spinner_item)
        widthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerWidth.adapter = widthAdapter

        buttonSave.setOnClickListener {
            saveSignature()
        }

        buttonClear.setOnClickListener {
            Log.d("MainActivity", "Clear button clicked") // Log button click event
            canvasView.clearCanvas()
        }

        spinnerColor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val colorName = parent.getItemAtPosition(position).toString()
                val color = when (colorName) {
                    "Black" -> Color.BLACK
                    "Red" -> Color.RED
                    "Green" -> Color.GREEN
                    "Blue" -> Color.BLUE
                    else -> Color.BLACK
                }
                canvasView.setStrokeColor(color)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spinnerWidth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val widthString = parent.getItemAtPosition(position).toString()
                val width = widthString.toFloat()
                canvasView.setStrokeWidth(width)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun saveSignature() {

        val bitmap = getImageOfView(canvasView)
        if(bitmap != null){
            saveToStorage(bitmap)
        }
//        canvasView.invalidate()
//        val signatureBitmap = canvasView.getBitmap()
//        println(signatureBitmap)
//        val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "signatures")
//
//        if (!directory.exists()) {
//            directory.mkdirs()
//        }
//
//        val file = File(directory, "signature.png")
//
//        try {
//            val stream = FileOutputStream(file)
//            signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
//            stream.flush()
//            stream.close()
//            Toast.makeText(this, "Signature saved to ${file.absolutePath}", Toast.LENGTH_SHORT).show()
//        } catch (e: IOException) {
//            Toast.makeText(this, "Failed to save signature", Toast.LENGTH_SHORT).show()
//            e.printStackTrace()
//        }
    }

    private fun saveToStorage(bitmap: Bitmap) {
        val imageName = "${System.currentTimeMillis()}.jpg"

        var fos: OutputStream? = null
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            this.contentResolver?.also {resolver ->
                val contentValues = ContentValues().apply{
                    put(MediaStore.MediaColumns.DISPLAY_NAME, imageName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let {
                    resolver.openOutputStream(it)
                }
            }
        }else{
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, imageName)
            fos = FileOutputStream(image)
        }
        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG,100, it)
            Toast.makeText(this, "Succesfully stored", Toast.LENGTH_LONG).show()
        }
    }

    private fun getImageOfView(canvasView: CanvasView): Bitmap? {
        var image: Bitmap? = null

        try{
            image = Bitmap.createBitmap(canvasView.measuredWidth, canvasView.measuredHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(image)
            canvasView.draw(canvas)
        }catch (e: Exception){
            Log.e("Error", "Cannot draw");
        }

        return image;
    }
}

package com.example.snigdhaapp.Camera

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.snigdhaapp.compose.previewImage
import com.example.snigdhaapp.repo.AuthState

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraManager(
    private val context: Context,
    private val finderView: PreviewView,
    private val lifecycleOwner: LifecycleOwner,
    private val outputDirectory: File,


    ){

    val database = Firebase.database

    private val showprogress = mutableStateOf(false)
    private val completeprogress = mutableStateOf(false)
    private val progress = mutableStateOf(0f)
    lateinit var imageCapture: ImageCapture

    private lateinit var photoUri: Uri
    private var preview: Preview? = null
    private var camera: Camera? = null
    private lateinit var cameraExecutor: ExecutorService
    private var cameraSelectorOption = CameraSelector.LENS_FACING_FRONT
    private var cameraProvider: ProcessCameraProvider? = null


    init {
        createNewExecutor()
        database.getReference().child("bool").setValue(false)
    }

    private fun createNewExecutor() {
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener(
            Runnable {
                cameraProvider = cameraProviderFuture.get()
                preview = Preview.Builder()
                    .build()


                imageCapture = ImageCapture.Builder().build()
                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(cameraSelectorOption)
                    .build()

                setCameraConfig(cameraProvider, cameraSelector)

            }, ContextCompat.getMainExecutor(context)
        )
    }



    fun takeOhoto() =
        takePhoto(
            filenameFormat = "yyyy-MM-dd-HH-mm-ss-SSS",
            imageCapture = imageCapture,
            outputDirectory = outputDirectory,
            executor = cameraExecutor,
            onImageCaptured = {
                photoUri = it

                database.getReference().child("photouri").setValue("file://"+it.path).also {

                    database.getReference().child("bool").setValue(true)
                }
            },
            onError = { Log.e("kilo", "View error:", it) }

        )
    fun bool(bool:Boolean):Boolean= bool



    fun takePhoto(
        filenameFormat: String,
        imageCapture: ImageCapture,
        outputDirectory: File,
        executor: Executor,
        onImageCaptured: (Uri) -> Unit,
        onError: (ImageCaptureException) -> Unit
    ) {

        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(filenameFormat, Locale.US).format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    Log.e("kilo", "Take photo error:", exception)
                    onError(exception)
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    onImageCaptured(savedUri)
                    val bundle = Bundle()
                    val stringPath=savedUri.path
                    bundle.putString("url",stringPath)
                    val fragment = previewImage()
                    fragment.arguments = bundle



                }
            })

    }


    fun setCameraConfig(
        cameraProvider: ProcessCameraProvider?,
        cameraSelector: CameraSelector
    ) {
        try {
            cameraProvider?.unbindAll()
            camera = cameraProvider?.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
                )
            preview?.setSurfaceProvider(
                finderView.createSurfaceProvider()
            )
        } catch (e: Exception) {
            Log.e(TAG, "Use case binding failed", e)
        }
    }

    fun changeCameraSelector() {
        cameraProvider?.unbindAll()
        cameraSelectorOption =
            if (cameraSelectorOption == CameraSelector.LENS_FACING_BACK) CameraSelector.LENS_FACING_FRONT
            else CameraSelector.LENS_FACING_BACK

        startCamera()
    }

    companion object {
        private const val TAG = "CameraXBasic"
    }



}

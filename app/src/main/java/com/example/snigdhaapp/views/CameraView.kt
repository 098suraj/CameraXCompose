package com.example.snigdhaapp.views

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.snigdhaapp.Camera.CameraManager
import com.example.snigdhaapp.R
import com.example.snigdhaapp.databinding.FragmentCameraViewBinding
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class CameraView : Fragment(){
    private lateinit var cameraManager: CameraManager
    private lateinit var outputDirectory: File
    private var _binding: FragmentCameraViewBinding? = null
    private val binding get() = _binding!!
    lateinit var photoUri: Uri
    val database = Firebase.database
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       outputDirectory= getOutputDirectory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCameraViewBinding.inflate(inflater, container, false)
        return binding.root


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createCameraManager()
        checkForPermission()
        onClicks()

        database.getReference().child("bool").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value==true){
                    database.getReference().child("photouri").addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val ph=snapshot.value as String
                            val action=CameraViewDirections.actionCameraViewToPreviewImage2(ph)
                            lifecycleScope.launch{
                                delay(1000L)
                                findNavController().navigate(action)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
                }






            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun checkForPermission() {
        if (allPermissionsGranted()) {
            cameraManager.startCamera()
        } else {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS
                )
            }
        }
    }

    private fun onClicks() {
        binding.btnSwitch.setOnClickListener {
            cameraManager.changeCameraSelector()

        }
        binding.TakePhoto.setOnClickListener {
           cameraManager.takeOhoto()

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                cameraManager.startCamera()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                )
                    .show()
                activity?.finish()
            }
        }
    }

    private fun createCameraManager() {
        cameraManager = CameraManager(
            requireContext(),
            binding.previewViewFinder,
            this,
            outputDirectory =outputDirectory,

        )
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
    }
    fun getOutputDirectory(): File {
        val mediaDir = activity?.externalMediaDirs?.firstOrNull()?.let {
            File(it, "SnigdhaApp").apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else activity?.filesDir!!
    }


}
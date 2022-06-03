package com.example.snigdhaapp.compose

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.compose.rememberImagePainter
import com.example.snigdhaapp.R
import com.example.snigdhaapp.databinding.FragmentPreviewImageBinding
import com.example.snigdhaapp.ui.theme.BackgroundColor
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*


class previewImage : Fragment() {
    private var _binding: FragmentPreviewImageBinding? = null
    private val binding get() = _binding!!
    private lateinit var photoUri: Uri
    private var shouldShowPhoto: MutableState<Boolean> = mutableStateOf(false)
    private var complete: MutableState<Boolean> = mutableStateOf(false)
    var indicatorProgress = 0f
    val storage = Firebase.storage
    val database = Firebase.database
    val progress = mutableStateOf(0f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        photoUri = Uri.parse(previewImageArgs.fromBundle(requireArguments()).photouri)
        Log.i("kilo", "onCreate:$photoUri ")
        if (photoUri != null) {
            shouldShowPhoto.value = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPreviewImageBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    @SuppressLint("UnrememberedMutableState")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        uploadToFirebase(photoUri)
        if (complete.value) {

        }
        binding.previewImage.setContent {

            Surface(color = MaterialTheme.colors.BackgroundColor) {

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize(),

                    ) {

                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (shouldShowPhoto.value) {
                            Log.i("kilo", "nahi chal raha hun $photoUri ")
                            Image(
                                painter = rememberImagePainter(photoUri),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(400.dp)
                                    .clip(RoundedCornerShape(20.dp))
                            )
                            Text(
                                text = "Uploading Image",
                                color = Color.White,
                                modifier = Modifier.padding(15.dp)
                            )
                            var progres by remember { mutableStateOf(0f) }
                            progres = progress.value
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .padding(top = 20.dp)
                                    .requiredWidth(270.dp)
                                    .align(Alignment.CenterHorizontally)
                                    .clip(RoundedCornerShape(20.dp)), // Rounded edges
                                progress = progres,
                                backgroundColor = Color.Black,
                                color = Color.White

                            )

                        }
                    }
                }


            }
        }
    }

    private fun uploadToFirebase(photoUri: Uri) {

        val storageRef = storage.reference.child("images/").child(
            SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(System.currentTimeMillis())+ "." + getFileExtension(photoUri)
        )
        storageRef.putFile(photoUri).addOnSuccessListener(OnSuccessListener {
            Log.i("kilo", "Image uploaded")
            complete.value = true
            storageRef.downloadUrl.addOnSuccessListener(OnSuccessListener {
                database.getReference().child("img").setValue(it.toString())
                findNavController().navigate(R.id.homeScreen)
            }).addOnFailureListener(OnFailureListener {
                it.message?.let { it1 ->
                    Log.i("kilo", it1)


                }
            })


        }).addOnProgressListener(OnProgressListener {
            Log.i("kilo", "inprogress ")
            indicatorProgress = ((1.0 * it.bytesTransferred) / it.totalByteCount).toFloat();
            progress.value = indicatorProgress


        }).addOnFailureListener(OnFailureListener {
            it.message?.let { it1 ->
                Log.i("kilo", it1)

            }
        })
    }

    private fun getFileExtension(uri: Uri): String {
        val mimeType = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
        return mimeType

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
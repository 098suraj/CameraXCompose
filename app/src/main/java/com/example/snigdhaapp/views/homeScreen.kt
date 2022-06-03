package com.example.snigdhaapp.views

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import com.example.sassistant.screens.homeScreenDashBoard.HomeScreen
import com.example.snigdhaapp.databinding.FragmentHomeScreenBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class homeScreen : Fragment() {

    var name:String?=null
    var _binding:FragmentHomeScreenBinding?=null
    val binding get()=_binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding= FragmentHomeScreenBinding.inflate(inflater,container,false)
        return binding.root
    }

    @OptIn(ExperimentalFoundationApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.homescreencompose.setContent {

            HomeScreen()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
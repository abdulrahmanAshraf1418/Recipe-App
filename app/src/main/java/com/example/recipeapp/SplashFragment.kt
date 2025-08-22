package com.example.recipeapp

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.auth.FirebaseAuth

class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lottieSplash = view.findViewById<LottieAnimationView>(R.id.lottieSplash)
        
        lottieSplash.addAnimatorListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    if (currentUser != null) {
                        startActivity(Intent(requireContext(), RecipeActivity::class.java))
                        requireActivity().finish()
                    } else {
                        findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
                    }
            }
        })
    }
}



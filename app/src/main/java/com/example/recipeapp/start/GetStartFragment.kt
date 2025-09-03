package com.example.recipeapp.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.example.recipeapp.R
import com.google.android.material.button.MaterialButton

class GetStartFragment : Fragment() {

    private lateinit var chefAnimation: LottieAnimationView
    private lateinit var foodAppAnimation: LottieAnimationView
    private lateinit var foodChoiceAnimation: LottieAnimationView
    private lateinit var startTitle: TextView
    private lateinit var startDescription: TextView
    private lateinit var nextButton: ImageButton
    private lateinit var btnStart: MaterialButton
    private lateinit var skipButton: TextView

    private var currentStep = 0

    private val titles = listOf(
        "Master Every Meal",
        "Your Personal Cookbook",
        "Endless Recipes Await"
    )

    private val descriptions = listOf(
        "Step-by-step guidance to turn everyday cooking into a delicious adventure.",
        "Save your favorites and build a cookbook that's truly yours.",
        "From quick snacks to gourmet dishes — discover flavors from around the world."
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_get_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        updateStep(0)
        setupClickListeners()


        val cardView = view.findViewById<View>(R.id.materialCardView)

        cardView.setOnTouchListener(object : OnSwipeTouchListener(requireContext()) {
            override fun onSwipeLeft() {
                currentStep = (currentStep + 1) % 3
                updateStep(currentStep, fromRight = true)
            }

            override fun onSwipeRight() {
                currentStep = if (currentStep - 1 < 0) 2 else currentStep - 1
                updateStep(currentStep, fromRight = false)
            }
        })

    }

    private fun updateStep(step: Int, fromRight: Boolean = true) {

        setupAnimation(fromRight)

        chefAnimation.visibility = View.INVISIBLE
        foodAppAnimation.visibility = View.INVISIBLE
        foodChoiceAnimation.visibility = View.INVISIBLE

        when (step) {
            0 -> chefAnimation.visibility = View.VISIBLE
            1 -> foodAppAnimation.visibility = View.VISIBLE
            2 -> foodChoiceAnimation.visibility = View.VISIBLE
        }

        startTitle.text = titles[step]
        startDescription.text = descriptions[step]
    }

    private fun initViews(view: View) {
        chefAnimation = view.findViewById(R.id.chef_animation)
        foodAppAnimation = view.findViewById(R.id.food_app_animation)
        foodChoiceAnimation = view.findViewById(R.id.food_choice_animation)
        startTitle = view.findViewById(R.id.start_title)
        startDescription = view.findViewById(R.id.start_description)
        nextButton = view.findViewById(R.id.nextButton)
        skipButton = view.findViewById(R.id.btn_Skip)
        btnStart = view.findViewById(R.id.bten_start)
    }
    private fun setupClickListeners() {
        nextButton.setOnClickListener {
            currentStep = (currentStep + 1) % 3
            updateStep(currentStep, fromRight = true)
        }

        btnStart.setOnClickListener {
            findNavController().navigate(R.id.action_getStartFragment_to_loginFragment)
        }
        skipButton.setOnClickListener {
            findNavController().navigate(R.id.action_getStartFragment_to_loginFragment)
        }
    }

    private fun setupAnimation(fromRight: Boolean = true) {
        val startLayout = view?.findViewById<View>(R.id.start_layout)
        val title = view?.findViewById<TextView>(R.id.start_title)
        val description = view?.findViewById<TextView>(R.id.start_description)

        val animRes = if (fromRight) R.anim.slide_in_right else R.anim.slide_in_left
        val anim = AnimationUtils.loadAnimation(requireContext(), animRes)
        startLayout?.startAnimation(anim)

        val animText = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        title?.startAnimation(animText)
        description?.startAnimation(animText)
    }

}
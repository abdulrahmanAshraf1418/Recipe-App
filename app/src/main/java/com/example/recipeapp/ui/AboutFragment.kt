package com.example.recipeapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.recipeapp.R

class AboutFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnContact = view.findViewById<Button>(R.id.btnContact)
        val tvVersion = view.findViewById<TextView>(R.id.tvVersion)

        val versionName = requireContext().packageManager
            .getPackageInfo(requireContext().packageName, 0).versionName
        tvVersion.text = "Version $versionName"

        btnContact.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, arrayOf("3bdelr7man.ashraf7@gmail.com"))
                putExtra(Intent.EXTRA_SUBJECT, "Contact - Recipe App")
                putExtra(Intent.EXTRA_TEXT, "Hello, I’d like to ask about...")
            }
            try {
                startActivity(Intent.createChooser(intent, "Send Email via..."))
            } catch (ex: android.content.ActivityNotFoundException) {
                Toast.makeText(requireContext(), "No email clients installed.", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
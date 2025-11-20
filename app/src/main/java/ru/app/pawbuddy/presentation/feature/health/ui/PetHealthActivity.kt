package ru.app.pawbuddy.presentation.feature.health.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.app.pawbuddy.data.local.MyPreferenceManager
import ru.app.pawbuddy.databinding.ActivityPetHealthBinding

class PetHealthActivity : AppCompatActivity() {
    private var petId: String? = null
    private lateinit var preferenceManager: MyPreferenceManager

    private lateinit var binding: ActivityPetHealthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPetHealthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener { finish() }

        preferenceManager = MyPreferenceManager(this)
        petId = intent.getStringExtra("petId") ?: return

        binding.insuranceBtn.setOnClickListener {
            val intent = Intent(this, PetInsuranceActivity::class.java)
            intent.putExtra("petId", petId)
            startActivity(intent)
        }

        binding.vaccinesBtn.setOnClickListener {
            val intent = Intent(this, VaccinesActivity::class.java)
            intent.putExtra("petId", petId)
            startActivity(intent)
        }

    }
}
package ru.app.pawbuddy.presentation.feature.health.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ru.app.pawbuddy.data.local.MyPreferenceManager
import ru.app.pawbuddy.R
import ru.app.pawbuddy.presentation.feature.health.adapter.VaccinesAdapter
import ru.app.pawbuddy.databinding.ActivityVaccinesBinding
import java.text.SimpleDateFormat
import java.util.Locale


class VaccinesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVaccinesBinding
    private var petId: String? = null
    private lateinit var preferenceManager: MyPreferenceManager
    private lateinit var vaccinesAdapter: VaccinesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVaccinesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener { finish() }

        preferenceManager = MyPreferenceManager(this)
        petId = intent.getStringExtra("petId") ?: return

        setupEmptyLayout()
        setupRecyclerView()

        binding.addVaccines.setOnClickListener {
            val intent = Intent(this, AddVaccinesActivity::class.java)
            intent.putExtra("petId", petId)
            startActivity(intent)
        }
    }

    private fun setupEmptyLayout() {
        binding.emptyLayout.apply {
            emptyImage.setImageResource(R.drawable.vaccines_image)
            emptyTitle.text = "Ваш питомец не привит?"
            emptyDesc.text = "Добавьте прививки для вашего питомца"
        }
    }

    private fun setupRecyclerView() {
        vaccinesAdapter = VaccinesAdapter(this,preferenceManager,petId!!)
        binding.vaccinesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.vaccinesRecyclerView.adapter = vaccinesAdapter
    }

    override fun onResume() {
        super.onResume()
        loadVaccines()
    }

    fun loadVaccines() {
        petId?.let {
            val vaccinesList = preferenceManager.getVaccinesForPet(it)

            if (vaccinesList.isEmpty()) {
                binding.vaccinesRecyclerView.visibility = View.GONE
                binding.emptyLayout.root.visibility = View.VISIBLE
            } else {
                binding.vaccinesRecyclerView.visibility = View.VISIBLE
                binding.emptyLayout.root.visibility = View.GONE

                val sortedVaccines = vaccinesList.sortedByDescending { vaccine ->
                    SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(vaccine.vaccineDate)
                }
                vaccinesAdapter.submitList(sortedVaccines)
            }
        }
    }


}

package ru.app.pawbuddy.presentation.feature.pet.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ru.app.pawbuddy.data.local.MyPreferenceManager
import ru.app.pawbuddy.R
import ru.app.pawbuddy.presentation.feature.health.adapter.WalkAdapter
import ru.app.pawbuddy.databinding.ActivityPetWalkBinding

class PetWalkActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPetWalkBinding
    private var petId: String? = null
    private lateinit var preferenceManager: MyPreferenceManager
    private lateinit var adapter: WalkAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPetWalkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEmptyLayout()

        preferenceManager = MyPreferenceManager(this)
        petId = intent.getStringExtra("petId") ?: return

        binding.back.setOnClickListener { finish() }
        binding.addWalk.setOnClickListener {
            val intent = Intent(this, AddPetActivityActivity::class.java)
            intent.putExtra("petId", petId)
            startActivity(intent)
        }

        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun setupRecyclerView() {
        adapter = WalkAdapter(emptyList(), this, preferenceManager)
        binding.walkRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.walkRecyclerView.adapter = adapter
    }

    private fun refreshData() {
        val petData = preferenceManager.getPetDataById(petId!!)
        petData?.let {
            adapter.updateData(it.walkList ?: emptyList(),petData.petId)

            binding.textView2.text = "Прогулка ${petData.petName}"

            if (it.walkList.isEmpty()) {
                binding.emptyLayout.root.visibility = View.VISIBLE
                binding.walkRecyclerView.visibility = View.GONE
            } else {
                binding.emptyLayout.root.visibility = View.GONE
                binding.walkRecyclerView.visibility = View.VISIBLE
            }
        }
    }

    private fun setupEmptyLayout() {
        binding.emptyLayout.apply {
            emptyImage.setImageResource(R.drawable.time_ic)
            emptyTitle.text = "Ваш питомец не гулял"
            emptyDesc.text = "Добавьте прогулки для вашего питомца"
        }
    }
}

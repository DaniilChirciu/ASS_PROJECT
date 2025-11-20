package ru.app.pawbuddy.presentation.feature.nutrition.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ru.app.pawbuddy.data.local.MyPreferenceManager
import ru.app.pawbuddy.R
import ru.app.pawbuddy.presentation.feature.nutrition.adapter.NutritionAdapter
import ru.app.pawbuddy.databinding.ActivityNutritionBinding
import ru.app.pawbuddy.domain.model.PetData

class NutritionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNutritionBinding
    private lateinit var preferenceManager: MyPreferenceManager
    private lateinit var petId: String
    private lateinit var adapter: NutritionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNutritionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceManager = MyPreferenceManager(this)

        binding.back.setOnClickListener { finish() }

        petId = intent.getStringExtra("petId").toString()

        setupRecyclerView()
        setupEmptyLayout()

        petId.let { id ->
            val petData = preferenceManager.getPetDataById(id)
            if (petData != null) {
                updateUI(petData)
            } else {
                Log.e("NutritionActivity", "Питомец не найден")
                binding.textView2.text = "Ошибка данных"
            }
        }

        binding.addFood.setOnClickListener {
            val intent = Intent(this, AddNutritionActivity::class.java)
            intent.putExtra("petId", petId)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Обновляем данные при возврате в активность
        refreshData()
    }

    private fun setupRecyclerView() {
        adapter = NutritionAdapter(emptyList(), this, preferenceManager) // Передаем context и preferenceManager
        binding.scheduleRecView.layoutManager = LinearLayoutManager(this)
        binding.scheduleRecView.adapter = adapter
    }

    private fun updateUI(petData: PetData) {
        binding.textView2.text = "Что ест ${petData.petName}"
        val foodList = petData.foodList ?: emptyList()

        adapter.updateData(foodList, petData.petId)

        // Показываем пустой layout, если еды нет
        if (foodList.isEmpty()) {
            binding.emptyLayout.root.visibility = View.VISIBLE
            binding.scheduleRecView.visibility = View.GONE
        } else {
            binding.emptyLayout.root.visibility = View.GONE
            binding.scheduleRecView.visibility = View.VISIBLE
        }
    }

    private fun refreshData() {
        val petData = preferenceManager.getPetDataById(petId)
        petData?.let {
            updateUI(it) // Используем `updateUI`, чтобы учесть emptyLayout
        }
    }

    private fun setupEmptyLayout() {
        binding.emptyLayout.apply {
            emptyImage.setImageResource(R.drawable.food_ic)
            emptyTitle.text = "Ваш питомец голоден!"
            emptyDesc.text = "Добавьте список питания вашего питомца"
        }
    }
}

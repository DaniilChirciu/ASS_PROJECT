package ru.app.pawbuddy.presentation.feature.pet.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.flexbox.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.app.pawbuddy.presentation.feature.pet.viewmodel.AddPetViewModel
import ru.app.pawbuddy.presentation.feature.pet.adapter.PetBreedAdapter
import ru.app.pawbuddy.databinding.FragmentBreedBinding
import ru.app.pawbuddy.domain.model.PetBreed

class BreedFragment : Fragment() {

    private lateinit var binding: FragmentBreedBinding
    private val addPetViewModel: AddPetViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBreedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val petList: List<PetBreed> = loadPetBreedsFromJson()

        Log.d("BreedFragment", "Loaded ${petList.size} breeds from JSON")

        if (petList.isEmpty()) {
            Log.e("BreedFragment", "Breed list is empty!")
            return
        }

        // Настраиваем FlexboxLayoutManager
        val layoutManager = FlexboxLayoutManager(context).apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.SPACE_AROUND
            alignItems = AlignItems.CENTER
            flexWrap = FlexWrap.WRAP
        }
        binding.recyclerView.layoutManager = layoutManager

        // Создаём адаптер
        val adapter = PetBreedAdapter(petList) { selectedBreed ->
            Log.d("BreedFragment", "Selected breed: ${selectedBreed.name}")
            addPetViewModel.setSelectedBreed(selectedBreed) // <=== Обновляем ViewModel
        }
        binding.recyclerView.adapter = adapter
    }

    private fun loadPetBreedsFromJson(): List<PetBreed> {
        return try {
            val inputStream = requireContext().assets.open("pet_breeds.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            Log.d("BreedFragment", "JSON Loaded Successfully")
            Gson().fromJson(jsonString, object : TypeToken<List<PetBreed>>() {}.type)
        } catch (e: Exception) {
            Log.e("BreedFragment", "Error loading JSON: ${e.message}")
            emptyList()
        }
    }
}

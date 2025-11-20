package ru.app.pawbuddy.presentation.feature.pet.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ru.app.pawbuddy.presentation.feature.pet.viewmodel.AddPetViewModel
import ru.app.pawbuddy.databinding.FragmentWeightBinding

class WeightFragment : Fragment() {

    private lateinit var binding: FragmentWeightBinding
    private val addPetViewModel: AddPetViewModel by activityViewModels()
    private var currentWeight: Float = 10.0f  // Значение по умолчанию

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeightBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Если ранее вес не был установлен, используем значение по умолчанию
        if (addPetViewModel.petWeightLiveData.value == null) {
            addPetViewModel.setPetWeight(currentWeight)
        } else {
            currentWeight = addPetViewModel.petWeightLiveData.value!!
        }

        binding.petImage.setImageBitmap(addPetViewModel.petImageLiveData.value!!)

        binding.weightValue.text = String.format("%.1f", currentWeight)
        binding.weightSeekBar.max = 500  // 50 кг (в 10-кратном масштабе)
        binding.weightSeekBar.progress = (currentWeight * 10).toInt()

        binding.weightSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                currentWeight = progress / 10f
                binding.weightValue.text = String.format("%.1f", currentWeight)
                addPetViewModel.setPetWeight(currentWeight)  // Обновляем вес в ViewModel
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
}

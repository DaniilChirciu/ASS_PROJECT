package ru.app.pawbuddy.presentation.feature.pet.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ru.app.pawbuddy.presentation.feature.pet.viewmodel.AddPetViewModel
import ru.app.pawbuddy.presentation.feature.health.adapter.WeightAdapter
import ru.app.pawbuddy.databinding.FragmentSizeBinding
import kotlin.random.Random

class SizeFragment : Fragment() {

    private lateinit var binding: FragmentSizeBinding
    private lateinit var adapter: WeightAdapter
    private val addPetViewModel: AddPetViewModel by activityViewModels()

    private val sizeList = listOf(
        "Очень маленький\nдо 25 см",
        "Маленький\n26-50 см",
        "Средний\n51-75 см",
        "Крупный\n76-100 см",
        "Огромный\n101-125 см"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSizeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = WeightAdapter(sizeList)

        val randomPosition = Random.nextInt(sizeList.size)


        binding.petImage.setImageBitmap(addPetViewModel.petImageLiveData.value!!)

        val carouselLayoutManager = com.mig35.carousellayoutmanager.CarouselLayoutManager(
            com.mig35.carousellayoutmanager.CarouselLayoutManager.HORIZONTAL, false
        ).apply {
            setPostLayoutListener(com.mig35.carousellayoutmanager.CarouselZoomPostLayoutListener())
            setCircleLayout(true)
            maxVisibleItems = 3
        }

        binding.weightCarouselRecyclerView.layoutManager = carouselLayoutManager
        binding.weightCarouselRecyclerView.setHasFixedSize(true)
        binding.weightCarouselRecyclerView.adapter = adapter

        binding.weightCarouselRecyclerView.addOnScrollListener(com.mig35.carousellayoutmanager.CenterScrollListener())

        carouselLayoutManager.addOnItemSelectionListener { adapterPosition ->
            if (adapterPosition in sizeList.indices) {
                adapter.updateCenterPosition(adapterPosition)
                addPetViewModel.setPetSize(sizeList[adapterPosition]) // Сохраняем размер
            }
        }

        binding.weightCarouselRecyclerView.scrollToPosition(randomPosition)
        adapter.updateCenterPosition(randomPosition)

        if (addPetViewModel.petSizeLiveData.value == null) {
            addPetViewModel.setPetSize(sizeList[randomPosition]) // Сохраняем в ViewModel
        }
    }
}

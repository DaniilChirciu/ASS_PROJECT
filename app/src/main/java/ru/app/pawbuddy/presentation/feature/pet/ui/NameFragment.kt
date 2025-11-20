package ru.app.pawbuddy.presentation.feature.pet.ui

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ru.app.pawbuddy.presentation.feature.pet.viewmodel.AddPetViewModel
import ru.app.pawbuddy.databinding.FragmentNameBinding

class NameFragment : Fragment() {

    private lateinit var binding: FragmentNameBinding
    private val addPetViewModel: AddPetViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardView2.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.petName.addTextChangedListener {
            addPetViewModel.setPetName(it.toString().trim())
        }

        restorePreviousData()
    }

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) {
            val croppedImage = getCroppedBitmap(uri)
            addPetViewModel.setPetImage(croppedImage)
            binding.petImage.setImageBitmap(croppedImage)
        } else {
            Log.e("NameFragment", "No media selected")
        }
    }

    private fun getCroppedBitmap(uri: Uri): Bitmap {
        val source = ImageDecoder.createSource(requireContext().contentResolver, uri)
        val bitmap = ImageDecoder.decodeBitmap(source)

        // Обрезаем изображение по центру, чтобы оно выглядело как `centerCrop`
        val width = bitmap.width
        val height = bitmap.height
        val size = width.coerceAtMost(height) // Берем минимальный размер (квадрат)

        val xOffset = (width - size) / 2
        val yOffset = (height - size) / 2

        return Bitmap.createBitmap(bitmap, xOffset, yOffset, size, size)
    }

    private fun restorePreviousData() {
        addPetViewModel.petNameLiveData.value?.let {
            binding.petName.setText(it)
        }
        addPetViewModel.petImageLiveData.value?.let {
            binding.petImage.setImageBitmap(it)
        }
    }
}

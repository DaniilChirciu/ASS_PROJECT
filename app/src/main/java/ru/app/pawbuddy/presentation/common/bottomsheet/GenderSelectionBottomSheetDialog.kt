package ru.app.pawbuddy.presentation.common.bottomsheet

import ru.app.pawbuddy.databinding.GenderDialogBinding


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import ru.app.pawbuddy.data.local.MyPreferenceManager


class GenderSelectionBottomSheetDialog(
    context: Context,
    private val petId: String,
    private val preferenceManager: MyPreferenceManager,
    private val onGenderSelected: (String) -> Unit
) : BottomSheetDialog(context) {

    private lateinit var binding: GenderDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GenderDialogBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)

        binding.genderFemaleBtn.setOnClickListener {
            saveGender("Женский")
        }

        binding.genderMaleBtn.setOnClickListener {
            saveGender("Мужской")
        }
    }

    private fun saveGender(gender: String) {
        preferenceManager.savePetGender(petId, gender)
        onGenderSelected(gender)
        dismiss()
    }
}

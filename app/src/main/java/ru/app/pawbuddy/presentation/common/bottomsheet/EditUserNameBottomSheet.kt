package ru.app.pawbuddy.presentation.common.bottomsheet

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.app.pawbuddy.databinding.FragmentBottomSheetDialogBinding

class EditUserNameBottomSheet(
    private val onSave: (String) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomSheetDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.title.text = "Введите ваше имя"
        binding.editText.hint = "Имя"
        binding.editText.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME

        // Кнопка "Сохранить"
        binding.saveButton.setOnClickListener {
            val newName = binding.editText.text.toString().trim()
            if (newName.isEmpty()) {
                Toast.makeText(requireContext(), "Введите имя", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            onSave(newName)
            dismiss()
        }
    }
}

package ru.app.pawbuddy.presentation.feature.main.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import ru.app.pawbuddy.R
import ru.app.pawbuddy.presentation.feature.user.UserActivity
import ru.app.pawbuddy.presentation.feature.pet.adapter.PetsAdapter
import ru.app.pawbuddy.presentation.feature.pet.ui.AddPetActivity
import ru.app.pawbuddy.data.local.MyPreferenceManager
import ru.app.pawbuddy.databinding.FragmentMainBinding
import ru.app.pawbuddy.domain.model.PetData
import ru.app.pawbuddy.presentation.feature.nutrition.ui.NutritionActivity
import ru.app.pawbuddy.presentation.feature.pet.ui.PetWalkActivity
import ru.app.pawbuddy.presentation.feature.health.ui.PetHealthActivity
import ru.app.pawbuddy.presentation.util.base64ToBitmap
import ru.app.pawbuddy.presentation.feature.report.ui.ReportActivity

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var adapter: PetsAdapter
    private lateinit var snapHelper: PagerSnapHelper

    // Текущий ID питомца, выбранного по центру
    private var currentPetId: String? = null

    // Список питомцев
    private var petList: List<PetData> = emptyList()

    // Менеджер предпочтений для работы с сохранёнными питомцами
    private lateinit var prefManager: MyPreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Основная логика после создания View
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = MyPreferenceManager(requireContext())

        // Кнопка для перехода к созданию нового питомца
        binding.addPetBtn.setOnClickListener {
            requireActivity().startActivity(Intent(requireActivity(), AddPetActivity::class.java))
        }
        binding.cardView.setOnClickListener {
            requireActivity().startActivity(Intent(requireActivity(), UserActivity::class.java))
        }
        binding.mainContent.nutritionBtn.setOnClickListener {
            val intent = Intent(requireActivity(), NutritionActivity::class.java)
            intent.putExtra("petId",currentPetId)
            requireActivity().startActivity(intent)
        }

        binding.mainContent.activityBtn.setOnClickListener {
            val intent = Intent(requireActivity(), PetWalkActivity::class.java)
            intent.putExtra("petId",currentPetId)
            requireActivity().startActivity(intent)
        }

        binding.mainContent.healthBtn.setOnClickListener {
            val intent = Intent(requireActivity(), PetHealthActivity::class.java)
            intent.putExtra("petId",currentPetId)
            requireActivity().startActivity(intent)
        }

        binding.mainContent.sendReportBtn.setOnClickListener {
            val intent = Intent(requireActivity(), ReportActivity::class.java)
            intent.putExtra("petId",currentPetId)
            requireActivity().startActivity(intent)
        }



        loadUserData()





        // Настраиваем RecyclerView (горизонтальный)
        binding.mainContent.petsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // Подключаем SnapHelper
        snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.mainContent.petsRecyclerView)

        // Следим за остановкой скролла, чтобы обновлять currentPetId
        binding.mainContent.petsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val centerView = snapHelper.findSnapView(binding.mainContent.petsRecyclerView.layoutManager)
                    val position = centerView?.let {
                        (binding.mainContent.petsRecyclerView.layoutManager as LinearLayoutManager)
                            .getPosition(it)
                    }
                    if (position != null && position != RecyclerView.NO_POSITION) {
                        currentPetId = petList[position].petId
                        Log.d("MainFragment", "currentPetId обновлён: $currentPetId")
                    }
                }
            }
        })
    }

    private fun loadUserData() {
        val userName = prefManager.getString("userName")
        val userPhoto = prefManager.getString("userPhoto")

        if (userName.equals("Введите имя") || userName.isNullOrEmpty()){
            binding.userName.text = "Нажми чтобы настроить"
        }else{
            binding.userName.text = userName
        }


        if (!userPhoto.isNullOrEmpty()) {
            val bitmap = base64ToBitmap(userPhoto)
            bitmap?.let { binding.userImage.setImageBitmap(it) }
        }else{
            binding.userImage.setImageResource(R.drawable.user_placeholder)
        }

    }

    // При возврате в фрагмент обновляем список
    override fun onResume() {
        super.onResume()

        loadUserData()

        // 1. Загружаем список питомцев из SharedPreferences
        petList = prefManager.getAllPetData()

        // 2. Проверяем, пуст ли список
        if (petList.isEmpty()) {
            // Показать заглушку emptyList
            binding.emptyList.root.visibility = View.VISIBLE
            // Скрыть основной контент
            binding.mainContent.root.visibility = View.GONE
            return
        } else {
            // Скрыть заглушку
            binding.emptyList.root.visibility = View.GONE
            // Показать основной контент
            binding.mainContent.root.visibility = View.VISIBLE
        }

        // 3. Обновляем количество питомцев
        binding.mainContent.petsCount.text = petList.size.toString()

        // 4. Если адаптер уже создан, обновляем данные, иначе создаём
        if (::adapter.isInitialized) {
            adapter.updateData(petList)
        } else {
            adapter = PetsAdapter(petList,requireActivity()) { clickedPet ->
                // При клике можно обновлять currentPetId, если нужно
                currentPetId = clickedPet.petId

            }
            binding.mainContent.petsRecyclerView.adapter = adapter
        }

        // 5. Ставим текущего питомца (по центру) после загрузки списка
        binding.mainContent.petsRecyclerView.post {
            val centerView = snapHelper.findSnapView(binding.mainContent.petsRecyclerView.layoutManager)
            val position = centerView?.let {
                (binding.mainContent.petsRecyclerView.layoutManager as LinearLayoutManager).getPosition(it)
            }
            if (position != null && position != RecyclerView.NO_POSITION) {
                currentPetId = petList[position].petId
                Log.d("MainFragment", "currentPetId обновлён в onResume: $currentPetId")
            }
        }
    }
}

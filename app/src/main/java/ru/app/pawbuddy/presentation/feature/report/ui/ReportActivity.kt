package ru.app.pawbuddy.presentation.feature.report.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ru.app.pawbuddy.data.local.MyPreferenceManager
import ru.app.pawbuddy.R
import ru.app.pawbuddy.databinding.ActivityReportBinding
import ru.app.pawbuddy.presentation.feature.report.adapter.ReportAdapter

class ReportActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReportBinding
    private lateinit var preferenceManager: MyPreferenceManager
    private lateinit var reportAdapter: ReportAdapter
    private var petId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupEmptyLayout()

        preferenceManager = MyPreferenceManager(this)
        petId = intent.getStringExtra("petId") ?: return

        binding.back.setOnClickListener { finish() }

        reportAdapter = ReportAdapter(this, preferenceManager, petId!!)
        binding.recyclerViewReports.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewReports.adapter = reportAdapter

        loadReports()

        binding.addReports.setOnClickListener {
            val intent = Intent(this, AddReportActivity::class.java)
            intent.putExtra("petId", petId)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadReports()
    }

    fun loadReports() {
        val reports = petId?.let { preferenceManager.getReportsForPet(it) } ?: emptyList()
        reportAdapter.submitList(reports)

        // Показываем пустой layout, если список отчетов пуст
        if (reports.isEmpty()) {
            binding.emptyLayout.root.visibility = View.VISIBLE
            binding.recyclerViewReports.visibility = View.GONE
        } else {
            binding.emptyLayout.root.visibility = View.GONE
            binding.recyclerViewReports.visibility = View.VISIBLE
        }
    }

    private fun setupEmptyLayout() {
        binding.emptyLayout.apply {
            emptyImage.setImageResource(R.drawable.empty_ic)
            emptyTitle.text = "Список отчетов пуст"
            emptyDesc.text = "Отправьте свой первый отчет"
        }
    }
}

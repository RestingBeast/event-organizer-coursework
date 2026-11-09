package com.example.eventadmin

import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.eventadmin.databinding.ActivitySessionFormBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class SessionFormActivity: AppCompatActivity() {
    private lateinit var binding: ActivitySessionFormBinding
    private fun showTimePicker() {
        val time: String = binding.txtTime.text.toString()
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(
                if (time.isEmpty()) LocalTime.now().hour else LocalTime.parse(time).hour
            )
            .setMinute(
                if (time.isEmpty()) LocalTime.now().minute else LocalTime.parse(time).minute
            )
            .setTitleText(R.string.select_time)
            .build()
        picker.addOnPositiveButtonClickListener {
            binding.txtTime.setText(LocalTime.of(picker.hour, picker.minute)
                .format(DateTimeFormatter.ofPattern("HH:mm")))
        }
        picker.show(supportFragmentManager, "time_picker")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            )
        )
        binding = ActivitySessionFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val typeAdapter = ArrayAdapter.createFromResource(this,
            R.array.session_types, android.R.layout.simple_spinner_item)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerType.adapter = typeAdapter
        binding.txtTime.setOnClickListener { showTimePicker() }
        binding.inputTime.setEndIconOnClickListener { showTimePicker() }
    }

    override fun onResume() {
        super.onResume()
    }
}
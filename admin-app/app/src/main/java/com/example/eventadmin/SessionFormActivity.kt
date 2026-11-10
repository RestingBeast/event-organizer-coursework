package com.example.eventadmin

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import android.widget.ArrayAdapter
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.eventadmin.databinding.ActivitySessionFormBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class SessionFormActivity: AppCompatActivity() {
    private lateinit var binding: ActivitySessionFormBinding
    private lateinit var dbHelper: DbHelper
    private var eventId = -1L
    private var sessionId = -1L
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
            binding.inputTime.error = null
        }
        picker.show(supportFragmentManager, "time_picker")
    }
    private fun validateFields(
        name: String,
        time: String,
        speaker: String,
        room: String,
        desc: String,
    ): Boolean {
        var isValid = true
        if (!validateField(name, binding.inputName,
                getString(R.string.session_name_required)))
            isValid = false
        if (!validateField(time, binding.inputTime,
            getString(R.string.time_required)))
            isValid = false
        if (!validateOptionalField(speaker, binding.inputSpeaker))
            isValid = false
        if (!validateOptionalField(room, binding.inputRoom))
            isValid = false
        if (!validateDesc(desc, binding.inputDesc))
            isValid = false
        return isValid
    }
    private fun clearError() {
        binding.inputName.error = null
        binding.inputTime.error = null
        binding.inputSpeaker.error = null
        binding.inputRoom.error = null
        binding.inputDesc.error = null
    }
    private fun saveSession(v: View) {
        clearError()
        val name = binding.txtName.text.toString().trim()
        val time = binding.txtTime.text.toString().trim()
        val speaker = binding.txtSpeaker.text.toString().trim()
        val room = binding.txtRoom.text.toString().trim()
        val desc = binding.txtDesc.text.toString().trim()
        val type = binding.spinnerType.selectedItem.toString()
        if (!validateFields(name, time, speaker, room, desc))
            return
        val session = Session(
            name = name,
            time = LocalTime.parse(time),
            eventId = eventId,
            speaker = speaker.ifEmpty { null },
            room = room.ifEmpty { null },
            desc = desc.ifEmpty { null },
            type = type
        )
        if (sessionId == -1L) {
            if (dbHelper.insertSession(session) == -1L)
                showToast(getString(R.string.session_insert_error), this)
            else
                finish()
        } else {
            if (dbHelper.updateSession(sessionId, session) == 0)
                showToast(getString(R.string.update_session_error), this)
            else
                finish()
        }

    }
    private fun loadSession(id: Long) {
        val session = dbHelper.getSessionById(id)
        session ?: run {
            finish()
            return
        }
        binding.txtFormTitle.text = getString(R.string.title_edit_session)
        binding.txtName.setText(session.name)
        binding.txtTime.setText(session.time.format(DateTimeFormatter.ofPattern("HH:mm")))
        binding.txtSpeaker.setText(session.speaker)
        binding.txtRoom.setText(session.room)
        binding.txtDesc.setText(session.desc)
        binding.spinnerType.setSelection(resources.getStringArray(R.array.session_types).indexOf(session.type))
        binding.btnDelete.visibility = VISIBLE
    }
    private fun deleteSession(id: Long): Boolean {
        if (dbHelper.deleteSession(id) == 0) {
            showToast(getString(R.string.delete_session_error), this)
            return false
        }
        return true
    }
    private fun showDeleteDialog(id: Long) {
        val dialog = MaterialAlertDialogBuilder(this)
        dialog.setTitle(R.string.delete_session_title)
            .setMessage(R.string.delete_session_message)
            .setCancelable(false)
            .setPositiveButton(R.string.dialog_yes) { _,_ ->
                if (deleteSession(id))
                    finish()
            }
            .setNegativeButton(R.string.back, { _,_ -> })
        val alert = dialog.create()
        alert.show()
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
        binding.btnSave.setOnClickListener(::saveSession)
        dbHelper = DbHelper(this)
        eventId = intent.getLongExtra(EXTRA_EVENT_ID, -1L)
        sessionId = intent.getLongExtra(EXTRA_SESSION_ID, -1L)
        if (sessionId != -1L) {
            loadSession(sessionId)
            binding.btnDelete.setOnClickListener {
                showDeleteDialog(sessionId)
            }
        }
    }
}
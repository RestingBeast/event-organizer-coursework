package com.example.eventadmin

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.eventadmin.databinding.ActivityEventFormBinding
import java.time.LocalDate
import com.google.android.material.textfield.TextInputLayout
import android.view.View.VISIBLE
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.format.DateTimeFormatter

const val MAX_FIELD_LENGTH = 64
const val EXTRA_EVENT_ID = "event_id"

class EventFormActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEventFormBinding
    private lateinit var dbHelper: EventDbHelper
    private var editingId = -1L
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    private fun validateField(
        text: String,
        input: TextInputLayout,
        error: String
    ): Boolean {
        if (text.isBlank()) {
            input.error = error
            return false
        } else if (text.length > MAX_FIELD_LENGTH) {
            input.error = getString(R.string.error_too_long, MAX_FIELD_LENGTH)
            return false
        }
        return true
    }
    private fun validateDesc(desc: String): Boolean{
        val maxLength = resources.getInteger(R.integer.max_desc_length)
        if (desc.length > maxLength) {
            binding.inputDesc.error = getString(R.string.error_too_long, maxLength)
            return false
        }
        return true
    }
    private fun validateEmail(email: String): Boolean{
        if (!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.inputEmail.error = getString(R.string.email_invalid)
            return false
        } else if (email.length > MAX_FIELD_LENGTH) {
            binding.inputEmail.error = getString(R.string.error_too_long, MAX_FIELD_LENGTH)
            return false
        }
        return true
    }
    private fun validatePhone(phone: String): Boolean{
        if (!phone.isEmpty() && !Patterns.PHONE.matcher(phone).matches()) {
            binding.inputPhone.error = getString(R.string.phone_invalid)
            return false
        } else if (phone.length > MAX_FIELD_LENGTH) {
            binding.inputPhone.error = getString(R.string.error_too_long, MAX_FIELD_LENGTH)
            return false
        }
        return true
    }
    private fun validateFields(
        name: String,
        location: String,
        organizer: String,
        desc: String,
        email: String,
        phone: String
    ): Boolean {
        var isValid = true
        if (!validateField(name, binding.inputName,
                getString(R.string.name_required)))
            isValid = false
        if (!validateField(location, binding.inputLocation,
                getString(R.string.location_required)))
            isValid = false
        if (!validateField(organizer, binding.inputOrganizer,
                getString(R.string.organizer_required)))
            isValid = false
        if (!validateDesc(desc))
            isValid = false
        if (!validateEmail(email))
            isValid = false
        if (!validatePhone(phone))
            isValid = false
        return (isValid)
    }
    private fun clearError() {
        binding.inputName.error = null
        binding.inputLocation.error = null
        binding.inputOrganizer.error = null
        binding.inputDesc.error = null
        binding.inputEmail.error = null
        binding.inputPhone.error = null
    }
    private fun storeEvent(event: Event): Boolean {
        when (editingId) {
            -1L -> {
                when (dbHelper.insert(event)) {
                    -1L -> showToast(getString(R.string.insert_error))
                    -2L -> showToast(getString(R.string.duplicate_error))
                    else -> return true
                }
            }
            else -> {
                when (dbHelper.update(editingId, event)) {
                    0 -> showToast(getString(R.string.update_error))
                    -2 -> showToast(getString(R.string.duplicate_error))
                    else -> return true
                }
            }
        }
        return false
    }
    private fun showConfirmDialog(event: Event) {
        val dialog = MaterialAlertDialogBuilder(this)
        dialog.setTitle(getString(R.string.confirm_title))
            .setMessage(getString(
                R.string.confirm_message,
                event.name,
                event.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                event.location,
                event.organizer,
                event.type,
                event.status,
                event.description ?: getString(R.string.not_provided),
                event.email ?: getString(R.string.not_provided),
                event.phone ?: getString(R.string.not_provided)
            ))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.confirm_save)) {
                dialog,_ ->
                    if (storeEvent(event)) {
                        dialog.dismiss()
                        finish()
                    }
            }
            .setNegativeButton(getString(R.string.back)) {
                dialog,_ ->
                    dialog.dismiss()
            }
        val alert = dialog.create()
        alert.show()
    }
    private fun saveEvent(v: View) {
        clearError()
        val name = binding.txtName.text.toString().trim()
        val day = binding.datePicker.dayOfMonth
        val month = binding.datePicker.month
        val year = binding.datePicker.year
        val date = LocalDate.of(year,  month + 1, day)
        val location = binding.txtLocation.text.toString().trim()
        val organizer = binding.txtOrganizer.text.toString().trim()
        val type = binding.spinnerType.selectedItem.toString()
        val status = binding.spinnerStatus.selectedItem.toString()
        val desc = binding.txtDesc.text.toString().trim()
        val email = binding.txtEmail.text.toString().trim()
        val phone = binding.txtPhone.text.toString().trim()
        if (!validateFields(name, location, organizer, desc, email, phone))
            return
        val event = Event(
            name = name,
            date = date,
            location = location,
            organizer = organizer,
            type = type,
            status = status,
            description = desc.ifEmpty { null },
            email = email.ifEmpty { null },
            phone = phone.ifEmpty { null }
        )
        showConfirmDialog(event)
    }
    private fun deleteEvent(eventId: Long) {
        if (dbHelper.delete(eventId) == 0)
            showToast(getString(R.string.delete_error))
    }
    private fun confirmDelete(eventId: Long) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle(getString(R.string.dialog_title))
            .setMessage(getString(R.string.dialog_message))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.dialog_yes)) {
                    dialog,_ ->
                deleteEvent(eventId)
                dialog.dismiss()
                finish()
            }
            .setNegativeButton(getString(android.R.string.cancel)) {
                    dialog,_ ->
                dialog.dismiss()
            }
        val alert = alertDialog.create()
        alert.show()
    }
    private fun loadEvent(editingId: Long) {
        val event = dbHelper.getById(editingId)
        event ?: run {
            finish()
            return
        }
        binding.txtFormTitle.setText(R.string.title_edit_event)
        binding.txtName.setText(event.name)
        binding.txtLocation.setText(event.location)
        binding.txtOrganizer.setText(event.organizer)
        binding.txtDesc.setText(event.description)
        binding.txtEmail.setText(event.email)
        binding.txtPhone.setText(event.phone)
        binding.datePicker.updateDate(
            event.date.year,
            event.date.monthValue - 1,
            event.date.dayOfMonth
        )
        binding.spinnerType.setSelection(
            resources.getStringArray (
                R.array.event_types
            ).indexOf(event.type)
        )
        binding.spinnerStatus.setSelection(
            resources.getStringArray (
                R.array.event_statuses
            ).indexOf(event.status)
        )
        binding.btnDelete.visibility = VISIBLE
        binding.btnDelete.setOnClickListener {
            confirmDelete(event.id)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        binding = ActivityEventFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dbHelper = EventDbHelper(this)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val typeAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.event_types,
            android.R.layout.simple_spinner_item
        )
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerType.adapter = typeAdapter
        val statusAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.event_statuses,
            android.R.layout.simple_spinner_item
        )
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerStatus.adapter = statusAdapter
        binding.btnSave.setOnClickListener(::saveEvent)
        editingId = intent.getLongExtra(EXTRA_EVENT_ID, -1L)
        if (editingId != -1L)
            loadEvent(editingId)
    }
}
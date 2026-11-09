package com.example.eventadmin

import com.google.android.material.textfield.TextInputLayout

const val MAX_FIELD_LENGTH = 64

fun validateField(
    text: String,
    input: TextInputLayout,
    error: String
): Boolean {
    if (text.isBlank()) {
        input.error = error
        return false
    } else if (text.length > MAX_FIELD_LENGTH) {
        input.error = input.context.getString(R.string.error_too_long, MAX_FIELD_LENGTH)
        return false
    }
    return true
}

fun validateDesc(desc: String, input: TextInputLayout): Boolean {
    val maxLength = input.context.resources.getInteger(R.integer.max_desc_length)
    if (desc.length > maxLength) {
        input.error = input.context.getString(R.string.error_too_long, maxLength)
        return false
    }
    return true
}

fun validateOptionalField(text: String, input: TextInputLayout): Boolean {
    if (text.length > MAX_FIELD_LENGTH) {
        input.error = input.context.getString(R.string.error_too_long, MAX_FIELD_LENGTH)
        return false
    }
    return true
}


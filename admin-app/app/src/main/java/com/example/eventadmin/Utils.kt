package com.example.eventadmin

import android.content.Context
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import com.google.android.material.appbar.MaterialToolbar

fun showToast(message: String, context: Context) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun setUpSearch(toolbar: MaterialToolbar, hint: String, onSearch: (String) -> Unit) {
    val searchView = toolbar.menu.findItem(R.id.action_search).actionView as SearchView
    searchView.queryHint = hint
    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            searchView.clearFocus()
            return true
        }
        override fun onQueryTextChange(newText: String?): Boolean {
            onSearch(newText.orEmpty().trim())
            return true
        }
    })
}
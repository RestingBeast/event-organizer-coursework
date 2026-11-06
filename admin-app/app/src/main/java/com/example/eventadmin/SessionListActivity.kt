package com.example.eventadmin

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventadmin.databinding.ActivitySessionListBinding
import android.view.View.VISIBLE
import android.view.View.GONE

class SessionListActivity: AppCompatActivity() {
    private lateinit var binding: ActivitySessionListBinding
    private lateinit var dbHelper: DbHelper
    private fun showSessions() {
        val sessions = dbHelper.getSessionsByEvent(2)
        val adapter = SessionAdapter(sessions)
        binding.recyclerSessions.adapter = adapter
        val count = adapter.itemCount
        if (count == 0)
            binding.txtEmpty.visibility = VISIBLE
        else
            binding.txtEmpty.visibility = GONE
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
        binding = ActivitySessionListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        dbHelper = DbHelper(this)
        binding.recyclerSessions.layoutManager = LinearLayoutManager(this)
    }

    override fun onResume() {
        super.onResume()
        showSessions()
    }
}
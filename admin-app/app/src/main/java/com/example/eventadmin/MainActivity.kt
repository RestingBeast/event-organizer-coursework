package com.example.eventadmin

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.view.View.VISIBLE
import android.view.View.GONE
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventadmin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: EventDbHelper

    private fun showEvents()
    {
        val events = dbHelper.getAll()
        val adapter = EventAdapter(events) {
            event ->
            val intent = Intent(this, EventFormActivity::class.java)
            intent.putExtra(EXTRA_EVENT_ID, event.id)
            startActivity(intent)
        }
        binding.recyclerEvents.adapter = adapter
        val count = adapter.itemCount
        binding.txtEventCount.text = getString(
            R.string.event_count,
            count,
            when (count) {
                1 -> getString(R.string.single_event)
                else -> getString(R.string.multiple_event)
            }
        )
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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        dbHelper = EventDbHelper(this)
        binding.fabAddEvent.setOnClickListener {
            val intent = Intent(this, EventFormActivity::class.java)
            startActivity(intent)
        }
        binding.recyclerEvents.layoutManager = LinearLayoutManager(this)
    }

    override fun onResume() {
        super.onResume()
        showEvents()
    }
}
package com.example.eventadmin

import android.content.Intent
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
import java.time.format.DateTimeFormatter

const val EXTRA_EVENT_ID = "event_id"
const val EXTRA_SESSION_ID = "session_id"
class SessionListActivity: AppCompatActivity() {
    private lateinit var binding: ActivitySessionListBinding
    private lateinit var dbHelper: DbHelper
    private var eventId = -1L
    private var searchQuery = ""
    private fun loadEvent(eventId: Long) {
        val event = dbHelper.getEventById(eventId)
        event ?: run {
            finish()
            return
        }
        binding.txtEventName.text = event.name
        binding.txtEventInfo.text = getString(R.string.info_string, event.date.format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy")), event.type, event.status)
        binding.txtEventPlace.text = getString(R.string.meta_string, event.location, event.organizer)
        binding.txtEventContact.visibility = VISIBLE
        if (event.email != null && event.phone != null)
            binding.txtEventContact.text = getString(R.string.meta_string, event.email, event.phone)
        else if (event.email != null)
            binding.txtEventContact.text = event.email
        else if (event.phone != null)
            binding.txtEventContact.text = event.phone
        else
            binding.txtEventContact.visibility = GONE
        if (event.desc != null) {
            binding.txtEventDesc.visibility = VISIBLE
            binding.txtEventDesc.text = event.desc
        }
        else
            binding.txtEventDesc.visibility = GONE
        binding.txtSessionCount.text = getString(R.string.item_count, event.sessions,
            getString(if (event.sessions == 1) R.string.single_session else R.string.multiple_sessions))
    }
    private fun showSessions(id: Long) {
        val sessions = dbHelper.getSessionsByEvent(id, searchQuery)
        val adapter = SessionAdapter(sessions) {
         session ->
         val intent = Intent(this, SessionFormActivity::class.java)
         intent.putExtra(EXTRA_SESSION_ID, session.id)
         intent.putExtra(EXTRA_EVENT_ID, id)
         startActivity(intent)
        }
        binding.recyclerSessions.adapter = adapter
        val count = adapter.itemCount
        if (count == 0) {
            binding.txtEmpty.visibility = VISIBLE
            binding.txtEmpty.text = getString(
                if (searchQuery.isEmpty()) R.string.no_session_text
                else R.string.no_sessions_result
            )
        }
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
        eventId = intent.getLongExtra(EXTRA_EVENT_ID, -1L)
        if (eventId != -1L) {

            binding.btnEditEvent.setOnClickListener {
                val intent = Intent(this, EventFormActivity::class.java)
                intent.putExtra(EXTRA_EVENT_ID, eventId)
                startActivity(intent)
            }
        }
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        binding.fabAddSession.setOnClickListener {
            val intent = Intent(this, SessionFormActivity::class.java)
            intent.putExtra(EXTRA_EVENT_ID, eventId)
            startActivity(intent)
        }
        setUpSearch(binding.toolbar, getString(R.string.search_hint)) {
            query ->
            searchQuery = query
            showSessions(eventId)
        }
    }

    override fun onResume() {
        super.onResume()
        loadEvent(eventId)
        showSessions(eventId)
    }
}
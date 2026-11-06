package com.example.eventadmin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.eventadmin.databinding.ItemSessionBinding
import java.time.format.DateTimeFormatter

class SessionAdapter (
    private val sessions: List<Session>
): RecyclerView.Adapter<SessionAdapter.SessionViewHolder>() {
    class SessionViewHolder(val binding: ItemSessionBinding) :
        RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val binding = ItemSessionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SessionViewHolder(binding)
    }
    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val session = sessions[position]
        val context = holder.binding.root.context
        holder.binding.txtSessionName.text = session.name
        holder.binding.txtTime.text = session.time.format(DateTimeFormatter.ofPattern("HH:mm"))
        holder.binding.txtSessionMeta.text = "Hello"
        holder.binding.txtSessionType.text = session.type
    }
    override fun getItemCount(): Int = sessions.size
}
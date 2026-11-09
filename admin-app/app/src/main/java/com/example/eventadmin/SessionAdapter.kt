package com.example.eventadmin

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.eventadmin.databinding.ItemSessionBinding
import java.time.format.DateTimeFormatter

class SessionAdapter (
    private val sessions: List<Session>,
    private val onClick: (Session) -> Unit
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
        holder.binding.txtSessionMeta.visibility = VISIBLE
        if (session.room != null && session.speaker != null)
            holder.binding.txtSessionMeta.text = context.getString(R.string.meta_string,
                session.room, session.speaker)
        else if (session.room != null)
            holder.binding.txtSessionMeta.text = session.room
        else if (session.speaker != null)
            holder.binding.txtSessionMeta.text = session.speaker
        else
            holder.binding.txtSessionMeta.visibility = GONE
        holder.binding.txtSessionType.text = session.type
        holder.binding.root.setOnClickListener {
            onClick(session)
        }
    }
    override fun getItemCount(): Int = sessions.size
}
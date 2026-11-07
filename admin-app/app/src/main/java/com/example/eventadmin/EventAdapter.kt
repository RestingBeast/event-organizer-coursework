package com.example.eventadmin

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.eventadmin.databinding.ItemEventBinding
import java.time.format.DateTimeFormatter

class EventAdapter(
    private val events: List<Event>,
    private val onClick: (Event) -> Unit
    ):
    RecyclerView.Adapter<EventAdapter.EventViewHolder>() {
    class EventViewHolder(val binding: ItemEventBinding) :
        RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, type: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        val context = holder.binding.root.context
        holder.binding.txtEventName.text = event.name
        holder.binding.txtDay.text = event.date.format(DateTimeFormatter.ofPattern("dd"))
        holder.binding.txtMonth.text = event.date.format(DateTimeFormatter.ofPattern("MMM"))
        holder.binding.txtEventType.text = event.type
        holder.binding.txtEventMeta.text = context.getString(
            R.string.meta_string,
            event.location,
            event.organizer
        )
        holder.binding.root.setOnClickListener {
            onClick(event)
        }
        val txtStatus = holder.binding.txtEventStatus
        txtStatus.text = event.status
        when (event.status) {
            "Upcoming" -> {
                txtStatus.setTextColor(
                    context.getColor(R.color.status_upcoming)
                )
                txtStatus.setBackgroundResource(R.drawable.bg_status_upcoming)
            }
            "Completed" -> {
                txtStatus.setTextColor(
                    context.getColor(R.color.status_completed)
                )
                txtStatus.setBackgroundResource(R.drawable.bg_status_completed)
            }

            "Cancelled" -> {
                txtStatus.setTextColor(
                    context.getColor(R.color.status_cancelled)
                )
                txtStatus.setBackgroundResource(R.drawable.bg_status_cancelled)
            }
            else -> {
                txtStatus.setTextColor(
                    context.getColor(R.color.status_completed)
                )
                txtStatus.setBackgroundResource(R.drawable.bg_status_completed)
            }
        }
        holder.binding.txtSessionCount.text = event.sessions.toString()
        holder.binding.txtSessionLabel.text = when (event.sessions) {
            1 -> context.getString(R.string.single_session)
            else -> context.getString(R.string.multiple_sessions)
        }

    }
    override fun getItemCount(): Int = events.size
}
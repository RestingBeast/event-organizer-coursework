package com.example.eventadmin

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.example.eventadmin.DbContract.EventEntry
import com.example.eventadmin.DbContract.SessionEntry
import java.time.LocalTime

class DbHelper(private val context: Context):
        SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    private val selectEvent = """
        |SELECT *,
        |   (SELECT 
        |       COUNT(*) 
        |   FROM ${SessionEntry.TABLE_NAME} AS s
        |   WHERE s.${SessionEntry.COL_EVENT_ID} = e.${BaseColumns._ID}
        |   ) AS ${EventEntry.COL_SESSIONS}
        |FROM
        |   ${EventEntry.TABLE_NAME} AS e
    """.trimMargin()
    override fun onCreate(db: SQLiteDatabase?) {
        val events = """
            |CREATE TABLE ${EventEntry.TABLE_NAME} (
            |   ${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            |   ${EventEntry.COL_NAME} TEXT NOT NULL,
            |   ${EventEntry.COL_DATE} TEXT NOT NULL,
            |   ${EventEntry.COL_LOCATION} TEXT,
            |   ${EventEntry.COL_ORGANIZER} TEXT,
            |   ${EventEntry.COL_TYPE} TEXT,
            |   ${EventEntry.COL_STATUS} TEXT,
            |   ${EventEntry.COL_DESC} TEXT,
            |   ${EventEntry.COL_EMAIL} TEXT,
            |   ${EventEntry.COL_PHONE} TEXT
            |);
        """.trimMargin()
        val sessions = """
            |CREATE TABLE ${SessionEntry.TABLE_NAME} (
            |   ${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            |   ${SessionEntry.COL_EVENT_ID} INTEGER NOT NULL REFERENCES ${EventEntry.TABLE_NAME}(${BaseColumns._ID}) ON DELETE CASCADE,
            |   ${SessionEntry.COL_NAME} TEXT NOT NULL,
            |   ${SessionEntry.COL_TIME} TEXT NOT NULL,
            |   ${SessionEntry.COL_TYPE} TEXT NOT NULL,
            |   ${SessionEntry.COL_SPEAKER} TEXT,
            |   ${SessionEntry.COL_ROOM} TEXT,
            |   ${SessionEntry.COL_DESC} TEXT
            |);
        """.trimMargin()
        db?.execSQL(events)
        db?.execSQL(sessions)
        Log.d("SQLite DB", "tables created!")
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val events = "DROP TABLE IF EXISTS ${EventEntry.TABLE_NAME};"
        val sessions = "DROP TABLE IF EXISTS ${SessionEntry.TABLE_NAME};"
        db?.execSQL(sessions)
        db?.execSQL(events)
        Log.d("SQLite DB", "tables upgraded!")
        onCreate(db)
    }
    override fun onConfigure(db: SQLiteDatabase?) {
        super.onConfigure(db)
        db?.setForeignKeyConstraintsEnabled(true)
    }
    private fun toEvent(cursor: Cursor): Event {
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
        val name = cursor.getString(cursor.getColumnIndexOrThrow(EventEntry.COL_NAME))
        val date = cursor.getString(cursor.getColumnIndexOrThrow(EventEntry.COL_DATE))
        val location = cursor.getString(cursor.getColumnIndexOrThrow(EventEntry.COL_LOCATION))
        val organizer = cursor.getString(cursor.getColumnIndexOrThrow(EventEntry.COL_ORGANIZER))
        val type = cursor.getString(cursor.getColumnIndexOrThrow(EventEntry.COL_TYPE))
        val status = cursor.getString(cursor.getColumnIndexOrThrow(EventEntry.COL_STATUS))
        val desc = cursor.getString(cursor.getColumnIndexOrThrow(EventEntry.COL_DESC))
        val email = cursor.getString(cursor.getColumnIndexOrThrow(EventEntry.COL_EMAIL))
        val phone = cursor.getString(cursor.getColumnIndexOrThrow(EventEntry.COL_PHONE))
        val sessions = cursor.getInt(cursor.getColumnIndexOrThrow(EventEntry.COL_SESSIONS))
        return Event(
            id = id,
            name = name,
            date = LocalDate.parse(date),
            location = location,
            organizer = organizer,
            type = type,
            status = status,
            desc = desc,
            email = email,
            phone = phone,
            sessions = sessions
        )
    }
    private fun toSession(cursor: Cursor): Session {
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
        val eventId = cursor.getLong(cursor.getColumnIndexOrThrow(SessionEntry.COL_EVENT_ID))
        val name = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COL_NAME))
        val time = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COL_TIME))
        val speaker = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COL_SPEAKER))
        val room = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COL_ROOM))
        val type = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COL_TYPE))
        val desc = cursor.getString(cursor.getColumnIndexOrThrow(SessionEntry.COL_DESC))
        return Session(
            id = id,
            name = name,
            eventId = eventId,
            time = LocalTime.parse(time),
            speaker = speaker,
            room = room,
            type = type,
            desc = desc
        )
    }
    private fun eventToContentValues(event: Event): ContentValues {
        val values = ContentValues()
        values.put(EventEntry.COL_NAME, event.name)
        values.put(EventEntry.COL_DATE, event.date.format(DateTimeFormatter.ISO_LOCAL_DATE))
        values.put(EventEntry.COL_LOCATION, event.location)
        values.put(EventEntry.COL_ORGANIZER, event.organizer)
        values.put(EventEntry.COL_TYPE, event.type)
        values.put(EventEntry.COL_STATUS, event.status)
        values.put(EventEntry.COL_DESC, event.desc)
        values.put(EventEntry.COL_EMAIL, event.email)
        values.put(EventEntry.COL_PHONE, event.phone)
        return values
    }
    private fun sessionToContentValues(session: Session): ContentValues {
        val values = ContentValues()
        values.put(SessionEntry.COL_EVENT_ID, session.eventId)
        values.put(SessionEntry.COL_NAME, session.name)
        values.put(SessionEntry.COL_TIME, session.time.format(DateTimeFormatter.ISO_LOCAL_TIME))
        values.put(SessionEntry.COL_SPEAKER, session.speaker)
        values.put(SessionEntry.COL_ROOM, session.room)
        values.put(SessionEntry.COL_TYPE, session.type)
        values.put(SessionEntry.COL_DESC, session.desc)
        return values
    }
    private fun validateEvent(event: Event, id: Long = -1): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor = db.query(
            EventEntry.TABLE_NAME,
            null,
            "${EventEntry.COL_NAME} = ? COLLATE NOCASE AND ${EventEntry.COL_DATE} = ? "
                    + "AND ${BaseColumns._ID} != ?",
            arrayOf(
                event.name,
                event.date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                id.toString()
            ),
            null,
            null,
            null
        )
        if (cursor.count != 0) {
            cursor.close()
            return (false)
        }
        cursor.close()
        return (true)
    }
    fun getAllEvents(): ArrayList<Event> {
        val db = this.readableDatabase
        val query = selectEvent + " ORDER BY ${EventEntry.COL_DATE};"

        val cursor = db.rawQuery(query, null)
        val eventList = ArrayList<Event>()
        if (cursor.moveToFirst()) {
            do {
                val event = toEvent(cursor)
                eventList.add(event)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return eventList
    }
    fun searchEvents(
        text: String,
        start: LocalDate? = null,
        end: LocalDate? = null ,
        organizer: String? = null
    ): ArrayList<Event> {
        val eventList = ArrayList<Event>()
        val conditions = ArrayList<String>()
        val args = ArrayList<String>()
        if (!text.isEmpty()) {
            conditions.add("(e.${EventEntry.COL_NAME} LIKE ? OR e.${EventEntry.COL_DESC} LIKE ?)")
            args.add("$text%")
            args.add("%$text%")
        }
        if (start != null && end != null) {
            conditions.add("e.${EventEntry.COL_DATE} BETWEEN ? AND ?")
            args.add(start.format(DateTimeFormatter.ISO_LOCAL_DATE))
            args.add(end.format(DateTimeFormatter.ISO_LOCAL_DATE))
        }
        if (organizer != null) {
            conditions.add("e.${EventEntry.COL_ORGANIZER} = ?")
            args.add(organizer)
        }
        val db = this.readableDatabase
        var query = selectEvent
        if (conditions.isNotEmpty())
            query += " WHERE ${conditions.joinToString(" AND ")}"
        query += " ORDER BY e.${EventEntry.COL_DATE}"
        val cursor = db.rawQuery(query, args.toTypedArray())
        if (cursor.moveToFirst()) {
            do {
                val event = toEvent(cursor)
                eventList.add(event)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return eventList
    }
    fun getEventById(id: Long): Event? {
        var event: Event? = null
        val db = this.readableDatabase
        val query = selectEvent + " WHERE ${BaseColumns._ID} = ?;"
        val cursor = db.rawQuery(query, arrayOf(id.toString()))
        if (cursor.moveToFirst())
            event = toEvent(cursor)
        cursor.close()
        return event
    }
    fun insertEvent(event: Event): Long {
        if (!validateEvent(event))
            return (-2)
        val db = this.writableDatabase
        val values = eventToContentValues(event)
        val result = db.insert(EventEntry.TABLE_NAME, null, values)
        db.close()
        if (result != -1L) {
            markSyncPending(context)
            requestSync(context)
        }
        return result
    }
    fun updateEvent(id: Long, event: Event): Int {
        if (!validateEvent(event, id))
            return (-2)
        val db =this.writableDatabase
        val values = eventToContentValues(event)
        val result = db.update(
            EventEntry.TABLE_NAME,
            values,
            "${BaseColumns._ID} = ?",
            arrayOf(id.toString())
        )
        db.close()
        if (result > 0) {
            markSyncPending(context)
            requestSync(context)
        }
        return result
    }
    fun deleteEvent(id: Long): Int {
        val db = this.writableDatabase
        val result = db.delete(
            EventEntry.TABLE_NAME,
            "${BaseColumns._ID} = ?",
            arrayOf(id.toString()))
        db.close()
        if (result > 0) {
            markSyncPending(context)
            requestSync(context)
        }
        return result
    }
    fun getAllSessions(): ArrayList<Session> {
        val db = this.readableDatabase
        val query = "SELECT * FROM ${SessionEntry.TABLE_NAME} ORDER BY ${SessionEntry.COL_TIME}"
        val cursor = db.rawQuery(query, null)
        val sessionList = ArrayList<Session>()
        if (cursor.moveToFirst()) {
            do {
                val session = toSession(cursor)
                sessionList.add(session)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return sessionList
    }
    fun getSessionsByEvent(eventId: Long, text: String): ArrayList<Session> {
        val db = this.readableDatabase
        var query = "SELECT * FROM ${SessionEntry.TABLE_NAME} WHERE ${SessionEntry.COL_EVENT_ID} = ?"
        val args = ArrayList<String>()
        args.add(eventId.toString())
        if (text.isNotEmpty()) {
            query += " AND (${SessionEntry.COL_NAME} LIKE ? OR ${SessionEntry.COL_DESC} LIKE ?)"
            args.add("$text%")
            args.add("%$text%")
        }
        query += " ORDER BY ${SessionEntry.COL_TIME};"
        val cursor = db.rawQuery(query, args.toTypedArray())
        val sessionList = ArrayList<Session>()
        if (cursor.moveToFirst()) {
            do {
                val session = toSession(cursor)
                sessionList.add(session)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return sessionList
    }
    fun getSessionById(id: Long): Session? {
        var session: Session? = null
        val db = this.readableDatabase
        val query = "SELECT * FROM ${SessionEntry.TABLE_NAME} WHERE ${BaseColumns._ID} = ?;"
        val cursor = db.rawQuery(query, arrayOf(id.toString()))
        if (cursor.moveToFirst())
            session = toSession(cursor)
        cursor.close()
        return session
    }
    fun insertSession(session: Session): Long {
        val db = this.writableDatabase
        val values = sessionToContentValues(session)
        val result = db.insert(SessionEntry.TABLE_NAME, null, values)
        db.close()
        if (result != -1L) {
            markSyncPending(context)
            requestSync(context)
        }
        return result
    }
    fun updateSession(id: Long, session: Session): Int {
        val db =this.writableDatabase
        val values = sessionToContentValues(session)
        val result = db.update(
            SessionEntry.TABLE_NAME,
            values,
            "${BaseColumns._ID} = ?",
            arrayOf(id.toString())
        )
        db.close()
        if (result > 0) {
            markSyncPending(context)
            requestSync(context)
        }
        return result
    }
    fun deleteSession(id: Long): Int {
        val db = this.writableDatabase
        val result = db.delete(
            SessionEntry.TABLE_NAME,
            "${BaseColumns._ID} = ?",
            arrayOf(id.toString()))
        db.close()
        if (result > 0) {
            markSyncPending(context)
            requestSync(context)
        }
        return result
    }
    fun getOrganizers(): ArrayList<String> {
        val db = this.readableDatabase
        val query = "SELECT DISTINCT ${EventEntry.COL_ORGANIZER} FROM ${EventEntry.TABLE_NAME}" +
                " WHERE ${EventEntry.COL_ORGANIZER} IS NOT NULL ORDER BY ${EventEntry.COL_ORGANIZER}" +
                " COLLATE NOCASE"
        val cursor = db.rawQuery(query, null)
        val organizers = ArrayList<String>()
        if (cursor.moveToFirst()) {
            do {
                organizers.add(cursor.getString(
                    cursor.getColumnIndexOrThrow(EventEntry.COL_ORGANIZER)))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return organizers
    }
    companion object {
        private const val DB_NAME = "events.db"
        private const val DB_VERSION = 1
    }
}
package com.example.eventadmin

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val DB_NAME = "events.db"
private const val DB_VERSION = 1

class EventDbHelper(context: Context):
        SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    private val tableName = "events"
    private val colID = "id"
    private val colName = "name"
    private val colDate = "date"
    private val colLocation = "location"
    private val colOrganizer = "organizer"
    private val colType = "type"
    private val colStatus = "status"

    override fun onCreate(db: SQLiteDatabase?) {
        val query = """
            |CREATE TABLE $tableName
            |(
            |   $colID INTEGER PRIMARY KEY AUTOINCREMENT,
            |   $colName TEXT,
            |   $colDate TEXT,
            |   $colLocation TEXT,
            |   $colOrganizer TEXT,
            |   $colType TEXT,
            |   $colStatus TEXT
            |);
        """.trimMargin()
        db?.execSQL(query)
        Log.d("SQLite DB", "table created!")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val query = "DROP TABLE IF EXISTS $tableName"
        db?.execSQL(query)
        Log.d("SQLite DB", "table upgraded!")
        onCreate(db)
    }

    private fun toEvent(cursor: Cursor): Event {
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(colID ))
        val name = cursor.getString(cursor.getColumnIndexOrThrow(colName))
        val date = cursor.getString(cursor.getColumnIndexOrThrow(colDate))
        val location = cursor.getString(cursor.getColumnIndexOrThrow(colLocation))
        val organizer = cursor.getString(cursor.getColumnIndexOrThrow(colOrganizer))
        val type = cursor.getString(cursor.getColumnIndexOrThrow(colType))
        val status = cursor.getString(cursor.getColumnIndexOrThrow(colStatus))
        return Event(
            id = id,
            name = name,
            date = LocalDate.parse(date),
            location = location,
            organizer = organizer,
            type = type,
            status = status

        )
    }

    private fun toContentValues(event: Event): ContentValues {
        val values = ContentValues()
        values.put(colName, event.name)
        values.put(colDate, event.date.format(DateTimeFormatter.ISO_LOCAL_DATE))
        values.put(colLocation, event.location)
        values.put(colOrganizer, event.organizer)
        values.put(colType, event.type)
        values.put(colStatus, event.status)
        return values
    }

    fun insert(event: Event): Long {
        val db = this.writableDatabase
        val values = toContentValues(event)
        val result = db.insert(tableName, null, values)
        db.close()
        return result
    }

    fun getAll(): ArrayList<Event> {
        val db = this.readableDatabase
        val query = "SELECT * FROM $tableName ORDER BY $colDate;"

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

    fun getById(id: Long): Event? {
        var event: Event? = null
        val db = this.readableDatabase
        val query = "SELECT * FROM $tableName WHERE $colID = ?;"
        val cursor = db.rawQuery(query, arrayOf(id.toString()))
        if (cursor.moveToFirst())
            event = toEvent(cursor)
        cursor.close()
        return event
    }

    fun update(id: Long, event: Event): Int {
        val db =this.writableDatabase
        val values = toContentValues(event)
        val result = db.update(
            tableName,
            values,
            "$colID = ?",
            arrayOf(id.toString())
        )
        db.close()
        return result
    }

    fun delete(id: Long): Int {
        val db = this.writableDatabase
        val result = db.delete(
            tableName,
            "$colID = ?",
            arrayOf(id.toString()))
        db.close()
        return result
    }
}
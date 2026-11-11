package com.example.eventadmin

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import org.json.JSONArray
import org.json.JSONObject
import com.example.eventadmin.DbContract.EventEntry
import com.example.eventadmin.DbContract.SessionEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.time.format.DateTimeFormatter
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

private const val FIREBASE_URL = "https://event-organizer-coursework-default-rtdb.asia-southeast1.firebasedatabase.app/events.json"
private const val PENDING_FLAG = "pending"
private const val SYNC_FLAG = "sync"
var isUploading = false

fun sessionToJson(session: Session): JSONObject {
    val res = JSONObject()
    res.put("id", session.id)
    res.put(SessionEntry.COL_EVENT_ID, session.eventId)
    res.put(SessionEntry.COL_NAME, session.name)
    res.put(SessionEntry.COL_TIME, session.time.format(DateTimeFormatter.ISO_LOCAL_TIME))
    res.put(SessionEntry.COL_SPEAKER, session.speaker)
    res.put(SessionEntry.COL_ROOM, session.room)
    res.put(SessionEntry.COL_TYPE, session.type)
    res.put(SessionEntry.COL_DESC, session.desc)
    return res

}
fun eventToJson(event: Event, sessions: List<Session>): JSONObject {
    val res = JSONObject()
    res.put("id", event.id)
    res.put(EventEntry.COL_NAME, event.name)
    res.put(EventEntry.COL_LOCATION, event.location)
    res.put(EventEntry.COL_ORGANIZER, event.organizer)
    res.put(EventEntry.COL_DATE, event.date.format(DateTimeFormatter.ISO_LOCAL_DATE))
    res.put(EventEntry.COL_TYPE, event.type)
    res.put(EventEntry.COL_STATUS, event.status)
    res.put(EventEntry.COL_DESC, event.desc)
    res.put(EventEntry.COL_EMAIL, event.email)
    res.put(EventEntry.COL_PHONE, event.phone)
    val sessionArray = JSONArray()
    sessions.forEach { session -> sessionArray.put(sessionToJson(session)) }
    res.put(EventEntry.COL_SESSIONS, sessionArray)
    return res
}

fun buildUploadJson(dbHelper: DbHelper): JSONArray {
    val events = dbHelper.getAllEvents()
    val sessions = dbHelper.getAllSessions().groupBy { it.eventId }
    val res = JSONArray()
    events.forEach { event ->
        res.put(eventToJson(event, sessions[event.id] ?: emptyList()))
    }
    return res
}

suspend fun uploadAll(dbHelper: DbHelper): Boolean = withContext(Dispatchers.IO) {
    val body = buildUploadJson(dbHelper).toString()
    val connection = URL(FIREBASE_URL).openConnection() as HttpURLConnection
    try {
        connection.requestMethod = "PUT"
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
        connection.connectTimeout = 10_000
        connection.readTimeout = 10_000
        connection.getOutputStream()?.use { it.write(body.toByteArray())}
        connection.responseCode == HttpURLConnection.HTTP_OK
    } catch (e: IOException) {
        false
    } finally {
        connection.disconnect()
    }
}

fun markSyncPending(context: Context) {
    context.getSharedPreferences(SYNC_FLAG, Context.MODE_PRIVATE).edit {
        putBoolean(
            PENDING_FLAG, true
        )
    }
}

fun clearSyncPending(context: Context) {
    context.getSharedPreferences(SYNC_FLAG, Context.MODE_PRIVATE).edit {
        putBoolean(
            PENDING_FLAG, false
        )
    }
}

fun isSyncPending(context: Context): Boolean {
    val flag = context.getSharedPreferences(SYNC_FLAG, Context.MODE_PRIVATE).getBoolean(
        PENDING_FLAG, false
    )
    return flag
}

fun hasInternet(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val caps = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
    return (caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED))
}

private val syncScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

fun requestSync(context: Context) {
    val appContext = context.applicationContext
    if (!hasInternet(context) || isUploading || !isSyncPending(context))
        return
    clearSyncPending(context)
    isUploading = true
    syncScope.launch {
        if (!uploadAll(DbHelper(appContext)))
            markSyncPending(appContext)
        isUploading = false
    }
}
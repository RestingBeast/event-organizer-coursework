package com.example.eventadmin

import android.provider.BaseColumns

object DbContract {
    object EventEntry : BaseColumns {
        const val TABLE_NAME = "events"
        const val COL_NAME = "name"
        const val COL_DATE = "date"
        const val COL_LOCATION = "location"
        const val COL_ORGANIZER = "organizer"
        const val COL_TYPE = "type"
        const val COL_STATUS = "status"
        const val COL_DESC = "description"
        const val COL_EMAIL = "email"
        const val COL_PHONE = "phone"
    }
    object SessionEntry : BaseColumns {
        const val TABLE_NAME = "sessions"
        const val COL_EVENT_ID = "event_id"
        const val COL_NAME = "name"
        const val COL_TIME = "time"
        const val COL_SPEAKER = "speaker"
        const val COL_ROOM = "room"
        const val COL_TYPE = "type"
        const val COL_DESC = "description"
    }
}
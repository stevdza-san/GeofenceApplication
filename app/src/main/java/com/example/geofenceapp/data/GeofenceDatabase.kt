package com.example.geofenceapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [GeofenceEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class GeofenceDatabase: RoomDatabase() {

    abstract fun geofenceDao(): GeofenceDao

}
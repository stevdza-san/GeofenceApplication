package com.example.geofenceapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GeofenceDao {

    @Query("SELECT * FROM geofence_table ORDER BY id ASC")
    fun readGeofences(): Flow<MutableList<GeofenceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addGeofence(geofenceEntity: GeofenceEntity)

    @Delete
    suspend fun removeGeofence(geofenceEntity: GeofenceEntity)

}
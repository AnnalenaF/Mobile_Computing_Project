package com.dhbw.cas.integra.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="sprints")
data class Sprint(
    @ColumnInfo(name="isActive")
    val isActive: Int,

    @ColumnInfo(name="startDate")
    val startDate: Long,

    @ColumnInfo(name="endDate")
    val endDate: Long,

    @ColumnInfo(name="id")
    @PrimaryKey(autoGenerate = true) val id: Long = 0
)

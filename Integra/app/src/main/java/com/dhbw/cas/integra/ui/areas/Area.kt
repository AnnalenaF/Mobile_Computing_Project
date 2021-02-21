package com.dhbw.cas.integra.ui.areas

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="areas")
data class Area(
    var text: String,
    var label: Int,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
)
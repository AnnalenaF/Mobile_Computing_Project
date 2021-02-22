package com.dhbw.cas.integra.ui.areas

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName="areas", indices=[Index(value = ["text"], unique = true),
    Index(value=["label"], unique = true)])
data class Area(
    var text: String,
    var label: Int,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
)
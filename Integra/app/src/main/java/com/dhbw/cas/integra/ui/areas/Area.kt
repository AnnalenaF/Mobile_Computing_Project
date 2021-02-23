package com.dhbw.cas.integra.ui.areas

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName="areas", indices=[Index(value = ["text"], unique = true),
    Index(value=["label"], unique = true)])
data class Area(
    @ColumnInfo(name="text")
    var text: String,

    @ColumnInfo(name="label")
    var label: Int,

    @ColumnInfo(name="id")
    @PrimaryKey(autoGenerate = true) val id: Long = 0
)
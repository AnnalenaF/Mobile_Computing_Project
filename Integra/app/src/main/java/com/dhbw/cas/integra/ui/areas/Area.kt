package com.dhbw.cas.integra.ui.areas

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName="areas", indices=[Index(value = ["label"], unique = true)])
data class Area(
    @PrimaryKey
    @ColumnInfo(name="text")
    var text: String,

    @ColumnInfo(name="label")
    var label: Int
)
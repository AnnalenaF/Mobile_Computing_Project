package com.dhbw.cas.integra.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName="tasks", foreignKeys = [ForeignKey(entity = Area::class,
        parentColumns = ["text"],
        childColumns = ["area_text"],
        onDelete = ForeignKey.CASCADE)]) // delete all tasks for an area if the area is deleted
data class Task(
        @ColumnInfo(name="title")
        var title: String,

        @ColumnInfo(name="description")
        var description: String,

        @ColumnInfo(name="priority")
        var priority: Int,

        @ColumnInfo(name="area_text")
        var area_text: String,

        @ColumnInfo(name="expectedDuration")
        var expectedDuration: Int,

        @ColumnInfo(name="loggedDuration")
        var loggedDuration: Int = 0,

        @ColumnInfo(name="id")
        @PrimaryKey(autoGenerate = true) val id: Long = 0
)
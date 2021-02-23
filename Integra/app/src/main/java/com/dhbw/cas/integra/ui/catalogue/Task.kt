package com.dhbw.cas.integra.ui.catalogue

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.dhbw.cas.integra.ui.areas.Area
import java.sql.Time
import java.sql.Timestamp

@Entity(tableName="tasks", foreignKeys = [ForeignKey(entity = Area::class,
        parentColumns = ["id"],
        childColumns = ["area_id"],
        onDelete = ForeignKey.CASCADE)]) // delete all tasks for an area if the area is deleted
data class Task(
        @ColumnInfo(name="text")
        var text: String,

        @ColumnInfo(name="priority")
        var priority: Int,

        @ColumnInfo(name="area_id")
        var area_id: Long,

        @ColumnInfo(name="expectedDuration")
        var expectedDuration: Time?,

        @ColumnInfo(name="loggedDuration")
        var loggedDuration: Time? = Time(0),

        @ColumnInfo(name="id")
        @PrimaryKey(autoGenerate = true) val id: Long = 0
)
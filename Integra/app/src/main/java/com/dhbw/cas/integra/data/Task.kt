package com.dhbw.cas.integra.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName="tasks", foreignKeys = [
                ForeignKey(entity = Area::class,
                           parentColumns = ["text"],
                           childColumns = ["area_text"],
                           // delete all tasks for an area if the area is deleted
                           onDelete = ForeignKey.CASCADE),
                ForeignKey(entity = Sprint::class,
                           parentColumns = ["id"],
                           childColumns = ["sprintId"],
                           // do not delete tasks if sprint is deleted
                           onDelete = ForeignKey.NO_ACTION)])
@Parcelize
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

        @ColumnInfo(name="sprintId")
        var sprintId: Long? = 0,

        @ColumnInfo(name="state")
        var state: Int = 0,

        @ColumnInfo(name="id")
        @PrimaryKey(autoGenerate = true) val id: Long = 0
): Parcelable
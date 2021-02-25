package com.dhbw.cas.integra.data

import androidx.room.*

@Entity(tableName="areas", indices=[Index(value = ["label"], unique = true)],
        foreignKeys = [ForeignKey(entity = Sprint::class, parentColumns = ["id"],
        childColumns = ["sprintId"])])
data class Area(
    @PrimaryKey
    @ColumnInfo(name="text")
    var text: String,

    @ColumnInfo(name="label")
    var label: Int,

    @ColumnInfo(name="sprintId")
    var sprintId: Long? = 0,

    @ColumnInfo(name="totalCapacity")
    var totalCapacity: Int? = 0,

    @ColumnInfo(name="remainingCapacity")
    var remainingCapacity: Int? = 0
)
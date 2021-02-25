package com.dhbw.cas.integra.data

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize

@Entity(tableName="areas", indices=[Index(value = ["label"], unique = true)],
        foreignKeys = [ForeignKey(entity = Sprint::class, parentColumns = ["id"],
        childColumns = ["sprintId"])])
@Parcelize
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
): Parcelable

@Parcelize
class Areas: ArrayList<Area>(), Parcelable
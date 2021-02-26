package com.dhbw.cas.integra.data

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize

@Entity(tableName="areas", indices=[Index(value = ["label"], unique = true)])
@Parcelize
data class Area(
    @PrimaryKey
    @ColumnInfo(name="text")
    var text: String,

    @ColumnInfo(name="label")
    var label: Int,

    @ColumnInfo(name="totalCapacity")
    var totalCapacity: Int? = 0,

    @ColumnInfo(name="remainingCapacity")
    var remainingCapacity: Int? = 0
): Parcelable

@Parcelize
class Areas: ArrayList<Area>(), Parcelable
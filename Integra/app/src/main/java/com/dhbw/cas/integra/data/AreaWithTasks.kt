package com.dhbw.cas.integra.data

import androidx.room.Embedded
import androidx.room.Relation

data class AreaWithTasks(
    @Embedded val area: Area,
    @Relation(
        parentColumn = "text",
        entityColumn = "area_text"
    )
    val tasks: List<Task>
)

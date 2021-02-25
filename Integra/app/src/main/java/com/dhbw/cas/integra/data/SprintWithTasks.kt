package com.dhbw.cas.integra.data

import androidx.room.Embedded
import androidx.room.Relation

data class SprintWithTasks(
    @Embedded val sprint: Sprint,
    @Relation(
        parentColumn = "id",
        entityColumn = "sprintId"
    )
    val tasks: List<Task>
)

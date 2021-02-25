package com.dhbw.cas.integra.data

import androidx.room.Embedded
import androidx.room.Relation

data class SprintWithTasks(
    @Embedded val sprint: Sprint,
    @Relation(
        parentColumn = "id",
        entityColumn = "sprintid"
    )
    val tasks: List<Task>
)

package com.bennu.app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val credits: Int,
    val semesterId: Int, // Foreign key a Semester
    val isApproved: Boolean = false
)

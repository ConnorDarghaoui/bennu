package com.bennu.app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "semesters")
data class Semester(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String, // Ej: "1er Semestre 2024"
    val order: Int    // Para ordenar los semestres
)

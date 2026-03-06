package com.bennu.app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "evaluations")
data class Evaluation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,     // Ej: "Parcial 1"
    val weight: Double,   // Ej: 0.3 (30%)
    val score: Double?,   // Nota obtenida, null si no se ha evaluado
    val courseId: Int     // Foreign key a Course
)

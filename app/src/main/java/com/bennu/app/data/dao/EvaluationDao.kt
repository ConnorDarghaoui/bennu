package com.bennu.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bennu.app.data.entities.Evaluation
import kotlinx.coroutines.flow.Flow

@Dao
interface EvaluationDao {
    @Query("SELECT * FROM evaluations WHERE courseId = :courseId")
    fun getEvaluationsByCourse(courseId: Int): Flow<List<Evaluation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvaluation(evaluation: Evaluation)

    @Update
    suspend fun updateEvaluation(evaluation: Evaluation)
}

package com.bennu.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bennu.app.data.database.AppDatabase
import com.bennu.app.data.entities.Course
import com.bennu.app.data.entities.Evaluation
import com.bennu.app.data.entities.Semester
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BennuViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val courseDao = database.courseDao()
    private val evaluationDao = database.evaluationDao()
    private val semesterDao = database.semesterDao()

    val semesters: Flow<List<Semester>> = semesterDao.getAllSemesters()
    val allCourses: Flow<List<Course>> = courseDao.getAllCourses()

    fun getCoursesBySemester(semesterId: Int): Flow<List<Course>> {
        return courseDao.getCoursesBySemester(semesterId)
    }

    fun getEvaluationsByCourse(courseId: Int): Flow<List<Evaluation>> {
        return evaluationDao.getEvaluationsByCourse(courseId)
    }

    fun addSemester(name: String, order: Int) {
        viewModelScope.launch {
            semesterDao.insertSemester(Semester(name = name, order = order))
        }
    }

    fun addCourse(name: String, credits: Int, semesterId: Int) {
        viewModelScope.launch {
            courseDao.insertCourse(Course(name = name, credits = credits, semesterId = semesterId))
        }
    }

    fun updateCourseStatus(course: Course, isApproved: Boolean) {
        viewModelScope.launch {
            courseDao.updateCourse(course.copy(isApproved = isApproved))
        }
    }

    fun addEvaluation(name: String, weight: Double, courseId: Int) {
        viewModelScope.launch {
            evaluationDao.insertEvaluation(Evaluation(name = name, weight = weight, score = null, courseId = courseId))
        }
    }

    fun updateEvaluationScore(evaluation: Evaluation, score: Double?) {
        viewModelScope.launch {
            evaluationDao.updateEvaluation(evaluation.copy(score = score))
        }
    }
}

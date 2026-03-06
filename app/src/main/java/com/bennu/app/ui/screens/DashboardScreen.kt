package com.bennu.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bennu.app.data.entities.Course
import com.bennu.app.data.entities.Semester
import com.bennu.app.viewmodel.BennuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToCourse: (Int) -> Unit,
    viewModel: BennuViewModel = viewModel()
) {
    val semesters by viewModel.semesters.collectAsState(initial = emptyList())
    val courses by viewModel.allCourses.collectAsState(initial = emptyList())

    var showAddSemesterDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Bennu - Tu Progreso") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddSemesterDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Semestre")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            val totalCredits = courses.sumOf { it.credits }
            val approvedCredits = courses.filter { it.isApproved }.sumOf { it.credits }
            val progress = if (totalCredits > 0) approvedCredits.toFloat() / totalCredits else 0f

            Text("Progreso Total", style = MaterialTheme.typography.titleLarge)
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .padding(vertical = 8.dp)
            )
            Text("$approvedCredits / $totalCredits Créditos Aprobados")

            Spacer(modifier = Modifier.height(24.dp))
            Text("Semestres", style = MaterialTheme.typography.titleLarge)

            if (semesters.isEmpty()) {
                Text("No hay semestres agregados.", modifier = Modifier.padding(top = 8.dp))
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(semesters) { semester ->
                        SemesterItem(semester, viewModel, onNavigateToCourse)
                    }
                }
            }
        }
    }

    if (showAddSemesterDialog) {
        var semesterName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddSemesterDialog = false },
            title = { Text("Agregar Semestre") },
            text = {
                OutlinedTextField(
                    value = semesterName,
                    onValueChange = { semesterName = it },
                    label = { Text("Nombre (ej. 1er Semestre 2024)") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (semesterName.isNotBlank()) {
                        viewModel.addSemester(semesterName, semesters.size)
                        showAddSemesterDialog = false
                    }
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSemesterDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun SemesterItem(
    semester: Semester,
    viewModel: BennuViewModel,
    onNavigateToCourse: (Int) -> Unit
) {
    val courses by viewModel.getCoursesBySemester(semester.id).collectAsState(initial = emptyList())
    var showAddCourseDialog by remember { mutableStateOf(false) }

    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(semester.name, style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = { showAddCourseDialog = true }) {
                    Text("+ Materia")
                }
            }

            courses.forEach { course ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onNavigateToCourse(course.id) },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("- ${course.name} (${course.credits} cr)")
                    Checkbox(
                        checked = course.isApproved,
                        onCheckedChange = { viewModel.updateCourseStatus(course, it) }
                    )
                }
            }
        }
    }

    if (showAddCourseDialog) {
        var courseName by remember { mutableStateOf("") }
        var courseCredits by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddCourseDialog = false },
            title = { Text("Agregar Materia") },
            text = {
                Column {
                    OutlinedTextField(
                        value = courseName,
                        onValueChange = { courseName = it },
                        label = { Text("Nombre") }
                    )
                    OutlinedTextField(
                        value = courseCredits,
                        onValueChange = { courseCredits = it },
                        label = { Text("Créditos") }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val credits = courseCredits.toIntOrNull() ?: 0
                    if (courseName.isNotBlank() && credits > 0) {
                        viewModel.addCourse(courseName, credits, semester.id)
                        showAddCourseDialog = false
                    }
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCourseDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

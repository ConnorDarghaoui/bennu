package com.bennu.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bennu.app.data.entities.Course
import com.bennu.app.data.entities.Evaluation
import com.bennu.app.viewmodel.BennuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    courseId: Int,
    onNavigateBack: () -> Unit,
    viewModel: BennuViewModel = viewModel()
) {
    val evaluations by viewModel.getEvaluationsByCourse(courseId).collectAsState(initial = emptyList())
    var showAddEvalDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Materia") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddEvalDialog = true }) {
                Text("+ Eval")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            val currentScore = evaluations.sumOf { (it.score ?: 0.0) * (it.weight / 100.0) }
            val totalWeight = evaluations.sumOf { it.weight }

            Text("Nota Acumulada: ${"%.2f".format(currentScore)} / 100", style = MaterialTheme.typography.titleLarge)
            Text("Peso Evaluado: ${"%.1f".format(totalWeight)}%", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn {
                items(evaluations) { eval ->
                    EvaluationItem(eval, viewModel)
                }
            }
        }
    }

    if (showAddEvalDialog) {
        var evalName by remember { mutableStateOf("") }
        var evalWeight by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddEvalDialog = false },
            title = { Text("Agregar Evaluación") },
            text = {
                Column {
                    OutlinedTextField(
                        value = evalName,
                        onValueChange = { evalName = it },
                        label = { Text("Nombre (ej. Parcial 1)") }
                    )
                    OutlinedTextField(
                        value = evalWeight,
                        onValueChange = { evalWeight = it },
                        label = { Text("Porcentaje (ej. 30)") }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val weight = evalWeight.toDoubleOrNull() ?: 0.0
                    if (evalName.isNotBlank() && weight > 0) {
                        viewModel.addEvaluation(evalName, weight, courseId)
                        showAddEvalDialog = false
                    }
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddEvalDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun EvaluationItem(evaluation: Evaluation, viewModel: BennuViewModel) {
    var scoreInput by remember { mutableStateOf(evaluation.score?.toString() ?: "") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(evaluation.name, style = MaterialTheme.typography.titleMedium)
                Text("${evaluation.weight}%", style = MaterialTheme.typography.bodySmall)
            }

            OutlinedTextField(
                value = scoreInput,
                onValueChange = {
                    scoreInput = it
                    val newScore = it.toDoubleOrNull()
                    if (newScore != null || it.isBlank()) {
                        viewModel.updateEvaluationScore(evaluation, newScore)
                    }
                },
                label = { Text("Nota (0-100)") },
                modifier = Modifier.width(100.dp)
            )
        }
    }
}

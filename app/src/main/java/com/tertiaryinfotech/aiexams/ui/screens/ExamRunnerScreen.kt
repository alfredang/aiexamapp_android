package com.tertiaryinfotech.aiexams.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tertiaryinfotech.aiexams.data.AnswerResponse
import com.tertiaryinfotech.aiexams.data.ApiException
import com.tertiaryinfotech.aiexams.data.AttemptResponse
import com.tertiaryinfotech.aiexams.data.AttemptScore
import com.tertiaryinfotech.aiexams.data.ExamMode
import com.tertiaryinfotech.aiexams.data.Question
import com.tertiaryinfotech.aiexams.data.QuestionOption
import com.tertiaryinfotech.aiexams.data.QuestionType
import com.tertiaryinfotech.aiexams.data.SavedResponse
import com.tertiaryinfotech.aiexams.ui.SessionViewModel
import kotlinx.coroutines.launch

/**
 * Drives a single attempt: paging through questions, selecting answers,
 * checking (Practice) or saving (Exam), and submitting for a score.
 * Mirrors the iOS ExamRunnerView.
 */
@Composable
fun ExamRunnerScreen(session: SessionViewModel, attemptId: String) {
    val scope = rememberCoroutineScope()
    var payload by remember { mutableStateOf<AttemptResponse?>(null) }
    var index by remember { mutableIntStateOf(0) }
    val answers = remember { mutableStateMapOf<String, SavedResponse>() }
    var reveal by remember { mutableStateOf<AnswerResponse?>(null) }
    var isBusy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var submittedScore by remember { mutableStateOf<AttemptScore?>(null) }

    LaunchedEffect(attemptId) {
        isBusy = true
        try {
            val loaded = session.api.attempt(attemptId)
            payload = loaded
            answers.clear()
            answers.putAll(loaded.attempt.responses)
            error = null
        } catch (e: ApiException) {
            error = e.message
        } finally {
            isBusy = false
        }
    }

    val data = payload
    when {
        data != null && data.questions.isNotEmpty() -> {
            val question = data.questions[index]
            val saved = answers[question.id] ?: SavedResponse()

            Column(Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            data.exam.code,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            "${index + 1} / ${data.questions.size}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    Text(question.stem, style = MaterialTheme.typography.titleMedium)

                    question.options.forEach { option ->
                        OptionRow(
                            option = option,
                            selected = saved.answer.contains(option.id),
                            onClick = {
                                val current = answers[question.id] ?: SavedResponse()
                                val nextAnswer = if (question.questionType == QuestionType.MULTI) {
                                    if (current.answer.contains(option.id))
                                        current.answer - option.id
                                    else current.answer + option.id
                                } else {
                                    listOf(option.id)
                                }
                                answers[question.id] = current.copy(answer = nextAnswer)
                            },
                        )
                    }

                    reveal?.let { r ->
                        AppCard(Modifier.fillMaxWidth()) {
                            val correct = r.isCorrect == true
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(
                                    if (correct) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (correct) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                                )
                                Text(
                                    if (correct) "Correct" else "Review",
                                    color = if (correct) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                            r.explanation?.let {
                                Text(it, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 8.dp))
                            }
                        }
                    }

                    submittedScore?.let { ResultSummary(it, data.exam.passingScore) }
                }

                HorizontalDivider()
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    TextButton(
                        onClick = { if (index > 0) { index--; reveal = null } },
                        enabled = index > 0,
                    ) {
                        Icon(Icons.Filled.ChevronLeft, contentDescription = null)
                        Text("Previous")
                    }

                    TextButton(
                        onClick = {
                            scope.launch {
                                try {
                                    val s = answers[question.id] ?: SavedResponse()
                                    reveal = session.api.answer(attemptId, question.id, s.answer, s.flagged)
                                } catch (e: ApiException) {
                                    error = e.message
                                }
                            }
                        },
                        enabled = saved.answer.isNotEmpty(),
                    ) {
                        Text(if (data.attempt.examMode == ExamMode.PRACTICE) "Check" else "Save")
                    }

                    val isLast = index == data.questions.size - 1
                    TextButton(
                        onClick = {
                            if (isLast) {
                                scope.launch {
                                    try {
                                        submittedScore = session.api.submit(attemptId)
                                    } catch (e: ApiException) {
                                        error = e.message
                                    }
                                }
                            } else {
                                index++; reveal = null
                            }
                        },
                    ) {
                        Text(if (isLast) "Submit" else "Next")
                        Icon(
                            if (isLast) Icons.Filled.Send else Icons.Filled.ChevronRight,
                            contentDescription = null,
                        )
                    }
                }
            }
        }

        isBusy -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }

        else -> MessageState("Attempt unavailable", error ?: "Try again.")
    }
}

@Composable
private fun OptionRow(option: QuestionOption, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
    else MaterialTheme.colorScheme.surfaceVariant
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            if (selected) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(22.dp),
        )
        Text(option.text, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun ResultSummary(score: AttemptScore, passingScore: Int) {
    val passed = score.score >= passingScore.toDouble()
    AppCard(Modifier.fillMaxWidth()) {
        Text(
            "Score ${score.score.toInt()}%",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            if (passed) "Passed" else "Keep practicing",
            color = if (passed) Color(0xFF2E7D32) else MaterialTheme.colorScheme.tertiary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 4.dp),
        )
        if (score.correctCount != null && score.total != null) {
            Text(
                "${score.correctCount} of ${score.total} correct",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

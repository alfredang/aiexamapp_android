package com.tertiaryinfotech.aiexams.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data transfer objects mirroring the Tertiary AI Exams mobile API
 * (https://exams.tertiaryinfotech.com under /api/mobile). Field names match the
 * JSON contract exactly so the same backend serves both the iOS and Android apps.
 */

@Serializable
data class User(
    val id: String,
    val email: String,
    val name: String? = null,
    val role: String,
)

@Serializable
data class AuthResponse(
    val token: String,
    val user: User,
)

@Serializable
data class ServerError(val error: String)

@Serializable
data class Vendor(
    val id: String,
    val slug: String,
    val name: String,
    val description: String? = null,
)

@Serializable
data class CatalogResponse(
    val vendors: List<Vendor> = emptyList(),
    val bundles: List<CatalogBundle> = emptyList(),
)

@Serializable
data class CatalogVendor(
    val slug: String,
    val name: String,
)

@Serializable
data class CatalogBundle(
    val id: String,
    val slug: String,
    val title: String,
    val description: String,
    val vendor: CatalogVendor? = null,
    val code: String,
    val level: String,
    val totalQuestions: Int,
    val practiceExamCount: Int,
    val exams: List<CatalogExam> = emptyList(),
)

@Serializable
data class CatalogExam(
    val id: String,
    val slug: String,
    val code: String,
    val title: String,
    val level: String,
    val durationMinutes: Int,
    val passingScore: Int,
    val questionCount: Int,
    val publishedQuestionCount: Int,
    val position: Int,
)

@Serializable
data class LibraryResponse(
    val bundles: List<LibraryBundle> = emptyList(),
    val standalone: List<LibraryExam> = emptyList(),
)

@Serializable
data class LibraryBundle(
    val bundleId: String,
    val bundleSlug: String,
    val bundleTitle: String,
    val bundleDescription: String,
    val vendorName: String,
    val vendorSlug: String,
    val code: String,
    val level: String,
    val items: List<LibraryExam> = emptyList(),
    val hasVoucher: Boolean = false,
    val grantedAt: String,
)

@Serializable
data class LibraryExam(
    val entitlementId: String,
    val examId: String,
    val examSlug: String,
    val examTitle: String,
    val examCode: String,
    val questionCount: Int,
    val durationMinutes: Int,
    val vendorName: String,
    val vendorSlug: String,
    val tier: String,
    val grantedAt: String,
)

enum class ExamMode(val apiValue: String, val title: String) {
    PRACTICE("PRACTICE", "Practice"),
    EXAM("EXAM", "Exam");

    companion object {
        fun from(value: String): ExamMode = entries.firstOrNull { it.apiValue == value } ?: PRACTICE
    }
}

@Serializable
data class StartAttemptRequest(
    val examId: String,
    val mode: String,
    val teaser: Boolean? = null,
)

@Serializable
data class StartAttemptResponse(val attemptId: String)

@Serializable
data class AttemptResponse(
    val attempt: Attempt,
    val exam: AttemptExam,
    val questions: List<Question> = emptyList(),
    val result: AttemptScore? = null,
)

@Serializable
data class Attempt(
    val id: String,
    val mode: String,
    val isTeaser: Boolean = false,
    val startedAt: String,
    val submittedAt: String? = null,
    val expiresAt: String? = null,
    val durationSec: Int = 0,
    val score: Double? = null,
    val passed: Boolean? = null,
    val responses: Map<String, SavedResponse> = emptyMap(),
) {
    val examMode: ExamMode get() = ExamMode.from(mode)
}

@Serializable
data class AttemptExam(
    val id: String,
    val title: String,
    val code: String,
    val vendorName: String,
    val passingScore: Int,
)

enum class QuestionType(val apiValue: String) {
    SINGLE("SINGLE"),
    MULTI("MULTI"),
    TRUE_FALSE("TRUE_FALSE"),
    ORDERING("ORDERING"),
    HOTSPOT("HOTSPOT");

    companion object {
        fun from(value: String): QuestionType = entries.firstOrNull { it.apiValue == value } ?: SINGLE
    }
}

@Serializable
data class Question(
    val id: String,
    val stem: String,
    val type: String,
    val domain: String = "",
    val options: List<QuestionOption> = emptyList(),
    val correct: List<String>? = null,
    val explanation: String? = null,
) {
    val questionType: QuestionType get() = QuestionType.from(type)
}

@Serializable
data class QuestionOption(
    val id: String,
    val text: String,
)

@Serializable
data class SavedResponse(
    val answer: List<String> = emptyList(),
    val flagged: Boolean? = null,
    val timeSpent: Int? = null,
)

@Serializable
data class AnswerRequest(
    val attemptId: String,
    val questionId: String,
    val answer: List<String>,
    val flagged: Boolean? = null,
)

@Serializable
data class AnswerResponse(
    val saved: Boolean = false,
    val isCorrect: Boolean? = null,
    val correct: List<String>? = null,
    val explanation: String? = null,
)

@Serializable
data class SubmitRequest(val attemptId: String)

@Serializable
data class AttemptScore(
    val score: Double,
    val correctCount: Int? = null,
    val total: Int? = null,
    val perDomain: Map<String, DomainScore>? = null,
)

@Serializable
data class DomainScore(
    val correct: Int,
    val total: Int,
)

@Serializable
data class DeleteAccountResponse(val ok: Boolean)

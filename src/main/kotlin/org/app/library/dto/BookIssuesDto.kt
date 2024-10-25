package org.app.library.dto

import jakarta.validation.constraints.NotEmpty

data class BookIssuesDto(
    val id: Long,
    val title: String,
    val isbn: String,
    val name: String,
    val admNno: String,
    val expectedDate: String?,
    val returnDate: String?,
    val dateIssued: String,
)
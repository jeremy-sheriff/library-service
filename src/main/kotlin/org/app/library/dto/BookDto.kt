package org.app.library.dto

import jakarta.validation.constraints.NotEmpty

data class BookDto(
    val id:Long,
    @field:NotEmpty(message = "The author cannot be empty")
    val bookAuthor:String,

    @field:NotEmpty(message = "The title cannot be empty")
    val bookTitle: String,

    @field:NotEmpty(message = "The isbn cannot be empty")
    val bookIsbn: String,
)
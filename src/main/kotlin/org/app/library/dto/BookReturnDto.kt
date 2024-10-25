package org.app.library.dto

import jakarta.validation.constraints.NotEmpty

data class BookReturnDto(
    @field:NotEmpty(message = "The admNo cannot be empty")
    val admNo:String,

    @field:NotEmpty(message = "The bookIsbn cannot be empty")
    val bookIsbn:String,

    )

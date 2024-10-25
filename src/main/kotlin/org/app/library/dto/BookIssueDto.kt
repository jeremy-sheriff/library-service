package org.app.library.dto
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern

class BookIssueDto(
    @field:NotEmpty(message = "The admNo cannot be empty")
    val admNo:String,

    @field:NotEmpty(message = "The bookIsbn cannot sbe empty")
    val bookIsbn:String,

    @field:NotEmpty(message = "The expectedDate cannot be empty")
    @field:Pattern(
        regexp = "\\d{4}-\\d{2}-\\d{2}",
        message = "The expectedDate must be in the format yyyy-MM-dd"
    )
    val expectedDate: String,
)
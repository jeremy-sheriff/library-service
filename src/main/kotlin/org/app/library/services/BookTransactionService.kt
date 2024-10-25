package org.app.library.services

import org.app.library.repositories.BookIssueRepository
import org.app.library.repositories.BookRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

@Service
class BookTransactionService(
    private val bookRepository: BookRepository,
    private val bookIssueRepository: BookIssueRepository
) {

    private val logger: Logger = LoggerFactory.getLogger(BookTransactionService::class.java)

    @Transactional
    fun markBookAsBorrowed(bookId: String): Boolean {
        return try {
            val updatedRows = bookRepository.markBookAsBorrowed(bookId)
            logger.debug("Updated rows : {}",updatedRows)
            updatedRows > 0 // Return true if at least one row was updated
        } catch (ex: Exception) {
            // Handle exception, logging, etc.
            logger.error("Error updating book as borrowed: ${ex.message}")
            false
        }
    }

    @Transactional
    fun markBookAsReturned(studentUuid: String, bookId: String, returnDate: Date){
        bookIssueRepository.markBookAsReturned(studentUuid, bookId, returnDate)
    }

    @Transactional
    fun markBookAsNotBorrowed(bookId: String){
        bookIssueRepository.markBookAsNotBorrowed(bookId)
    }
}

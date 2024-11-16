package org.app.library.services


import org.app.library.clients.UsersClient
import org.app.library.dto.*
import org.app.library.models.Book
import org.app.library.models.BookIssue
import org.app.library.repositories.BookIssueRepository
import org.app.library.repositories.BookRepository
import org.slf4j.LoggerFactory

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.net.ConnectException
import java.util.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

@Service
class BookService(
    private val bookRepository: BookRepository,
    private val bookIssueRepository: BookIssueRepository,
    private val usersClient: UsersClient,
    private val bookTransactionService: BookTransactionService
) {


    private val logger = LoggerFactory.getLogger(BookService::class.java)
    fun receiveData(userJson:String){
        try {
//            val objectMapper = jacksonObjectMapper()
        } catch (e: Exception) {

            println(" Error => ${e.message}")
            println("******************************************")
            println("Error deserializing message: $userJson Error => ${e.message}")
        }
    }

    fun saveBook(bookDto: BookDto) {
        val book = Book(
            id = null,
            title = bookDto.bookTitle,
            author = bookDto.bookAuthor,
            isbn = bookDto.bookIsbn,
            borrowed = false
        )
        bookRepository.save(book)
    }


    fun getBooks(page: Int, size: Int): Page<Book> {
        val pageable = PageRequest.of(page, size)
        return bookRepository.findAvailableBooks(pageable)
    }

    fun getStudents(): MutableList<UserDto> {
        try {
            return usersClient.getAllUsers()
        } catch (e: ConnectException) {
            println("Caught Exception: ${e.message}")
            return mutableListOf()
        }
    }

    fun getUserByAdmNo(admNo: String): UserDto {
        return usersClient.getUserByAdmNo(admNo)
    }

    fun issueBook(bookIssue: BookIssue) {
        bookIssueRepository.save(bookIssue)
        logger.debug("Book Id : {} ",bookIssue.bookId)
        bookIssue.bookId.let { bookTransactionService.markBookAsBorrowed(it) }
    }

    fun studentExists(admNo: String): StudentExistsResponse {
        logger.info("Service Student Exists: {}", admNo)
        return usersClient.studentExists(admNo) //Client
    }

    fun bookIsAllocatedTheStudent(
        bookId: String,
        studentId: String
    ): Boolean {
        return bookIssueRepository
            .existsByBookIdAndStudentIdAndReturnDateIsNull(bookId, studentId)
    }

    fun getBookByIsbn(bookIsbn: String): Book {
        return bookRepository.getBookByIsbn(bookIsbn)
    }

    fun getStudentByAdmNo(admNo: String): UserDto {
        return usersClient.getUserByAdmNo(admNo)
    }

    fun bookExistsByIsbn(isbn: String): Boolean {
        return bookRepository.existsByIsbn(isbn)
    }

    fun bookIsAvailable(bookId: String): Boolean {
        return bookIssueRepository.existsByBookIdAndReturnDateIsNull(bookId)

    }

    fun findByBookIdAndReturnDateIsNull(string: String): BookIssue {
        return bookIssueRepository.findByBookIdAndReturnDateIsNull(string)
    }

    fun returnBook(student: UserDto,book: Book) {
        bookTransactionService.markBookAsReturned(
            student.id.toString(),
            book.id.toString(),
            Date()
        )

        bookTransactionService.markBookAsNotBorrowed(book.isbn)
    }

    fun getBooksAndStudents(): MutableList<BookIssuesDto> {
        val bookIssueDto = mutableListOf<BookIssuesDto>()
        bookIssueRepository.getBooksAndStudents().forEach { dto ->
            if (dto is Array<*>) {
                val id = dto[0].toString().toLong()
                val title = dto[1].toString()
                val isbn = dto[2].toString()
                val name = dto[3].toString()
                val adm_no = dto[4].toString()
                val expectedDate = dto[5]?.toString() // Handle null return date
                val returnDate = dto[5]?.toString() // Handle null return date
                val dateIssued = dto[6].toString()
                val bookIssue = BookIssuesDto(
                    id, title, isbn, name, adm_no, expectedDate,returnDate, dateIssued
                )
                bookIssueDto.add(bookIssue)
            }
        }

        return bookIssueDto

    }


    fun getBorrowedBooks(pageable: Pageable): Page<BookIssuesDto> {
        // Fetch the paginated result from the repository
        val borrowedBooksPage = bookRepository.findBorrowedBooks(pageable)

        // Map the result to a Page of BookIssuesDto
        val bookIssuesDtoPage = borrowedBooksPage.map { it ->
            if (it is Array<*>) {
                val studentId = it[1].toString()
                val bookTitle = it[2].toString()
                val isbn = it[3].toString()
                val returnDate = it[4]?.toString() ?: "N/A" // Handle null values
                val expectedDate = it[5]?.toString() ?: "N/A" // Handle null values
                val dateIssued = it[6].toString()
                val student = usersClient.getUserById(studentId)

                // Create and return BookIssuesDto
                BookIssuesDto(
                    id = it[0].toString().toLong(),
                    title = bookTitle,
                    isbn = isbn,
                    name = student.name,
                    admNno = student.admNo,
                    expectedDate = expectedDate,
                    returnDate = returnDate,
                    dateIssued = dateIssued
                )
            } else {
                throw IllegalStateException("Invalid data format")
            }
        }

        return bookIssuesDtoPage
    }

    //    @Deprecated("This has been deprecated")
    fun getBooksTransactionsHistory(): MutableList<BookTransactionHistory> {
        val transactionHistory = mutableListOf<BookTransactionHistory>()
        bookIssueRepository.getBooksTransactionsHistory().forEach { dto ->
            if (dto is Array<*>) {
                val name = dto[0].toString()
                val transaction = dto[1].toString().toInt()

                val transactionDto = BookTransactionHistory(
                    name, transaction
                )

                transactionHistory.add(transactionDto)
            }
        }
        return transactionHistory
    }


}
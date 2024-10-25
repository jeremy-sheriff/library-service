package org.app.library.controllers

import jakarta.validation.Valid
import org.app.library.clients.UsersClient
import org.app.library.dto.*
import org.app.library.models.Book
import org.app.library.models.BookIssue
import org.app.library.services.BookService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*
import org.slf4j.LoggerFactory


@RestController
@RequestMapping("api/library/books")
@CrossOrigin(origins = ["http://localhost:4200","https://muhohodev.com"])
@PreAuthorize("hasAnyAuthority('library_role')")
class BooksController(
    private val bookService: BookService,
    private val usersClient: UsersClient
) {
    private val logger = LoggerFactory.getLogger(BooksController::class.java)

    @GetMapping("")
    fun books(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): Page<Book> {
        return bookService.getBooks(page, size)
    }

    @GetMapping("borrowed")
    fun getBorrowedBooks(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): Page<BookIssuesDto> {
        val pageable = PageRequest.of(page, size)  // Create a Pageable object
        return bookService.getBorrowedBooks(pageable)  // Pass Pageable to the service
    }

    @GetMapping("transactions")
    fun getBooksTransactionsHistory():MutableList<BookTransactionHistory>{
        return bookService.getBooksTransactionsHistory()
    }

    @PostMapping("save")
    fun saveBook(@RequestBody @Valid bookDto: BookDto):ResponseEntity<MessageDto>{
        return try {
            bookService.saveBook(bookDto)
            ResponseEntity.ok(MessageDto("Book has been saved successfully"))
        }catch (ex: MethodArgumentNotValidException){
            ResponseEntity.ok(MessageDto("Error saving the book"))
        }
    }

    @GetMapping("students")
    fun getStudents(): MutableList<UserDto> {
        return bookService.getStudents()
    }

    @GetMapping("students/{admNo}")
    fun getUserByAdmNo(
        @PathVariable admNo:String
    ):UserDto {
        return bookService.getUserByAdmNo(admNo)
    }

    private fun bookExists(bookIsbn:String):Boolean{
        return bookService.bookExistsByIsbn(bookIsbn)
    }

    private fun studentExists(admNo:String):StudentExistsResponse{
        logger.info("Controller Checking for student $admNo")
        return bookService.studentExists(admNo)
    }

    private fun checkIfStudentHasTheBook():Boolean{

        return false
    }

    @PostMapping("return")
    fun returnBook(@RequestBody bookReturnDto: BookReturnDto): ResponseEntity<Map<String, String>> {

        // Validate if the book exists
        val bookExists: Boolean = bookExists(bookReturnDto.bookIsbn)

        // Validate if the student exists
        val studentExistsResponse = studentExists(bookReturnDto.admNo)
        val studentExists: Boolean = studentExistsResponse.exists

        return if (bookExists && studentExists) {
            val book = bookService.getBookByIsbn(bookReturnDto.bookIsbn)
            val student = bookService.getStudentByAdmNo(bookReturnDto.admNo)

            // Check if the book was issued the student
            if (bookService.bookIsAllocatedTheStudent(book.id.toString(), student.id.toString())) {


                //return the book
                bookService.returnBook(student,book)

                return successResponse("Success ! Student with Adm no ${bookReturnDto.admNo} was allocated the book ${bookReturnDto.bookIsbn}")
            }else{
                ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(mapOf("status" to "error", "message" to "Sorry! No relationship found for ${bookReturnDto.bookIsbn} and student =${bookReturnDto.admNo}"))
            }
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf("status" to "error", "message" to "Book and student do not exist in the database"))
        }
    }



    @PostMapping("issue")
    fun issueBook(@RequestBody @Valid bookIssueDto: BookIssueDto): ResponseEntity<Map<String, String>> {
        val bookExists = bookExists(bookIssueDto.bookIsbn)
        val studentExistsResponse = studentExists(bookIssueDto.admNo)
        val studentExists = studentExistsResponse.exists

        // Validation checks
        val errorResponse = validateBookAndStudent(bookExists, studentExists, bookIssueDto.bookIsbn, bookIssueDto.admNo)
        if (errorResponse != null) {
            return errorResponse
        }

        // Retrieve book and student details
        val book = bookService.getBookByIsbn(bookIssueDto.bookIsbn)
        val student = bookService.getStudentByAdmNo(bookIssueDto.admNo)

        // Check if the book is available
        if (bookService.bookIsAvailable(book.id.toString())) {
            logger.info("Check the book availability {}", bookService.bookIsAvailable(book.id.toString()))
            return errorResponse("${book.title} is not available")
        }

        // Check if the book is already issued to the student
        if (bookService.bookIsAllocatedTheStudent(book.id.toString(), student.id.toString())) {
            return errorResponse("Student with ID ${bookIssueDto.admNo} has already been allocated the book ${bookIssueDto.bookIsbn}")
        }

        // Parse expected return date and issue the book
        val expectedReturnDate = parseExpectedDate(bookIssueDto.expectedDate)
            ?: return errorResponse("Invalid date format for expected return date")

        val bookIssue = BookIssue(
            id = null,
            bookId = book.id.toString(),
            studentId = student.id.toString(),
            dateIssued = Date(),
            expectedDate = java.sql.Date.valueOf(expectedReturnDate),
            returnDate = null
        )
        bookService.issueBook(bookIssue)

        // Return success message
        return successResponse("Book Issued successfully")
    }

    // Helper method to validate if book and student exist
    private fun validateBookAndStudent(
        bookExists: Boolean,
        studentExists: Boolean,
        bookIsbn: String,
        admNo: String): ResponseEntity<Map<String, String>>? {
        if (!bookExists) {
            return errorResponse("Book with ISBN $bookIsbn does not exist")
        }
        if (!studentExists) {
            return errorResponse("Student with ID $admNo does not exist")
        }
        return null
    }

    // Helper method to parse expected return date
    private fun parseExpectedDate(expectedDate: String): LocalDate? {
        return try {
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            LocalDate.parse(expectedDate, dateFormatter)
        } catch (e: DateTimeParseException) {
            null
        }
    }

    // Helper method to create error responses
    private fun errorResponse(message: String): ResponseEntity<Map<String, String>> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(mapOf("status" to "error", "message" to message))
    }

    // Helper method to create success responses
    private fun successResponse(message: String): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(mapOf(
            "status" to "success",
            "message" to message))
    }





    @GetMapping("issues")
    fun issues():List<Any> {
        return bookService.getBooksAndStudents()
    }
}
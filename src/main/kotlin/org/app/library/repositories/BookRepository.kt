package org.app.library.repositories
import org.app.library.models.Book
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional

interface BookRepository:JpaRepository<Book,Long>{

    fun getBookByIsbn(isbn:String):Book

    fun existsByIsbn(isbn: String):Boolean

    @Query("""
        SELECT
            library.book_issues.book_id,
            library.book_issues.student_id,
            b.title,
            b.isbn,
            library.book_issues.return_date,
            library.book_issues.expected_date,
            library.book_issues.date_issued
        FROM
            library.book_issues
        JOIN
            library.books b ON CAST(b.id AS BIGINT) = CAST(library.book_issues.book_id AS BIGINT)
        WHERE
            library.book_issues.return_date IS NULL;
        """
        , nativeQuery = true)
    fun findBorrowedBooks(pageable: Pageable): Page<Any>

    // Add this query to fetch books where borrowed = false
    @Query("SELECT b FROM Book b WHERE b.borrowed = false")
    fun findAvailableBooks(pageable: Pageable): Page<Book>

    @Modifying
    @Transactional
    @Query("UPDATE Book b SET b.borrowed = true WHERE b.id = :bookId")
    fun markBookAsBorrowed(bookId: String): Int

    override fun findAll(pageable: Pageable): Page<Book>
}
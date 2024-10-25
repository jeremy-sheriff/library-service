package org.app.library.models
import jakarta.persistence.*

@Table(name = "books")
@Entity
open class Book(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id:Long?,

    @Column(name = "title", nullable = false)
    open var title: String,

    @Column(name = "author", nullable = false)
    open var author: String,

    @Column(name = "borrowed", nullable = false)
    open var borrowed: Boolean,

    @Column(name = "isbn", nullable = false)
    open var isbn: String
) {
    constructor() : this(1,"","", false,"")
}
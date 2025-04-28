package com.example.bookstore.specification;

import com.example.bookstore.model.Book;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class BookSpecifications {

    public static Specification<Book> hasAuthor(String author) {
        return (root, query, cb) ->
                cb.equal(cb.lower(root.get("author")), author.toLowerCase());
    }

    public static Specification<Book> hasTitle(String title) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Book> inCategory(String category) {
        return (root, query, cb) ->
                cb.equal(cb.lower(root.get("category").get("name")), category.toLowerCase());
    }

    public static Specification<Book> priceBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) ->
                cb.between(root.get("price"), min, max);
    }
}

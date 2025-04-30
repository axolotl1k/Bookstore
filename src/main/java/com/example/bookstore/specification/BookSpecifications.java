package com.example.bookstore.specification;

import com.example.bookstore.model.Book;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class BookSpecifications {

    public static Specification<Book> hasAuthor(String author) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("author")), "%" + author.toLowerCase() + "%");
    }

    public static Specification<Book> hasTitle(String title) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Book> inCategory(String categoryId) {
        return (root, query, cb) -> {
            Long id = Long.valueOf(categoryId);
            return cb.equal(root.get("category").get("id"), id);
        };
    }

    public static Specification<Book> priceGreaterOrEqual(BigDecimal min) {
        return (root, query, cb) ->
                cb.ge(root.get("price"), min);
    }

    public static Specification<Book> priceLessOrEqual(BigDecimal max) {
        return (root, query, cb) ->
                cb.le(root.get("price"), max);
    }
}

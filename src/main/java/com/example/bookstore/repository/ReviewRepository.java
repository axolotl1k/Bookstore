package com.example.bookstore.repository;

import com.example.bookstore.model.Book;
import com.example.bookstore.model.Review;
import com.example.bookstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBookId(Long bookId);
    List<Review> findByBook(Book book);
    List<Review> findAllByUser(User user);
}

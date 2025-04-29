package com.example.bookstore.service.impl;

import com.example.bookstore.model.Book;
import com.example.bookstore.model.Review;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.ReviewRepository;
import com.example.bookstore.repository.UserRepository;
import com.example.bookstore.service.ReviewService;
import com.example.bookstore.dto.ReviewDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final UserRepository userRepo;
    private final ReviewRepository reviewRepo;
    private final BookRepository bookRepo;

    @Override
    public void addReview(ReviewDto dto) {
        Book book = bookRepo.findById(dto.bookId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found: " + dto.bookId()));

        User user = userRepo.findById(dto.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + dto.userId()));

        Review review = Review.builder()
                .book(book)
                .user(user)
                .rating(dto.rating())
                .comment(dto.comment())
                .build();

        reviewRepo.save(review);
    }

    @Override
    public List<ReviewDto> getReviewsByBookId(Long bookId) {
        if (!bookRepo.existsById(bookId)) {
            throw new EntityNotFoundException("Book not found: " + bookId);
        }

        return reviewRepo.findByBookId(bookId).stream()
                .map(r -> new ReviewDto(
                        r.getBook().getId(),
                        r.getUser().getId(),
                        r.getRating(),
                        r.getComment()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public double getAverageRating(Long bookId) {
        List<Review> reviews = reviewRepo.findByBookId(bookId);
        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }
}

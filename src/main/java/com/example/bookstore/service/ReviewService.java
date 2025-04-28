package com.example.bookstore.service;

import com.example.bookstore.service.dto.ReviewDto;
import java.util.List;

public interface ReviewService {
    void addReview(ReviewDto dto);
    List<ReviewDto> getReviewsByBookId(Long bookId);
    double getAverageRating(Long bookId);
}

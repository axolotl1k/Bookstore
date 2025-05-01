package com.example.bookstore.controller;

import com.example.bookstore.dto.ReviewDto;
import com.example.bookstore.service.ReviewService;
import com.example.bookstore.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    @GetMapping("/book/{bookId}")
    public String reviewsForBook(
            @PathVariable Long bookId,
            Model model
    ) {
        List<ReviewDto> reviews = reviewService.getReviewsByBookId(bookId);
        model.addAttribute("reviews", reviews);
        String me = userService.getCurrentUser().getUsername();
        model.addAttribute("reviewDto", new ReviewDto(bookId, null, me, 0, ""));
        return "reviews/list";
    }

    @PostMapping("/book/{bookId}")
    public String createReview(
            @PathVariable Long bookId,
            @ModelAttribute("reviewDto") @Valid ReviewDto dto,
            BindingResult br,
            Model model
    ) {
        if (br.hasErrors()) {
            model.addAttribute("reviews", reviewService.getReviewsByBookId(bookId));
            return "reviews/list";
        }
        long userId = userService.getCurrentUser().getId();
        String me = userService.getCurrentUser().getUsername();
        reviewService.addReview(new ReviewDto(
                bookId,
                userId,
                me,
                dto.rating(),
                dto.comment()
        ));
        return "redirect:/reviews/book/" + bookId;
    }
}


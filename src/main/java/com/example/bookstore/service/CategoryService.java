package com.example.bookstore.service;

import com.example.bookstore.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> findAll();
    CategoryDto create(CategoryDto dto);
    void delete(Long id);
}

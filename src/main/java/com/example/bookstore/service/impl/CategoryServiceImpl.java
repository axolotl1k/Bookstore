package com.example.bookstore.service.impl;

import com.example.bookstore.dto.CategoryDto;
import com.example.bookstore.model.Category;
import com.example.bookstore.repository.CategoryRepository;
import com.example.bookstore.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepo;

    @Override
    public List<CategoryDto> findAll() {
        return categoryRepo.findAll()
                .stream()
                .map(c -> new CategoryDto(c.getId(), c.getName()))
                .toList();
    }

    @Override
    @Transactional
    public CategoryDto create(CategoryDto dto) {
        Category cat = new Category();
        cat.setName(dto.name());
        Category saved = categoryRepo.save(cat);
        return new CategoryDto(saved.getId(), saved.getName());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!categoryRepo.existsById(id)) {
            throw new EntityNotFoundException("Категорія не знайдена: " + id);
        }
        categoryRepo.deleteById(id);
    }
}
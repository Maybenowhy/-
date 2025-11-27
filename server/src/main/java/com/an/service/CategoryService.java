package com.an.service;

import com.an.entity.Category;
import java.util.List;

public interface CategoryService {

    void add(Category category);

    void update(Category category);

    void delete(Long id);

    List<Category> list();
}

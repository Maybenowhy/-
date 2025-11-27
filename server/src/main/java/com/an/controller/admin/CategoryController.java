package com.an.controller.admin;

import com.an.entity.Category;
import com.an.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // 新增分类
    @PostMapping("/add")
    public void add(@RequestBody Category category) {
        categoryService.add(category);
    }

    // 修改分类
    @PutMapping("/update")
    public void update(@RequestBody Category category) {
        categoryService.update(category);
    }

    // 删除分类
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        categoryService.delete(id);
    }

    // 查询分类列表
    @GetMapping("/list")
    public List<Category> list() {
        return categoryService.list();
    }
}

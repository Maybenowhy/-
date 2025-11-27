package com.an.mapper;

import com.an.entity.Category;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategoryMapper {

    @Insert("INSERT INTO categories(name, description, created_at, updated_at) " +
            "VALUES (#{name}, #{description}, #{createdAt}, #{updatedAt})")
    void insert(Category category);

    @Update("UPDATE categories SET name=#{name}, description=#{description}, updated_at=NOW() WHERE id=#{id}")
    void update(Category category);

    @Delete("DELETE FROM categories WHERE id=#{id}")
    void delete(Long id);

    @Select("SELECT * FROM categories ORDER BY id ASC")
    List<Category> list();
}
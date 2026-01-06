package com.example.PetHome.mapper;

import com.example.PetHome.entity.Carousel;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CarouselMapper {
    List<Carousel> findAll();
    int insert(Carousel carousel);
    int update(Carousel carousel);
    int delete(Integer id);
}
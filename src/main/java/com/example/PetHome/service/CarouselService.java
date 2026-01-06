package com.example.PetHome.service;

import com.example.PetHome.entity.Carousel;
import com.example.PetHome.mapper.CarouselMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarouselService {

    @Autowired
    private CarouselMapper carouselMapper;

    public List<Carousel> getAll() {
        return carouselMapper.findAll();
    }

    public boolean save(Carousel carousel) {
        if (carousel.getId() == null) {
            return carouselMapper.insert(carousel) > 0;
        } else {
            return carouselMapper.update(carousel) > 0;
        }
    }

    public boolean delete(Integer id) {
        return carouselMapper.delete(id) > 0;
    }
}
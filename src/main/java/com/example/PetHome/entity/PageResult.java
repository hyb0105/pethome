package com.example.PetHome.entity;

import lombok.Data;
import java.util.List;

@Data
public class PageResult<T> {
    /**
     * 总记录数
     */
    private long total;

    /**
     * 当前页数据列表
     */
    private List<T> records;

    public PageResult(long total, List<T> records) {
        this.total = total;
        this.records = records;
    }
}
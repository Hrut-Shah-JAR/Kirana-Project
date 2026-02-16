package com.kiranastore.dto;

import jakarta.validation.constraints.Min;

public class PageRequestDto {

    @Min(0)
    private Integer page;

    @Min(1)
    private Integer size;

    public PageRequestDto() {
    }

    public PageRequestDto(Integer page, Integer size) {
        this.page = page;
        this.size = size;
    }

    public Integer getPage() {
        return page;
    }

    public Integer getSize() {
        return size;
    }

    public int pageOrDefault() {
        return page != null ? page : 0;
    }

    public int sizeOrDefault() {
        return size != null ? size : 20;
    }
}

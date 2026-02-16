package com.kiranastore.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Getter;

@Getter
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

    public int pageOrDefault() {
        return page != null ? page : 0;
    }

    public int sizeOrDefault() {
        return size != null ? size : 20;
    }
}

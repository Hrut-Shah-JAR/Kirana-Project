package com.kiranastore.dto.response;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PageResponseDto<T> {

    private final List<T> items;
    private final int page;
    private final int size;
    private final long totalItems;
    private final int totalPages;
    private final boolean hasNext;
    private final boolean hasPrevious;

    public PageResponseDto(List<T> items, int page, int size, long totalItems, int totalPages,
                           boolean hasNext, boolean hasPrevious) {
        this.items = items;
        this.page = page;
        this.size = size;
        this.totalItems = totalItems;
        this.totalPages = totalPages;
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
    }

    public static <T> PageResponseDto<T> from(Page<T> page) {
        return new PageResponseDto<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious()
        );
    }

}

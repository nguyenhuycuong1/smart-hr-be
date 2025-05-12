package com.devcuong.smart_hr.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageFilterInput<T> {
    @NotNull()
    Integer pageNumber;

    @NotNull()
    Integer pageSize;

    @NotNull()
    T filter;

    String common;

    String sortProperty;
    String sortOrder;
}

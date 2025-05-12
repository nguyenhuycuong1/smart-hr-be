package com.devcuong.smart_hr.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageResponse<T> {
    Result result;
    T data;
    long dataCount;

    public PageResponse() {}

    public PageResponse(Result result, T data, long dataCount) {
        this.result = result;
        this.data = data;
        this.dataCount = dataCount;
    }

    public PageResponse success() {
        this.result = new Result("Thành công", 200, true);
        return this;
    }
}

package com.devcuong.smart_hr.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;


@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiResponse<T> {
    Result result;
    T data;

    public ApiResponse() {}

    public ApiResponse(Result result, T data) {
        this.result = result;
        this.data = data;
    }

    public ApiResponse success() {
        this.result = new Result("Thành công", 200, true);
        return this;
    }

    public ApiResponse success(String message) {
        this.result = new Result(message, 200, true);
        return this;
    }

    public ApiResponse success(int code) {
        this.result = new Result("Thành công", code, true);
        return this;
    }
}

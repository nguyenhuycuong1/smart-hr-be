package com.devcuong.smart_hr.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Result {
    String message;
    int responseCode;
    boolean success;

    public Result(){}

    public Result message(String message){
        this.message = message;
        return this;
    }

    public Result errorCode(int errorCode){
        this.responseCode = errorCode;
        return this;
    }

    public Result isSuccess(boolean success){
        this.success = success;
        return this;
    }

    public Result(String message, int responseCode, boolean success) {
        this.message = message;
        this.responseCode = responseCode;
        this.success = success;
    }
}

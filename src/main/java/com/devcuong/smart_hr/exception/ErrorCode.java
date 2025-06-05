package com.devcuong.smart_hr.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    INPUT_INVALID(101, "Input invalid", HttpStatus.BAD_REQUEST),
    UNCATEGORIZED(102, "Uncategorized Exception", HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_EXISTS(103, "Not Exists", HttpStatus.NOT_FOUND),
    NOT_FOUND(104, "Not Found", HttpStatus.NOT_FOUND ),
    BAD_REQUEST(105, "Bad Request", HttpStatus.BAD_REQUEST),
    ;

    int code;
    String message;
    HttpStatusCode statusCode;
    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}

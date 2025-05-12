package com.devcuong.smart_hr.exception;

import com.devcuong.smart_hr.dto.response.ApiResponse;
import com.devcuong.smart_hr.dto.response.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
//    @ExceptionHandler(value = RuntimeException.class)
//    ResponseEntity<ApiResponse<?>> runtimeExceptionHandler(RuntimeException e) {
//        ApiResponse<?> apiResponse = new ApiResponse<>();
//
//        apiResponse.setResult(Result.builder()
//                        .success(false)
//                        .message(ErrorCode.UNCATEGORIZED.getMessage())
//                        .responseCode(ErrorCode.UNCATEGORIZED.getCode())
//                .build());
//        return ResponseEntity.badRequest().body(apiResponse);
//    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<?>> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse<?> apiResponse = new ApiResponse<>();

        apiResponse.setResult(Result.builder().success(false).responseCode(errorCode.getCode()).message(exception.getMessage()).build());

        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }
}

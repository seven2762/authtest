package com.sparta.homework.presentation.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class SuccessResponse<T> {


    private String message;
    private int code;
    private T data;



    public static <T> SuccessResponse<T> success(T data) {
        return SuccessResponse.<T>builder()
            .code(200)
            .message("success")
            .data(data)
            .build();
    }
}
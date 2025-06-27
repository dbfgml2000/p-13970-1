package com.back.global.globalExceptionHandler;


import com.back.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // 응답 헤더의 코드(404 Not Found)
    public ResponseEntity<RsData<Void>> handle(NoSuchElementException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND) // 응답 헤더의 코드(404 Not Found)
                .body( // 응답 바디
                        new RsData<>(
                                "404-1"
                                , "해당 데이터가 존재하지 않습니다."
                        )
                );
    }
}

/*
import com.back.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // 응답 헤더의 코드(404 Not Found)
    @ResponseBody
    public RsData<Void> handle() {
        return
                new RsData<>(
                        "404-1",
                        "해당 데이터가 존재하지 않습니다."
                );
    }
}
 */

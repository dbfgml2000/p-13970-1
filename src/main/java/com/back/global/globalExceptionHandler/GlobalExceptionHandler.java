package com.back.global.globalExceptionHandler;

import com.back.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<RsData<Void>> handle() {
        return new ResponseEntity<>(
                new RsData<>(
                        "404-1",
                        "해당 데이터가 존재하지 않습니다."
                ),
                NOT_FOUND
        );
    }
}

/*
// 이 코드는 @ControllerAdvice를 사용하여 전역 예외 처리기를 정의합니다.
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<RsData<Void>> handle() {
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

 */

/*
// 이 코드는 @RestControllerAdvice를 사용하여 전역 예외 처리기를 정의합니다.
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

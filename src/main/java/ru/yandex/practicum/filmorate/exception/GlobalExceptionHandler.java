package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(ValidationException e) {
        return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(NotFoundException e) {
        return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        return ResponseEntity.status(500).body(Map.of("error", "Произошла внутренняя ошибка"));
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(
            org.springframework.web.bind.MethodArgumentNotValidException e) {
        return ResponseEntity.status(400).body(Map.of(
                "error", "Ошибка валидации через аннотации",
                "message", e.getBindingResult().getAllErrors().get(0).getDefaultMessage()
        ));
    }

}

package com.projectFit.fit_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //Se dispara cuando usa ResourceNotFoundException (para algun recurso no encontrado)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiException> handleNotFoundException(ResourceNotFoundException ex){
        ApiException error = new ApiException(
                HttpStatus.NOT_FOUND.value(),
                "Recurso no encontrado",
                ex.getMessage(),
                LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    //Se dispara cuando usa BusinessException (algun error de logica)
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiException> handleBusinessException(
            BusinessException ex) {
        ApiException error = new ApiException(
                HttpStatus.BAD_REQUEST.value(),
                "Error de negocio",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Se dispara cuando fallan @NotNull, @Email, @NotBlank, etc. (errores de validacion de DTOs)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(
            MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(err -> {
            String campo = ((FieldError) err).getField();
            String mensaje = err.getDefaultMessage();
            errores.put(campo, mensaje);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errores);
    }

    // Se dispara cuando un usuario intenta acceder a algo que no le corresponde
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiException> handleAccessDeniedException(
            AccessDeniedException ex) {
        ApiException error = new ApiException(
                HttpStatus.FORBIDDEN.value(),
                "Acceso denegado",
                "No tenés permisos para realizar esta acción",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    // Atrapa cualquier error que no fue manejado arriba
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiException> handleGenericException(Exception ex) {
        ApiException error = new ApiException(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error interno del servidor",
                "Ocurrió un error inesperado",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

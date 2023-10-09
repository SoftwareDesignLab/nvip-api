package org.nvip.util;


import org.nvip.api.serializers.ErrorDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler {
        @ExceptionHandler(AppException.class)
        public ResponseEntity<ErrorDTO> handleAppException(AppException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new ErrorDTO(e.getMessage()));
        }

}

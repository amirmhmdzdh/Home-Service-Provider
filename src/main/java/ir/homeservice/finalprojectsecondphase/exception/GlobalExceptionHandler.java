package ir.homeservice.finalprojectsecondphase.exception;

import ir.homeservice.finalprojectsecondphase.dto.ExceptionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(DuplicateInformationException.class)
    public ResponseEntity<ExceptionDto> duplicateInformationExceptionHandler(DuplicateInformationException e) {
        log.warn(e.getMessage());
        ExceptionDto exceptionDto = new ExceptionDto(e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(exceptionDto, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionDto> NotFoundExceptionHandler(NotFoundException e) {
        log.warn(e.getMessage());
        ExceptionDto exceptionDto = new ExceptionDto(e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(exceptionDto, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PriceException.class)
    public ResponseEntity<ExceptionDto> priceExceptionHandler(PriceException e) {
        log.warn(e.getMessage());
        ExceptionDto exceptionDto = new ExceptionDto(e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(exceptionDto, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TimeException.class)
    public ResponseEntity<ExceptionDto> timeExceptionHandler(TimeException e) {
        log.warn(e.getMessage());
        ExceptionDto exceptionDto = new ExceptionDto(e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(exceptionDto, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ImageFormatException.class)
    public ResponseEntity<ExceptionDto> ImageFormatExceptionHandler(ImageFormatException e) {
        log.warn(e.getMessage());
        ExceptionDto exceptionDto = new ExceptionDto(e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(exceptionDto, HttpStatus.CONFLICT);
    }


}

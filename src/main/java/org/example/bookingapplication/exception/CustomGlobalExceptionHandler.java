package org.example.bookingapplication.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.example.bookingapplication.exception.booking.BookingInfoException;
import org.example.bookingapplication.exception.booking.InvalidDateException;
import org.example.bookingapplication.exception.payment.CantPaidBookingException;
import org.example.bookingapplication.exception.payment.PaymentCancelException;
import org.example.bookingapplication.exception.payment.PaymentDontConfirmException;
import org.example.bookingapplication.exception.payment.StripeSessionException;
import org.example.bookingapplication.exception.repository.EntityAlreadyExistsException;
import org.example.bookingapplication.exception.repository.EntityNotFoundException;
import org.example.bookingapplication.exception.telegram.EmailTokenGeneratorException;
import org.example.bookingapplication.exception.telegram.InvalidTelegramToken;
import org.example.bookingapplication.exception.telegram.TelegramBotException;
import org.example.bookingapplication.exception.telegram.TelegramSendMassageException;
import org.example.bookingapplication.exception.user.PasswordNotValidException;
import org.example.bookingapplication.exception.user.UserDontHavePermissions;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final int BAD_REQUEST_STATUS_CODE = 400;
    private static final int UNAUTHORIZED_STATUS_CODE = 401;
    private static final int FORBIDDEN_STATUS_CODE = 403;
    private static final int NOT_FOUND_STATUS_CODE = 404;
    private static final int CONFLICT_STATUS_CODE = 409;
    private static final int INTERNAL_SERVER_STATUS_CODE = 500;

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        Map<String, Object> errorsBody = new LinkedHashMap<>();
        errorsBody.put("timestamp", LocalDateTime.now());

        List<String> messagesList = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(this::getErrorMessage)
                .toList();

        errorsBody.put("errors", messagesList);
        return new ResponseEntity<>(errorsBody, status);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex) {
        return getObjectResponseEntity(
                ex.getMessage(),
                HttpStatusCode.valueOf(NOT_FOUND_STATUS_CODE)
        );
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<Object> handleEntityAlreadyExistsException(
            EntityAlreadyExistsException ex) {
        return getObjectResponseEntity(
                ex.getMessage(),
                HttpStatusCode.valueOf(CONFLICT_STATUS_CODE)
        );
    }

    @ExceptionHandler(PasswordNotValidException.class)
    public ResponseEntity<Object> handlePasswordNotValidException(PasswordNotValidException ex) {
        return getObjectResponseEntity(
                ex.getMessage(),
                HttpStatusCode.valueOf(UNAUTHORIZED_STATUS_CODE)
        );
    }

    @ExceptionHandler(InvalidDateException.class)
    public ResponseEntity<Object> handleInvalidDateException(InvalidDateException ex) {
        return getObjectResponseEntity(
                ex.getMessage(),
                HttpStatusCode.valueOf(BAD_REQUEST_STATUS_CODE)
        );
    }

    @ExceptionHandler(UserDontHavePermissions.class)
    public ResponseEntity<Object> handleUserDontHavePermissions(UserDontHavePermissions ex) {
        return getObjectResponseEntity(
                ex.getMessage(),
                HttpStatusCode.valueOf(FORBIDDEN_STATUS_CODE)
        );
    }

    @ExceptionHandler(BookingInfoException.class)
    public ResponseEntity<Object> handleBookingInfoException(BookingInfoException ex) {
        return getObjectResponseEntity(
                ex.getMessage(),
                HttpStatusCode.valueOf(BAD_REQUEST_STATUS_CODE)
        );
    }

    @ExceptionHandler(StripeSessionException.class)
    public ResponseEntity<Object> handleStripeSessionException(StripeSessionException ex) {
        return getObjectResponseEntity(
                ex.getMessage(),
                HttpStatusCode.valueOf(INTERNAL_SERVER_STATUS_CODE)
        );
    }

    @ExceptionHandler(PaymentDontConfirmException.class)
    public ResponseEntity<Object> handlePaymentDontConfirmException(
            PaymentDontConfirmException ex) {
        return getObjectResponseEntity(
                ex.getMessage(),
                HttpStatusCode.valueOf(CONFLICT_STATUS_CODE)
        );
    }

    @ExceptionHandler(CantPaidBookingException.class)
    public ResponseEntity<Object> handleCantPaidBookingException(CantPaidBookingException ex) {
        return getObjectResponseEntity(
                ex.getMessage(),
                HttpStatusCode.valueOf(CONFLICT_STATUS_CODE)
        );
    }

    @ExceptionHandler(PaymentCancelException.class)
    public ResponseEntity<Object> handlePaymentCancelException(PaymentCancelException ex) {
        return getObjectResponseEntity(
                ex.getMessage(),
                HttpStatusCode.valueOf(CONFLICT_STATUS_CODE)
        );
    }

    @ExceptionHandler(TelegramSendMassageException.class)
    public ResponseEntity<Object> handleTelegramSendMassageException(
            TelegramSendMassageException ex) {
        return getObjectResponseEntity(
                ex.getMessage(),
                HttpStatusCode.valueOf(INTERNAL_SERVER_STATUS_CODE)
        );
    }

    @ExceptionHandler(EmailTokenGeneratorException.class)
    public ResponseEntity<Object> handleEmailTokenGeneratorException(
            EmailTokenGeneratorException ex) {
        return getObjectResponseEntity(
                ex.getMessage(),
                HttpStatusCode.valueOf(INTERNAL_SERVER_STATUS_CODE)
        );
    }

    @ExceptionHandler(InvalidTelegramToken.class)
    public ResponseEntity<Object> handleInvalidTelegramToken(InvalidTelegramToken ex) {
        return getObjectResponseEntity(
                ex.getMessage(),
                HttpStatusCode.valueOf(BAD_REQUEST_STATUS_CODE)
        );
    }

    @ExceptionHandler(TelegramBotException.class)
    public ResponseEntity<Object> handleTelegramBotException(TelegramBotException ex) {
        return getObjectResponseEntity(
                ex.getMessage(),
                HttpStatusCode.valueOf(INTERNAL_SERVER_STATUS_CODE)
        );
    }

    private String getErrorMessage(ObjectError error) {
        if (error instanceof FieldError fieldError) {
            return fieldError.getField() + " " + fieldError.getDefaultMessage();
        }
        return error.getDefaultMessage();
    }

    private ResponseEntity<Object> getObjectResponseEntity(String message, HttpStatusCode status) {
        Map<String, Object> errorsBody = new LinkedHashMap<>();
        errorsBody.put("timestamp", LocalDateTime.now());
        errorsBody.put("errors", message);
        return new ResponseEntity<>(errorsBody, status);
    }
}

package dev.studentpp1.streamingservice.common.config;

import dev.studentpp1.streamingservice.common.dto.ErrorResponse;
import dev.studentpp1.streamingservice.movies.domain.exception.ActorNotFoundException;
import dev.studentpp1.streamingservice.movies.domain.exception.DirectorNotFoundException;
import dev.studentpp1.streamingservice.movies.domain.exception.MovieDomainException;
import dev.studentpp1.streamingservice.movies.domain.exception.MovieNotFoundException;
import dev.studentpp1.streamingservice.movies.domain.exception.OptimisticLockingException;
import dev.studentpp1.streamingservice.movies.domain.exception.PerformanceNotFoundException;
import dev.studentpp1.streamingservice.subscription.domain.exception.ActiveSubscriptionAlreadyExistsException;
import dev.studentpp1.streamingservice.subscription.domain.exception.InvalidFamilyMemberException;
import dev.studentpp1.streamingservice.subscription.domain.exception.MoviesNotFoundException;
import dev.studentpp1.streamingservice.subscription.domain.exception.MoviesNotInPlanException;
import dev.studentpp1.streamingservice.subscription.domain.exception.SubscriptionAccessDeniedException;
import dev.studentpp1.streamingservice.subscription.domain.exception.SubscriptionNotActiveException;
import dev.studentpp1.streamingservice.subscription.domain.exception.SubscriptionNotFoundException;
import dev.studentpp1.streamingservice.subscription.domain.exception.SubscriptionPlanAlreadyExistsException;
import dev.studentpp1.streamingservice.subscription.domain.exception.SubscriptionPlanNotFoundException;
import dev.studentpp1.streamingservice.users.domain.exception.UserAlreadyExistsException;
import dev.studentpp1.streamingservice.users.domain.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
        ActiveSubscriptionAlreadyExistsException.class,
        SubscriptionPlanAlreadyExistsException.class
    })
    public ResponseEntity<ErrorResponse> handleConflict(
        RuntimeException ex,
        HttpServletRequest request
    ) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(
            UserAlreadyExistsException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler({
        UserNotFoundException.class,
        UsernameNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleUserNotFound(
            RuntimeException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({
        SubscriptionNotFoundException.class,
        SubscriptionPlanNotFoundException.class,
        MoviesNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleSubscriptionNotFound(
        RuntimeException ex,
        HttpServletRequest request
    ) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }


    @ExceptionHandler({
        InvalidFamilyMemberException.class,
        SubscriptionNotActiveException.class,
        MoviesNotInPlanException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(
        RuntimeException ex,
        HttpServletRequest request
    ) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(SubscriptionAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
        RuntimeException ex,
        HttpServletRequest request
    ) {
        return buildErrorResponse(ex, HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler({
        ActorNotFoundException.class,
        MovieNotFoundException.class,
        DirectorNotFoundException.class,
        PerformanceNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleMovieNotFound(
            RuntimeException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(OptimisticLockingException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLocking(
            OptimisticLockingException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(MovieDomainException.class)
    public ResponseEntity<ErrorResponse> handleMovieDomain(
            MovieDomainException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleSpringAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(ex, HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleSpringAuthentication(
            AuthenticationException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(ex, HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request
    ) {
        return buildErrorResponse(ex, HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        return buildErrorResponse(
                new RuntimeException(message),
                HttpStatus.BAD_REQUEST,
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Unhandled exception", ex);
        return buildErrorResponse(
                ex,
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
        );
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            Exception ex,
            HttpStatus status,
            HttpServletRequest request
    ) {
        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }
}

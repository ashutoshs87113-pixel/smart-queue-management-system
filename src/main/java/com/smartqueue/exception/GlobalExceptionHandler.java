package com.smartqueue.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserAlreadyExistsException.class)
    public String handleUserAlreadyExists(UserAlreadyExistsException ex, RedirectAttributes redirectAttributes) {
        logger.warn("Registration error: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/register";
    }

    @ExceptionHandler(QueueException.class)
    public String handleQueueException(QueueException ex, RedirectAttributes redirectAttributes) {
        logger.warn("Queue operation warning: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/dashboard";
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFound(ResourceNotFoundException ex, Model model) {
        logger.error("Resource not found: {}", ex.getMessage());
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(AccessDeniedException ex, Model model) {
        logger.warn("Access denied attempt: {}", ex.getMessage());
        model.addAttribute("errorMessage", "Access Denied: You do not have administrative privileges to access this page.");
        return "error";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public void handleNoResourceFound(NoResourceFoundException ex) {
        // Silently ignore static asset 404s (such as favicon.ico)
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrityViolation(DataIntegrityViolationException ex, RedirectAttributes redirectAttributes, Model model) {
        logger.error("Database data integrity violation: {}", ex.getMessage());
        String msg = "A database constraint conflict occurred. Please verify your input and try again.";
        if (ex.getMessage() != null && ex.getMessage().contains("username")) {
            msg = "Username already exists. Please choose another username.";
        } else if (ex.getMessage() != null && ex.getMessage().contains("email")) {
            msg = "Email is already registered. Please use another email.";
        }
        redirectAttributes.addFlashAttribute("errorMessage", msg);
        model.addAttribute("errorMessage", msg);
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        logger.error("Unhandled exception caught in GlobalExceptionHandler", ex);
        model.addAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
        return "error";
    }
}

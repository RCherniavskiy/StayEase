package org.example.bookingapplication.exception.user;

public class UserDontHavePermissions extends RuntimeException {
    public UserDontHavePermissions(String message) {
        super(message);
    }
}

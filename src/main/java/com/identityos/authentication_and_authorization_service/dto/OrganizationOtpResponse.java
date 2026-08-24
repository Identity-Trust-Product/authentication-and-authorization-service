package com.identityos.authentication_and_authorization_service.dto;

public record OrganizationOtpResponse(
        boolean success,
        String message,
        String maskedEmail,
        long expiresInSeconds) {
}
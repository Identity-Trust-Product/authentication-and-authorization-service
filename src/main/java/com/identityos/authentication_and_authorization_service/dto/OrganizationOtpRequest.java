package com.identityos.authentication_and_authorization_service.dto;

import jakarta.validation.constraints.NotBlank;

public record OrganizationOtpRequest(
        @NotBlank String organizationId,
        String otp) {
}
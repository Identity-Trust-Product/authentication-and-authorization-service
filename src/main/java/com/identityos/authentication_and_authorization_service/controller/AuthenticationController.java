package com.identityos.authentication_and_authorization_service.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.identityos.authentication_and_authorization_service.dto.OrganizationOtpRequest;
import com.identityos.authentication_and_authorization_service.dto.OrganizationOtpResponse;
import com.identityos.authentication_and_authorization_service.service.OrganizationOtpService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private final OrganizationOtpService organizationOtpService;

    public AuthenticationController(OrganizationOtpService organizationOtpService) {
        this.organizationOtpService = organizationOtpService;
    }
    
    @GetMapping("/test")
    public String test() {
        return "Authentication Service is working";
    }

    @GetMapping("/organization/{organizationId}/status")
    public OrganizationOtpResponse status(@PathVariable String organizationId) {
        return organizationOtpService.status(organizationId);
    }

    @PostMapping("/organization/otp/request")
    public OrganizationOtpResponse requestOtp(@Valid @RequestBody OrganizationOtpRequest request) {
        return organizationOtpService.requestOtp(request.organizationId());
    }

    @PostMapping("/organization/otp/verify")
    public OrganizationOtpResponse verifyOtp(@Valid @RequestBody OrganizationOtpRequest request) {
        return organizationOtpService.verifyOtp(request.organizationId(), request.otp());
    }

}

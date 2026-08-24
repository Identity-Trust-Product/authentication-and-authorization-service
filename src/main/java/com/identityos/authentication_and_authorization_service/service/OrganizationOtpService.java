package com.identityos.authentication_and_authorization_service.service;

import com.identityos.authentication_and_authorization_service.dto.OrganizationOtpResponse;
import com.identityos.authentication_and_authorization_service.repository.OrganizationAuthRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrganizationOtpService {
    private final OrganizationAuthRepository organizationRepository;
    private final JavaMailSender mailSender;
    private final SecureRandom secureRandom = new SecureRandom();
    private final Map<String, OtpAttempt> attempts = new ConcurrentHashMap<>();
    private final long expirySeconds;
    private final long resendSeconds;
    private final int maxAttempts;
    private final String mailFrom;

    public OrganizationOtpService(
            OrganizationAuthRepository organizationRepository,
            JavaMailSender mailSender,
            @Value("${organization-auth.otp.expiry-seconds}") long expirySeconds,
            @Value("${organization-auth.otp.resend-seconds}") long resendSeconds,
            @Value("${organization-auth.otp.max-attempts}") int maxAttempts,
            @Value("${organization-auth.mail.from}") String mailFrom) {
        this.organizationRepository = organizationRepository;
        this.mailSender = mailSender;
        this.expirySeconds = expirySeconds;
        this.resendSeconds = resendSeconds;
        this.maxAttempts = maxAttempts;
        this.mailFrom = mailFrom;
    }

    public OrganizationOtpResponse status(String organizationId) {
        return organizationRepository.findEligibleOrganization(organizationId)
                .map(contact -> new OrganizationOtpResponse(true, "Organization is eligible for login.", maskEmail(contact.email()), 0))
                .orElse(new OrganizationOtpResponse(false, "Organization ID not found or not approved.", null, 0));
    }

    public OrganizationOtpResponse requestOtp(String organizationId) {
        OrganizationAuthRepository.OrganizationContact contact = organizationRepository
                .findEligibleOrganization(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Organization ID not found or not approved."));
        OtpAttempt existing = attempts.get(organizationId);
        if (existing != null && existing.createdAt().plusSeconds(resendSeconds).isAfter(Instant.now())) {
            throw new IllegalStateException("Please wait before requesting another OTP.");
        }

        String otp = String.format("%06d", secureRandom.nextInt(1_000_000));
        attempts.put(organizationId, new OtpAttempt(hash(otp), Instant.now(), Instant.now().plusSeconds(expirySeconds), 0));
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom);
        message.setTo(contact.email());
        message.setSubject("Identity OS organization login OTP");
        message.setText("Your Identity OS verification code is " + otp + ". It expires in " + expirySeconds + " seconds.");
        mailSender.send(message);
        return new OrganizationOtpResponse(true, "OTP sent to the registered representative email.", maskEmail(contact.email()), expirySeconds);
    }

    public OrganizationOtpResponse verifyOtp(String organizationId, String otp) {
        if (otp == null || !otp.matches("\\d{6}")) {
            return new OrganizationOtpResponse(false, "Enter a valid six-digit OTP.", null, 0);
        }
        OtpAttempt attempt = attempts.get(organizationId);
        if (attempt == null || attempt.expiresAt().isBefore(Instant.now())) {
            attempts.remove(organizationId);
            return new OrganizationOtpResponse(false, "OTP expired. Request a new OTP.", null, 0);
        }
        if (attempt.attempts() >= maxAttempts) {
            attempts.remove(organizationId);
            return new OrganizationOtpResponse(false, "Too many attempts. Request a new OTP.", null, 0);
        }
        OtpAttempt updated = attempt.withAttempts(attempt.attempts() + 1);
        attempts.put(organizationId, updated);
        if (!MessageDigest.isEqual(updated.hash(), hash(otp))) {
            return new OrganizationOtpResponse(false, "Invalid OTP.", null, 0);
        }
        attempts.remove(organizationId);
        return new OrganizationOtpResponse(true, "OTP verified. Continue with Keycloak login.", null, 0);
    }

    private byte[] hash(String value) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Unable to hash OTP", exception);
        }
    }

    private String maskEmail(String email) {
        int at = email.indexOf('@');
        if (at <= 1) return "***" + email.substring(Math.max(at, 0));
        return email.charAt(0) + "***" + email.substring(at);
    }

    private record OtpAttempt(byte[] hash, Instant createdAt, Instant expiresAt, int attempts) {
        private OtpAttempt withAttempts(int value) {
            return new OtpAttempt(hash, createdAt, expiresAt, value);
        }
    }
}

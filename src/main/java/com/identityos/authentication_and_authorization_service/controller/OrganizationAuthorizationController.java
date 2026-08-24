package com.identityos.authentication_and_authorization_service.controller;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/organization")
public class OrganizationAuthorizationController {

    @GetMapping("/{organizationId}/access-check")
    public String accessCheck(
            @PathVariable String organizationId,
            @AuthenticationPrincipal Jwt jwt) {
        String tokenOrganizationId = jwt.getClaimAsString("organization_id");
        if (!organizationId.equals(tokenOrganizationId)) {
            throw new AccessDeniedException("Organization access denied");
        }
        return "Organization access granted for " + tokenOrganizationId;
    }
}
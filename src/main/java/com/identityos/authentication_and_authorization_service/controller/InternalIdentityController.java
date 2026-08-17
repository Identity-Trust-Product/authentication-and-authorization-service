package com.identityos.authentication_and_authorization_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.identityos.authentication_and_authorization_service.dto.CreateIdentityRequest;
import com.identityos.authentication_and_authorization_service.dto.CreateIdentityResponse;
import java.util.UUID;

@RestController
@RequestMapping("/internal/v1/identities")
public class InternalIdentityController {

    @PostMapping
    public ResponseEntity<CreateIdentityResponse> createIdentity(
            @RequestBody CreateIdentityRequest request) {

        System.out.println(
                "Received identity creation request for organization: "
                        + request.getOrganizationId()
        );

        String identityId = "ID-" + UUID.randomUUID();

        CreateIdentityResponse response =
                new CreateIdentityResponse(
                        true,
                        identityId,
                        "Authentication identity created successfully"
                );

        return ResponseEntity.ok(response);
    }
}
package com.identityos.authentication_and_authorization_service.dto;

public class CreateIdentityResponse {

    private boolean success;
    private String identityId;
    private String message;

    public CreateIdentityResponse() {
    }

    public CreateIdentityResponse(
            boolean success,
            String identityId,
            String message) {

        this.success = success;
        this.identityId = identityId;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getIdentityId() {
        return identityId;
    }

    public String getMessage() {
        return message;
    }
}

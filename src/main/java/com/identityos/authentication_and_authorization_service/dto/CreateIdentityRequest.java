package com.identityos.authentication_and_authorization_service.dto;

public class CreateIdentityRequest {

    private String organizationId;
    private String applicationId;

    public CreateIdentityRequest() {
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }
}

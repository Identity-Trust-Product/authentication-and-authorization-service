package com.identityos.authentication_and_authorization_service.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class OrganizationAuthRepository {
    private final JdbcTemplate jdbcTemplate;

    public OrganizationAuthRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<OrganizationContact> findEligibleOrganization(String organizationId) {
        return jdbcTemplate.query("""
                SELECT official_email
                FROM organizations
                WHERE organization_id = ?
                  AND status = 'ACTIVE'
                  AND approval_status = 'APPROVED'
                  AND official_email IS NOT NULL
                """, resultSet -> resultSet.next()
                ? Optional.of(new OrganizationContact(resultSet.getString("official_email")))
                : Optional.empty(), organizationId);
    }

    public record OrganizationContact(String email) {
    }
}

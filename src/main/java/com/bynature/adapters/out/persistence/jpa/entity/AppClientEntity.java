package com.bynature.adapters.out.persistence.jpa.entity;

import com.bynature.domain.model.AppClient;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table(name = "app_clients")
@Valid
public class AppClientEntity {
    @Id
    @NotNull(message = "Application client technical ID cannot be null")
    private UUID id;

    @Column(nullable = false, unique = true)
    @NotNull(message = "Application client ID cannot be null")
    private String appClientId;

    @Column(nullable = false)
    @NotNull(message = "Application client secret cannot be null")
    private String appClientSecret;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    @NotNull(message = "Application client allowed Origin cannot be null")
    private String allowedOrigin;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAppClientId() {
        return appClientId;
    }

    public void setAppClientId(String clientId) {
        this.appClientId = clientId;
    }

    public String getAppClientSecret() {
        return appClientSecret;
    }

    public void setAppClientSecret(String clientSecret) {
        this.appClientSecret = clientSecret;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getAllowedOrigin() {
        return allowedOrigin;
    }

    public void setAllowedOrigin(String allowedOrigin) {
        this.allowedOrigin = allowedOrigin;
    }

    public static AppClientEntity fromDomain(AppClient appClient) {
        AppClientEntity entity = new AppClientEntity();
        entity.setId(appClient.getId());
        entity.setAppClientId(appClient.getAppClientId());
        entity.setAppClientSecret(appClient.getAppClientSecret());
        entity.setActive(appClient.isActive());
        entity.setAllowedOrigin(appClient.getAllowedOrigin());
        return entity;
    }
}

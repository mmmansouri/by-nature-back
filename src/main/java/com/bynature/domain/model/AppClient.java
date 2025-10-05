package com.bynature.domain.model;

import java.util.UUID;

public class AppClient {

    private UUID id;

    private String appClientId;


    private String appClientSecret;


    private boolean active = true;


    private String allowedOrigin;

    public AppClient(UUID id, String appClientId, String appClientSecret, boolean active, String allowedOrigin) {
        this.id = id;
        this.appClientId = appClientId;
        this.appClientSecret = appClientSecret;
        this.active = active;
        this.allowedOrigin = allowedOrigin;
    }

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
}

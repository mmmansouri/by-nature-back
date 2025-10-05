package com.bynature.application.service;

import com.bynature.adapters.out.persistence.jpa.adapter.AppClientRepositoryAdapter;
import com.bynature.domain.model.AppClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppClientSpringService {

    private final AppClientRepositoryAdapter clientRepositoryAdapter;

    public AppClientSpringService(AppClientRepositoryAdapter clientRepository) {
        this.clientRepositoryAdapter = clientRepository;
    }

    public List<AppClient> findByAppClientId(String appClientId) {
        return clientRepositoryAdapter.findByAppClientId(appClientId);
    }
}

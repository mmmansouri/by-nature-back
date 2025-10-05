package com.bynature.adapters.out.persistence.jpa.adapter;

import com.bynature.adapters.out.persistence.jpa.adapter.mapper.EntityMapper;
import com.bynature.adapters.out.persistence.jpa.repository.AppClientJpaRepository;
import com.bynature.domain.model.AppClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AppClientRepositoryAdapter {
    private final AppClientJpaRepository appClientRepository;

    public AppClientRepositoryAdapter(AppClientJpaRepository appClientRepository) {
        this.appClientRepository = appClientRepository;
    }

    public List<AppClient> findByAppClientId(String appClientId) {
        return appClientRepository.findByAppClientId(appClientId).stream().map(EntityMapper::mapAppClientToDomain)
                .toList();
    }
}

package com.swiftline.client.infrastructure.persistence.repository;

import com.swiftline.client.infrastructure.persistence.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientJpaRepository extends JpaRepository<ClientEntity, Long> {
}


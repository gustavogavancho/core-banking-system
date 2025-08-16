package com.swiftline.client.infrastructure.persistence.repository;

import com.swiftline.client.infrastructure.persistence.entity.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonJpaRepository extends JpaRepository<PersonEntity, Long> {
}


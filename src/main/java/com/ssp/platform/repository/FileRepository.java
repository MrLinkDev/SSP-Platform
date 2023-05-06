package com.ssp.platform.repository;

import com.ssp.platform.entity.FileEntity;
import com.ssp.platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, UUID> {
    Optional<FileEntity> findById(UUID id);

    boolean existsByHash(String hash);

    FileEntity getOneByHash(String hash);
}

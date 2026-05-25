package com.filecraft.repository;

import com.filecraft.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkspaceRepository extends JpaRepository<Workspace, UUID> {

    List<Workspace> findByOwner_IdOrderByCreatedAtDesc(UUID ownerId);

    Optional<Workspace> findByIdAndOwner_Id(UUID id, UUID ownerId);
}
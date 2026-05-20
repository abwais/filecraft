package com.filecraft.repository;

import com.filecraft.entity.FileAsset;
import com.filecraft.entity.enums.FileStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FileAssetRepository extends JpaRepository<FileAsset, UUID> {
    List<FileAsset> findByWorkspaceIdAndFileStatusOrderByCreatedAtDesc(
            UUID workspaceId,
            FileStatus fileStatus
    );

    boolean existsByWorkspaceId(UUID workspaceId);
}

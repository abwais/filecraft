package com.filecraft.entity;

import com.filecraft.entity.enums.FileKind;
import com.filecraft.entity.enums.FileStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "file_assets")
public class FileAsset {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;
    @ManyToOne
    @JoinColumn(name ="parent_file_id")
    private FileAsset parentFile;
    @Column(nullable = false)
    private String originalName;
    @Column(nullable = false)
    private String displayName;
    @Column(nullable = false)
    private String storedName;
    @Column(nullable = false)
    private String mimeType;
    @Column(nullable = false)
    private String extension;
    @Column(nullable = false)
    private Long size;
    @Column(nullable = false)
    private String sha256Hash;
    @Column(nullable = false)
    private String storagePath;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileKind fileKind;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileStatus fileStatus;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}

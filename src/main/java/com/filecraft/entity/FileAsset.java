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
    @Column(name ="sha256_hash", nullable = false, length = 64)
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

    @PrePersist
    public void onCreate(){
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if(this.fileStatus == null){
            this.fileStatus = FileStatus.ACTIVE;
        }
    }

    @PreUpdate
    public void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    public FileAsset getParentFile() {
        return parentFile;
    }

    public void setParentFile(FileAsset parentFile) {
        this.parentFile = parentFile;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getStoredName() {
        return storedName;
    }

    public void setStoredName(String storedName) {
        this.storedName = storedName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getSha256Hash() {
        return sha256Hash;
    }

    public void setSha256Hash(String sha256Hash) {
        this.sha256Hash = sha256Hash;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public FileKind getFileKind() {
        return fileKind;
    }

    public void setFileKind(FileKind fileKind) {
        this.fileKind = fileKind;
    }

    public FileStatus getFileStatus() {
        return fileStatus;
    }

    public void setFileStatus(FileStatus fileStatus) {
        this.fileStatus = fileStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

}

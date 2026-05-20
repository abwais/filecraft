package com.filecraft.service;

import com.filecraft.dto.FileResponse;
import com.filecraft.entity.FileAsset;
import com.filecraft.entity.Workspace;
import com.filecraft.entity.enums.FileKind;
import com.filecraft.entity.enums.FileStatus;
import com.filecraft.repository.FileAssetRepository;
import com.filecraft.repository.WorkspaceRepository;
import com.filecraft.dto.RenameFileRequest;

import com.filecraft.exception.WorkspaceNotFoundException;
import com.filecraft.exception.FileNotDeletedException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.UUID;

import java.util.List;

import com.filecraft.exception.FileDoesNotBelongToWorkspaceException;
import com.filecraft.exception.FileNotFoundException;
import com.filecraft.exception.FileAlreadyDeletedException;
@Service
public class FileService {

    private final WorkspaceRepository workspaceRepository;
    private final FileAssetRepository fileAssetRepository;
    private final FileStorageService fileStorageService;

    public FileService(
            WorkspaceRepository workspaceRepository,
            FileAssetRepository fileAssetRepository,
            FileStorageService fileStorageService
    ) {
        this.workspaceRepository = workspaceRepository;
        this.fileAssetRepository = fileAssetRepository;
        this.fileStorageService = fileStorageService;
    }

    public FileResponse uploadFile(UUID workspaceId, MultipartFile multipartFile) throws Exception {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));

        if (multipartFile.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty");
        }

        String originalName = multipartFile.getOriginalFilename();

        if (originalName == null || originalName.isBlank()) {
            originalName = "unnamed-file";
        }

        String extension = "";

        if (originalName.contains(".")) {
            extension = originalName
                    .substring(originalName.lastIndexOf(".") + 1)
                    .toLowerCase();
        }

        String storedName = extension.isBlank()
                ? UUID.randomUUID().toString()
                : UUID.randomUUID() + "." + extension;

        String mimeType = multipartFile.getContentType();

        if (mimeType == null || mimeType.isBlank()) {
            mimeType = "application/octet-stream";
        }

        if (!isAllowedMimeType(mimeType)) {
            throw new IllegalArgumentException("File type is not allowed: " + mimeType);
        }

        String sha256Hash = calculateSha256(multipartFile.getBytes());

        Path savedPath = fileStorageService.saveFile(
                multipartFile.getInputStream(),
                workspaceId.toString(),
                storedName
        );

        LocalDateTime now = LocalDateTime.now();

        FileAsset fileAsset = new FileAsset();
        fileAsset.setWorkspace(workspace);
        fileAsset.setParentFile(null);
        fileAsset.setOriginalName(originalName);
        fileAsset.setDisplayName(originalName);
        fileAsset.setStoredName(storedName);
        fileAsset.setMimeType(mimeType);
        fileAsset.setExtension(extension);
        fileAsset.setSize(multipartFile.getSize());
        fileAsset.setSha256Hash(sha256Hash);
        fileAsset.setStoragePath(savedPath.toString());
        fileAsset.setFileKind(FileKind.ORIGINAL);
        fileAsset.setFileStatus(FileStatus.ACTIVE);
        fileAsset.setCreatedAt(now);
        fileAsset.setUpdatedAt(now);

        FileAsset savedFileAsset = fileAssetRepository.save(fileAsset);

        return toResponse(savedFileAsset);
    }

    private String calculateSha256(byte[] bytes) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(bytes);
        return HexFormat.of().formatHex(hash);
    }

    private boolean isAllowedMimeType(String mimeType) {
        return mimeType.equals("text/plain")
                || mimeType.equals("application/pdf")
                || mimeType.equals("image/png")
                || mimeType.equals("image/jpeg");
    }

    private FileResponse toResponse(FileAsset fileAsset) {
        FileResponse response = new FileResponse();

        response.setId(fileAsset.getId());
        response.setWorkspaceId(fileAsset.getWorkspace().getId());
        response.setOriginalName(fileAsset.getOriginalName());
        response.setDisplayName(fileAsset.getDisplayName());
        response.setStoredName(fileAsset.getStoredName());
        response.setMimeType(fileAsset.getMimeType());
        response.setExtension(fileAsset.getExtension());
        response.setSize(fileAsset.getSize());
        response.setSha256Hash(fileAsset.getSha256Hash());
        response.setStoragePath(fileAsset.getStoragePath());
        response.setFileKind(fileAsset.getFileKind());
        response.setFileStatus(fileAsset.getFileStatus());
        response.setCreatedAt(fileAsset.getCreatedAt());
        response.setUpdatedAt(fileAsset.getUpdatedAt());

        return response;
    }

    public List<FileResponse> getFilesByWorkspace(UUID workspaceId) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));

        return fileAssetRepository
                .findByWorkspaceIdAndFileStatusOrderByCreatedAtDesc(
                        workspace.getId(),
                        FileStatus.ACTIVE
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public FileResponse getFileById(UUID workspaceId, UUID fileId) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));

        FileAsset fileAsset = fileAssetRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException(fileId));

        if (!fileAsset.getWorkspace().getId().equals(workspace.getId())) {
            throw new FileDoesNotBelongToWorkspaceException(fileId, workspaceId);
        }

        if (fileAsset.getFileStatus() == FileStatus.DELETED) {
            throw new FileNotFoundException(fileId);
        }

        return toResponse(fileAsset);
    }

    public Resource downloadFile(UUID workspaceId, UUID fileId) throws Exception {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));

        FileAsset fileAsset = fileAssetRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException(fileId));

        if (!fileAsset.getWorkspace().getId().equals(workspace.getId())) {
            throw new FileDoesNotBelongToWorkspaceException(fileId, workspaceId);
        }

        if (fileAsset.getFileStatus() == FileStatus.DELETED) {
            throw new FileNotFoundException(fileId);
        }

        Path filePath = fileStorageService.getFilePath(fileAsset.getStoragePath());

        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new FileNotFoundException(fileId);
        }

        return resource;
    }

    public void deleteFile(UUID workspaceId, UUID fileId) {

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));

        FileAsset fileAsset = fileAssetRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException(fileId));

        if (!fileAsset.getWorkspace().getId().equals(workspace.getId())) {
            throw new FileDoesNotBelongToWorkspaceException(fileId, workspaceId);
        }

        if (fileAsset.getFileStatus() == FileStatus.DELETED) {
            throw new FileAlreadyDeletedException(fileId);
        }

        fileAsset.setFileStatus(FileStatus.DELETED);

        fileAssetRepository.save(fileAsset);
    }

    public FileResponse renameFile(
            UUID workspaceId,
            UUID fileId,
            RenameFileRequest request
    ) {

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));

        FileAsset fileAsset = fileAssetRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException(fileId));

        if (!fileAsset.getWorkspace().getId().equals(workspace.getId())) {
            throw new FileDoesNotBelongToWorkspaceException(fileId, workspaceId);
        }

        if (fileAsset.getFileStatus() == FileStatus.DELETED) {
            throw new FileNotFoundException(fileId);
        }

        fileAsset.setDisplayName(request.getDisplayName());

        FileAsset savedFileAsset = fileAssetRepository.save(fileAsset);

        return toResponse(savedFileAsset);
    }

    public List<FileResponse> getDeletedFilesByWorkspace(UUID workspaceId) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));

        return fileAssetRepository
                .findByWorkspaceIdAndFileStatusOrderByCreatedAtDesc(
                        workspace.getId(),
                        FileStatus.DELETED
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public FileResponse restoreFile(UUID workspaceId, UUID fileId) {

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));

        FileAsset fileAsset = fileAssetRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException(fileId));

        if (!fileAsset.getWorkspace().getId().equals(workspace.getId())) {
            throw new FileDoesNotBelongToWorkspaceException(fileId, workspaceId);
        }

        if (fileAsset.getFileStatus() != FileStatus.DELETED) {
            throw new FileNotDeletedException(fileId);
        }

        fileAsset.setFileStatus(FileStatus.ACTIVE);

        FileAsset savedFileAsset = fileAssetRepository.save(fileAsset);

        return toResponse(savedFileAsset);
    }

    public void permanentlyDeleteFile(UUID workspaceId, UUID fileId) throws Exception {

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));

        FileAsset fileAsset = fileAssetRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException(fileId));

        if (!fileAsset.getWorkspace().getId().equals(workspace.getId())) {
            throw new FileDoesNotBelongToWorkspaceException(fileId, workspaceId);
        }

        if (fileAsset.getFileStatus() != FileStatus.DELETED) {
            throw new FileNotDeletedException(fileId);
        }

        fileStorageService.deleteFile(fileAsset.getStoragePath());

        fileAssetRepository.delete(fileAsset);
    }



}
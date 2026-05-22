package com.filecraft.controller;

import com.filecraft.dto.FileResponse;
import com.filecraft.service.FileService;
import com.filecraft.dto.RenameFileRequest;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping
    public FileResponse uploadFile(
            @PathVariable UUID workspaceId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) throws Exception {
        return fileService.uploadFile(workspaceId, file, authentication.getName());
    }

    @GetMapping
    public List<FileResponse> getFilesByWorkspace(
            @PathVariable UUID workspaceId,
            Authentication authentication
    ) throws Exception{
        return fileService.getFilesByWorkspace(workspaceId, authentication.getName());
    }

    @GetMapping("/deleted")
    public List<FileResponse> getDeletedFilesByWorkspace(
            @PathVariable UUID workspaceId,
            Authentication authentication
    ) {
        return fileService.getDeletedFilesByWorkspace(workspaceId, authentication.getName());
    }

    @GetMapping("/{fileId}")
    public FileResponse getFileById(
            @PathVariable UUID workspaceId,
            @PathVariable UUID fileId,
            Authentication authentication
    ) {
        return fileService.getFileById(workspaceId, fileId, authentication.getName());
    }

    @GetMapping("/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable UUID workspaceId,
            @PathVariable UUID fileId,
            Authentication authentication
    ) throws Exception {
        FileResponse fileResponse = fileService.getFileById(workspaceId, fileId, authentication.getName());
        Resource resource = fileService.downloadFile(workspaceId, fileId, authentication.getName());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileResponse.getMimeType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileResponse.getOriginalName() + "\""
                )
                .body(resource);
    }

    @DeleteMapping("/{fileId}")
    public void deleteFile(
            @PathVariable UUID workspaceId,
            @PathVariable UUID fileId,
            Authentication authentication
    ) {
        fileService.deleteFile(workspaceId, fileId, authentication.getName());
    }

    @DeleteMapping("/{fileId}/permanent")
    public void permanentlyDeleteFile(
            @PathVariable UUID workspaceId,
            @PathVariable UUID fileId,
            Authentication authentication
    ) throws Exception {
        fileService.permanentlyDeleteFile(workspaceId, fileId, authentication.getName());
    }

    @PatchMapping("/{fileId}/rename")
    public FileResponse renameFile(
            @PathVariable UUID workspaceId,
            @PathVariable UUID fileId,
            @Valid @RequestBody RenameFileRequest request,
            Authentication authentication
    ) {
        return fileService.renameFile(workspaceId, fileId, request, authentication.getName());
    }

    @PatchMapping("/{fileId}/restore")
    public FileResponse restoreFile(
            @PathVariable UUID workspaceId,
            @PathVariable UUID fileId,
            Authentication authentication
    ) {
        return fileService.restoreFile(workspaceId, fileId, authentication.getName());
    }
}
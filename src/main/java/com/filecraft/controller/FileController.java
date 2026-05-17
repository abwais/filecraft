package com.filecraft.controller;

import com.filecraft.dto.FileResponse;
import com.filecraft.service.FileService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

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
            @RequestParam("file") MultipartFile file
    ) throws Exception {
        return fileService.uploadFile(workspaceId, file);
    }

    @GetMapping
    public List<FileResponse> getFilesByWorkspace(@PathVariable UUID workspaceId) {
        return fileService.getFilesByWorkspace(workspaceId);
    }

    @GetMapping("/{fileId}")
    public FileResponse getFileById(
            @PathVariable UUID workspaceId,
            @PathVariable UUID fileId
    ) {
        return fileService.getFileById(workspaceId, fileId);
    }

    @GetMapping("/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable UUID workspaceId,
            @PathVariable UUID fileId
    ) throws Exception {
        FileResponse fileResponse = fileService.getFileById(workspaceId, fileId);
        Resource resource = fileService.downloadFile(workspaceId, fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileResponse.getMimeType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileResponse.getOriginalName() + "\""
                )
                .body(resource);
    }

}
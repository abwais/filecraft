package com.filecraft.controller;

import com.filecraft.dto.FileResponse;
import com.filecraft.service.FileService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
}
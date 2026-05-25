package com.filecraft.controller;

import com.filecraft.dto.CreateWorkspaceRequest;
import com.filecraft.dto.WorkspaceResponse;
import com.filecraft.service.WorkspaceService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/workspaces")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @PostMapping
    public WorkspaceResponse createWorkspace(
            @Valid @RequestBody CreateWorkspaceRequest request,
            Authentication authentication
    ) {
        return workspaceService.createWorkspace(
                request.getName(),
                authentication.getName()
        );
    }

    @GetMapping
    public List<WorkspaceResponse> getAllWorkspaces(
            Authentication authentication
    ) {
        return workspaceService.getWorkspaces(
                authentication.getName()
        );
    }

    @GetMapping("/{id}")
    public WorkspaceResponse getWorkspace(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        System.out.println("PATH ID = " + id);
        System.out.println("AUTH USER = " + authentication.getName());

        return workspaceService.getWorkspace(
                id,
                authentication.getName()
        );
    }

    @PutMapping("/{id}")
    public WorkspaceResponse updateWorkspace(
            @PathVariable UUID id,
            @Valid @RequestBody CreateWorkspaceRequest request,
            Authentication authentication
    ) {
        return workspaceService.updateWorkspace(
                id,
                request.getName(),
                authentication.getName()
        );
    }

    @DeleteMapping("/{workspaceId}")
    public void deleteWorkspace(
            @PathVariable UUID workspaceId,
            Authentication authentication
    ) {
        workspaceService.deleteWorkspace(
                workspaceId,
                authentication.getName()
        );
    }
}
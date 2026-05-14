package com.filecraft.controller;

import com.filecraft.dto.CreateWorkspaceRequest;
import com.filecraft.dto.WorkspaceResponse;
import com.filecraft.service.WorkspaceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/workspaces")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    public WorkspaceController(WorkspaceService workspaceService){
        this.workspaceService = workspaceService;
    }

    @PostMapping
    public WorkspaceResponse createWorkspace(@Valid @RequestBody CreateWorkspaceRequest request){
        return workspaceService.createWorkspace(request.getName());
    }

    @GetMapping("/{id}")
    public WorkspaceResponse getWorkspace(@PathVariable UUID id){
        return workspaceService.getWorkspace(id);
    }

}

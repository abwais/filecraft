package com.filecraft.controller;

import com.filecraft.dto.CreateWorkspaceRequest;
import com.filecraft.dto.WorkspaceResponse;
import com.filecraft.service.WorkspaceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.List;

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

    @GetMapping
    public List<WorkspaceResponse> getAllWorkspaces() {
        return workspaceService.getAllWorkspaces();
    }

    @PutMapping("/{id}")
    public WorkspaceResponse updateWorkspace(
            @PathVariable UUID id,
            @Valid @RequestBody CreateWorkspaceRequest request
    ) {
        return workspaceService.updateWorkspace(id, request.getName());
    }

    @DeleteMapping("/{workspaceId}")
    public void deleteWorkspace(@PathVariable UUID workspaceId) throws Exception{
        workspaceService.deleteWorkspace(workspaceId);
    }



}

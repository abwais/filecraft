package com.filecraft.service;

import com.filecraft.dto.WorkspaceResponse;
import com.filecraft.entity.Workspace;
import com.filecraft.exception.WorkspaceNotFoundException;
import com.filecraft.repository.WorkspaceRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;

    public WorkspaceService(WorkspaceRepository workspaceRepository){
        this.workspaceRepository = workspaceRepository;
    }

    public WorkspaceResponse createWorkspace(String name){
        Workspace workspace = new Workspace();
        workspace.setName(name);

        Workspace savedWorkspace = workspaceRepository.save(workspace);

        return toResponse(savedWorkspace);
    }

    public WorkspaceResponse getWorkspace(UUID id){
        Workspace workspace = workspaceRepository.findById(id).orElseThrow(() -> new WorkspaceNotFoundException(id));
        return toResponse(workspace);
    }

    private WorkspaceResponse toResponse(Workspace workspace){
        WorkspaceResponse response = new WorkspaceResponse();
        response.setId(workspace.getId());
        response.setName(workspace.getName());
        response.setCreatedAt(workspace.getCreatedAt());
        response.setUpdatedAt(workspace.getUpdatedAt());
        return response;
    }

}


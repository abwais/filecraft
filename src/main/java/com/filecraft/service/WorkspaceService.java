package com.filecraft.service;

import com.filecraft.dto.WorkspaceResponse;
import com.filecraft.entity.Workspace;
import com.filecraft.repository.WorkspaceRepository;
import com.filecraft.repository.FileAssetRepository;

import com.filecraft.exception.WorkspaceNotEmptyException;
import com.filecraft.exception.WorkspaceNotFoundException;

import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.List;
@Service
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final FileAssetRepository fileAssetRepository;

    public WorkspaceService(
            WorkspaceRepository workspaceRepository,
            FileAssetRepository fileAssetRepository
    ) {
        this.workspaceRepository = workspaceRepository;
        this.fileAssetRepository = fileAssetRepository;
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

    public List<WorkspaceResponse> getAllWorkspaces() {
        return workspaceRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public WorkspaceResponse updateWorkspace(UUID id, String name) {

        Workspace workspace = workspaceRepository.findById(id)
                .orElseThrow(() -> new WorkspaceNotFoundException(id));

        workspace.setName(name);

        Workspace savedWorkspace = workspaceRepository.save(workspace);

        return toResponse(savedWorkspace);
    }

    public void deleteWorkspace(UUID workspaceId) throws Exception {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));

        if(!workspace.getId().equals(workspaceId)){
            throw new WorkspaceNotFoundException(workspaceId);
        }

        if (fileAssetRepository.existsByWorkspaceId(workspaceId)) {
            throw new WorkspaceNotEmptyException(workspaceId);
        }

        workspaceRepository.delete(workspace);
    }




}


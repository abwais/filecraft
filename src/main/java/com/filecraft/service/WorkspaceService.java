package com.filecraft.service;

import com.filecraft.dto.WorkspaceResponse;
import com.filecraft.entity.User;
import com.filecraft.entity.Workspace;
import com.filecraft.exception.WorkspaceNotFoundException;
import com.filecraft.repository.UserRepository;
import com.filecraft.repository.WorkspaceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;

    public WorkspaceService(
            WorkspaceRepository workspaceRepository,
            UserRepository userRepository
    ) {
        this.workspaceRepository = workspaceRepository;
        this.userRepository = userRepository;
    }

    public WorkspaceResponse createWorkspace(String name, String userEmail) {
        User user = getCurrentUser(userEmail);

        Workspace workspace = new Workspace();
        workspace.setName(name);
        workspace.setOwner(user);

        Workspace savedWorkspace = workspaceRepository.save(workspace);

        return toResponse(savedWorkspace);
    }

    public List<WorkspaceResponse> getWorkspaces(String userEmail) {
        User user = getCurrentUser(userEmail);

        return workspaceRepository.findByOwnerIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public WorkspaceResponse getWorkspace(UUID id, String userEmail) {
        User user = getCurrentUser(userEmail);

        Workspace workspace = workspaceRepository.findByIdAndOwnerId(id, user.getId())
                .orElseThrow(() -> new WorkspaceNotFoundException(id));

        return toResponse(workspace);
    }

    public WorkspaceResponse updateWorkspace(UUID id, String name, String userEmail) {
        User user = getCurrentUser(userEmail);

        Workspace workspace = workspaceRepository.findByIdAndOwnerId(id, user.getId())
                .orElseThrow(() -> new WorkspaceNotFoundException(id));

        workspace.setName(name);

        Workspace updatedWorkspace = workspaceRepository.save(workspace);

        return toResponse(updatedWorkspace);
    }

    public void deleteWorkspace(UUID id, String userEmail) {
        User user = getCurrentUser(userEmail);

        Workspace workspace = workspaceRepository.findByIdAndOwnerId(id, user.getId())
                .orElseThrow(() -> new WorkspaceNotFoundException(id));

        workspaceRepository.delete(workspace);
    }

    private User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private WorkspaceResponse toResponse(Workspace workspace) {
        WorkspaceResponse response = new WorkspaceResponse();
        response.setId(workspace.getId());
        response.setName(workspace.getName());
        response.setCreatedAt(workspace.getCreatedAt());
        response.setUpdatedAt(workspace.getUpdatedAt());
        return response;
    }
}
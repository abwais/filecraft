package com.filecraft.exception;

import java.util.UUID;

public class FileDoesNotBelongToWorkspaceException extends RuntimeException {

    public FileDoesNotBelongToWorkspaceException(UUID fileId, UUID workspaceId) {
        super("File " + fileId + " does not belong to workspace: " + workspaceId);
    }
}
package com.filecraft.exception;

import java.util.UUID;

public class WorkspaceNotEmptyException extends RuntimeException {

    public WorkspaceNotEmptyException(UUID id) {
        super("Workspace still contains files: " + id);
    }
}
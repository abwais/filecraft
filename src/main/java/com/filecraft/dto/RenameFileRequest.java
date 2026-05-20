package com.filecraft.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RenameFileRequest {

    @NotBlank(message = "Display name is required")
    @Size(max = 255, message = "Display name must be at most 255 characters")
    private String displayName;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
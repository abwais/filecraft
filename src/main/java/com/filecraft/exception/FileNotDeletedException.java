package com.filecraft.exception;

import java.util.UUID;

public class FileNotDeletedException extends RuntimeException {

    public FileNotDeletedException(UUID id) {
        super("File is not deleted: " + id);
    }
}
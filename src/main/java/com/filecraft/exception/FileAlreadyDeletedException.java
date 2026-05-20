package com.filecraft.exception;

import java.util.UUID;

public class FileAlreadyDeletedException extends RuntimeException {

    public FileAlreadyDeletedException(UUID id) {
        super("File already deleted: " + id);
    }


}
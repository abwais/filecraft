package com.filecraft.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
@Service
public class FileStorageService {

    @Value("${Filecraft.storage.root}")
    private String storageRoot;

    public Path saveFile(InputStream inputStream, String workspaceId, String storedFileName) throws Exception{
        Path workspacePath = Path.of(
                storageRoot, "workspaces", workspaceId, "original"
        );

        Files.createDirectories(workspacePath);

        Path targetPath = workspacePath.resolve(storedFileName);

        Files.copy(inputStream,targetPath,StandardCopyOption.REPLACE_EXISTING);

        return targetPath;
    }

}

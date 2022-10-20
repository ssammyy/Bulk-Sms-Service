package com.sammy.sendbulksmsservice.v2;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    public void init();

    public boolean save(MultipartFile file);

    public Resource load(String filename);

    public boolean deleteAll();

    public Stream<Path> loadAll();
}

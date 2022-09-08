package com.sammy.sendbulksmsservice.v2;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Random;
import java.util.stream.Stream;


import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class FilesStorageServiceImpl implements FileStorageService {

    private final Path root = Paths.get("uploads");
    String fileName = "BulkSms.xlsx";


    @Override
    public void init() {
        try {

            if (root.toFile().exists()) {
                //directory already exists
//                System.out.println("Directory exists");
            } else {
//                System.out.println("Directory does not exists");
                Files.createDirectory(root);
            }


        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    /***
     *
     * @return path
     */
    boolean filePathCreator() {
        String path = "./uploads";
        File f1 = new File(path);
        //Creating a folder using mkdir() method but first check whether it already exists
        if (f1.exists())
            return true;
        boolean bool = f1.mkdir();
        if (bool) {
            System.out.println("Folder is created successfully");
        } else {
            return true;
        }
        return true;
    }

    @Override
    public void save(MultipartFile file, String type) {
        try {
            log.info("file name printed >>..", file.getOriginalFilename());

            filePathCreator();
            Files.copy(file.getInputStream(), this.root.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    @Override
    public Resource load(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
        return true;
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!");
        }
    }

    public String generateBactchCode() {

        int n = 6;

        int lowerLimit = 97;

        // lower limit for LowerCase Letters
        int upperLimit = 122;

        Random random = new Random();

        // Create a StringBuffer to store the result
        StringBuffer r = new StringBuffer(n);

        for (int i = 0; i < n; i++) {

            // take a random value between 97 and 122
            int nextRandomChar = lowerLimit
                    + (int) (random.nextFloat()
                    * (upperLimit - lowerLimit + 1));

            // append a character at the end of bs
            r.append((char) nextRandomChar);
        }

        // return the resultant string
        String code = "BTC";
        return code + r.toString();
    }
}
package com.barry.uploadFile.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.barry.uploadFile.message.ResponseFile;
import com.barry.uploadFile.message.ResponseMessage;
import com.barry.uploadFile.model.FileDB;
import com.barry.uploadFile.service.FileStorageService;

@Controller
@CrossOrigin("http://localhost:8081")
public class FileController {

  @Autowired
  private FileStorageService storageService;

  @PostMapping("/upload")
  public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {
    String message = "";
    try {
      storageService.store(file);

      message = "Uploaded the file successfully: " + file.getOriginalFilename();
      return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
    } catch (Exception e) {
      message = "Could not upload the file: " + file.getOriginalFilename() + "!";
      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
    }
  }
  
  @PostMapping("/uploadMutiliple")
  public ResponseEntity<ResponseMessage> uploadAllFile(@RequestParam("file") MultipartFile files[]) {
    String message = "";
    
    if (files.length > 7) {
        throw new RuntimeException("too many files");
    }
 
    Arrays.asList(files)
    .stream()
    .forEach(file->{	
    	try {
    	      storageService.store(file);
    	    
    	    } catch (Exception e) {
    	  
    	    	e.printStackTrace();
    	 
    	    }
    
    });
    
    return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("files upload successfuly!")); 
  }
 

  @GetMapping("/files")
  public ResponseEntity<List<ResponseFile>> getListFiles() {
    List<ResponseFile> files = storageService.getAllFiles().map(dbFile -> {
      String fileDownloadUri = ServletUriComponentsBuilder
          .fromCurrentContextPath()
          .path("/files/")
          .path(dbFile.getId())
          .toUriString();

      return new ResponseFile(
          dbFile.getName(),
          fileDownloadUri,
          dbFile.getType(),
          dbFile.getData().length);
    }).collect(Collectors.toList());

    return ResponseEntity.status(HttpStatus.OK).body(files);
  }

  @GetMapping("/files/{id}")
  public ResponseEntity<byte[]> getFile(@PathVariable String id) {
    FileDB fileDB = storageService.getFile(id);

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDB.getName() + "\"")
        .body(fileDB.getData());
  }
  @DeleteMapping("/files/delete/{id}")
  public ResponseEntity<?> deleteFile(@PathVariable String id) {
   // FileDB fileDB = storageService.getFile(id);
	  storageService.deleteFile(id);
    return new ResponseEntity<>(new ResponseMessage("Delete successfuly!") , HttpStatus.OK);
        
  }
}

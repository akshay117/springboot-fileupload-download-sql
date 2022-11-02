package com.fileoperation.demo.controller;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.fileoperation.demo.message.ResponseFile;
import com.fileoperation.demo.message.ResponseMessage;
import com.fileoperation.demo.model.FileDB;
import com.fileoperation.demo.service.FileStorageService;

@Controller
@CrossOrigin("http://localhost:8080")
public class FileController {
	
	@Autowired
	private FileStorageService fileStorageService;
	
	@PostMapping("/upload")
	public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {
		String Message = "";
		try {
			fileStorageService.store(file);
			Message = "uploaded successfully" + file.getOriginalFilename() + " ! " ;
			return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(Message));
		} catch (Exception e) {
			 Message = "Could not upload the file: " + file.getOriginalFilename() + "!";
			 return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(Message));
		}
	}
	
	@GetMapping("/files/{id}")
	public ResponseEntity<byte[]> getFile(@PathVariable String id)
	{
		FileDB file = fileStorageService.getFile(id);
		 return ResponseEntity.ok()
				              .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
				              .body(file.getData());
	}
	
	@GetMapping("/files")
	public ResponseEntity<List<ResponseFile>> getFileList(){
		
		List<ResponseFile> files = fileStorageService.getAllFiles().map(dbFile -> {
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
	
	
}

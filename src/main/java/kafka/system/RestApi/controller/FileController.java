package kafka.system.RestApi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import kafka.system.RestApi.data.vo.v1.PersonVO;
import kafka.system.RestApi.data.vo.v1.UploadFileResponseVO;
import kafka.system.RestApi.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/file/v1")
@Tag(name = "File", description = "Endpoints for file")
public class FileController {

    private final Logger logger = Logger.getLogger(FileController.class.getName());

    @Autowired
    private FileStorageService service;

    @PostMapping("/uploadFile")
    @Operation(summary = "Upload a file",
            description = "Adds new file",
            tags = {"File"},
            responses = {
                    @ApiResponse(description = "Created", responseCode = "200", content =
                    @Content(schema = @Schema(implementation = PersonVO.class))
                    ),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "internal Error", responseCode = "500", content = @Content)
            })
    public UploadFileResponseVO uploadFile(@RequestParam(value = "file") MultipartFile file){
        logger.info("Storing file to disk");

        var filename = service.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/api/file/v1/downloadFile/")
                .path(filename)
                .toUriString();

        return new UploadFileResponseVO(filename, fileDownloadUri, file.getContentType(), file.getSize());
    }

    @PostMapping("/uploadMultipleFiles")
    @Operation(summary = "Upload multiple files",
            description = "Adds new files",
            tags = {"Files"},
            responses = {
                    @ApiResponse(description = "Created", responseCode = "200", content =
                    @Content(schema = @Schema(implementation = PersonVO.class))
                    ),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "internal Error", responseCode = "500", content = @Content)
            })
    public List<UploadFileResponseVO> uploadMultipleFiles(@RequestParam(value = "files") MultipartFile[] files){
        logger.info("Storing files to disk");

        return Arrays.stream(files)
                .map(this::uploadFile)
                .collect(Collectors.toList());
    }

    @GetMapping("/downloandFile/{filename:.+}")
    @Operation(summary = "Download a file",
            description = "Download a file",
            tags = {"File"},
            responses = {
                    @ApiResponse(description = "Created", responseCode = "200", content =
                    @Content(schema = @Schema(implementation = PersonVO.class))
                    ),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "internal Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<Resource> downloandFile(@PathVariable(value = "filename") String filename, HttpServletRequest request){
        logger.info("Reading a file on disk");

        Resource resource = service.loadFileAsResource(filename);
        String contentType = "";

        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        }catch (Exception e){
            logger.info("Could not determine type");
        }

        if(contentType.isBlank()) contentType = "application/octet-stream";

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }



}

package kafka.system.RestApi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kafka.system.RestApi.data.vo.v1.BookVO;
import kafka.system.RestApi.data.vo.v1.PersonVO;
import kafka.system.RestApi.service.BookService;
import kafka.system.RestApi.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/book/v1")
@Tag(name = "Book", description = "Endpoints for books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping//(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Operation(summary = "Finds all books",
            description = "Finds all books",
            tags = {"Book"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = PersonVO.class))
                            )
                    }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "internal Error", responseCode = "500", content = @Content)
            })
    public List<BookVO> findAll() throws Exception {
        return bookService.findAll();
    }

    @GetMapping(value = "/{id}") //, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "application/x-yaml"})
    @Operation(summary = "Finds a book",
            description = "Finds a book",
            tags = {"Book"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content =
                    @Content(schema = @Schema(implementation = PersonVO.class))
                    ),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "internal Error", responseCode = "500", content = @Content)
            })
    public BookVO findById(@PathVariable(value = "id") Long id) throws Exception {

        return bookService.findById(id);
    }

    @PostMapping//(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Operation(summary = "Adds new Book",
            description = "Adds new Book",
            tags = {"Book"},
            responses = {
                    @ApiResponse(description = "Created", responseCode = "200", content =
                    @Content(schema = @Schema(implementation = PersonVO.class))
                    ),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "internal Error", responseCode = "500", content = @Content)
            })
    public BookVO create(@RequestBody BookVO book) throws Exception {
        return bookService.create(book);
    }

    @PutMapping//(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Operation(summary = "Update a book",
            description = "Update a book",
            tags = {"Book"},
            responses = {
                    @ApiResponse(description = "Updated", responseCode = "200", content =
                    @Content(schema = @Schema(implementation = PersonVO.class))
                    ),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "internal Error", responseCode = "500", content = @Content)
            })
    public BookVO update(@RequestBody BookVO book) throws Exception {
        return bookService.update(book);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete a book",
            description = "Delete a book",
            tags = {"Book"},
            responses = {
                    @ApiResponse(description = "No content", responseCode = "200", content = @Content
                    ),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "internal Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<?> delete(@PathVariable(value = "id") Long id) throws Exception {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

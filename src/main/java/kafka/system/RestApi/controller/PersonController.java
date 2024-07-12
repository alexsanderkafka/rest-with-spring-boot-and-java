package kafka.system.RestApi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.websocket.server.PathParam;
import kafka.system.RestApi.data.vo.v1.PersonVO;
import kafka.system.RestApi.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/person/v1")
@Tag(name = "People", description = "Endpoints for Mananing People")
public class PersonController {

    @Autowired
    private PersonService personService;

    @GetMapping//(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Operation(summary = "Finds all people",
            description = "Finds all people",
            tags = {"People"},
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
    public ResponseEntity<PagedModel<EntityModel<PersonVO>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) throws Exception {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "firstName"));
        return ResponseEntity.ok(personService.findAll(pageable));
    }

    @GetMapping(value = "/findPersonByName/{firstName}")
    @Operation(summary = "Find people by name",
            description = "Find people by name",
            tags = {"People"},
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
    public ResponseEntity<PagedModel<EntityModel<PersonVO>>> findPersonsByName(
            @PathVariable(value = "firstName") String firstName,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) throws Exception {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "firstName"));
        return ResponseEntity.ok(personService.findPersonByName(firstName, pageable));
    }


    @CrossOrigin(origins = "http://localhost:8080")
    @GetMapping(value = "/{id}") //, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "application/x-yaml"})
    @Operation(summary = "Finds a person",
            description = "Finds a person",
            tags = {"People"},
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
    public PersonVO findById(@PathVariable(value = "id") Long id) throws Exception {

        return personService.findById(id);
    }

    @CrossOrigin(origins = {"http://localhost:8080", "https://alex.com.br"})
    @PostMapping//(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Operation(summary = "Adds new Person",
            description = "Adds new Person",
            tags = {"People"},
            responses = {
                    @ApiResponse(description = "Created", responseCode = "200", content =
                    @Content(schema = @Schema(implementation = PersonVO.class))
                    ),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "internal Error", responseCode = "500", content = @Content)
            })
    public PersonVO create(@RequestBody PersonVO person) throws Exception {
        return personService.create(person);
    }

    @PutMapping//(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Operation(summary = "Update person",
            description = "Update a person",
            tags = {"People"},
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
    public PersonVO update(@RequestBody PersonVO person) throws Exception {
        return personService.update(person);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete a person",
            description = "Delete a person",
            tags = {"People"},
            responses = {
                    @ApiResponse(description = "No content", responseCode = "200", content = @Content
                    ),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "internal Error", responseCode = "500", content = @Content)
            })
    public ResponseEntity<?> delete(@PathVariable(value = "id") Long id) throws Exception {
        personService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{id}") //, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "application/x-yaml"})
    @Operation(summary = "Disable a person",
            description = "Disable a person",
            tags = {"People"},
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
    public PersonVO disablePerson(@PathVariable(value = "id") Long id) throws Exception {

        return personService.disablePerson(id);
    }
}

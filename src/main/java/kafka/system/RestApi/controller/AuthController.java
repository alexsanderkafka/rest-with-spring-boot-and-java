package kafka.system.RestApi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kafka.system.RestApi.data.vo.v1.security.AccountCredentialsVO;
import kafka.system.RestApi.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication Endpoint")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(
            summary = "Autheticates a user and returns a token"
    )
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody AccountCredentialsVO data){
        if(checkIfParamsIsNotNull(data)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request");

        var token = authService.signin(data);

        if(token == null) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request");

        return token;
    }

    @Operation(
            summary = "Refresh token for authenticated user and returns a token"
    )
    @PutMapping("/refresh/{username}")
    public ResponseEntity<?> refreshToken(@PathVariable("username") String username, @RequestHeader("Authorization") String refreshToken){
        if(checkIfParamsIsNotNull(refreshToken, username)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request");

        var token = authService.refreshToken(username, refreshToken);

        if(token == null) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request");

        return token;
    }

    public boolean checkIfParamsIsNotNull(AccountCredentialsVO data){
        return data == null || data.getUserName() == null || data.getUserName().isBlank()
                || data.getPassword() == null ||data.getPassword().isBlank();
    }

    public boolean checkIfParamsIsNotNull(String refreshToken, String username){
        return refreshToken == null || refreshToken.isBlank()
               || username == null || username.isBlank();
    }
}

package kafka.system.RestApi.service;

import kafka.system.RestApi.data.vo.v1.security.AccountCredentialsVO;
import kafka.system.RestApi.data.vo.v1.security.TokenVO;
import kafka.system.RestApi.repositories.UserRepository;
import kafka.system.RestApi.securityJwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> signin(AccountCredentialsVO data){
        try {
            var username = data.getUserName();
            var password = data.getPassword();

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

            var user = userRepository.findByUserName(username);

            //System.out.println("User password: " + user.getPassword());

            var tokenResponse = new TokenVO();
            if (user != null){
                System.out.println("User password: " + user.getPassword());
                tokenResponse = tokenProvider.createAccessToken(username, user.getRoles());
            }else{
                throw new UsernameNotFoundException("Username " + username + " not found!");
            }

            return ResponseEntity.ok(tokenResponse);
        }
        catch (Exception e){
            throw new BadCredentialsException("Invalid username/password supplied!");
        }

    }

    public ResponseEntity<?> refreshToken(String username, String refreshToken){
        var user = userRepository.findByUserName(username);

        var tokenResponse = new TokenVO();

        if (user != null){
            System.out.println("User password: " + user.getPassword());
            tokenResponse = tokenProvider.refreshToken(refreshToken);
        }else{
            throw new UsernameNotFoundException("Username " + username + " not found!");
        }

        return ResponseEntity.ok(tokenResponse);
    }
}

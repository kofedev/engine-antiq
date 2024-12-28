package dev.kofe.kengine.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.kofe.kengine.dto.UserDTO;
import dev.kofe.kengine.model.User;
import dev.kofe.kengine.model.Role;
import dev.kofe.kengine.service.UserService;
import dev.kofe.kengine.token.KEToken;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

import static dev.kofe.kengine.constant.KEConstant.AUTH_HEADER;
import static dev.kofe.kengine.constant.KEConstant.SECRET;

@RestController
public class UserController {

    private UserService userService;
    private KEToken keToken;

    public UserController(UserService userService, KEToken keToken) {
        this.userService = userService;
        this.keToken = keToken;
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    public boolean checkIfEmailExists(@RequestParam(name = "email", defaultValue = "") String email) {
        return userService.loadUserByEmail(email) != null;
    }

    @GetMapping("/refresh-token")
    public void generateNewAccessToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String jwtRefreshToken = keToken.extractTokenFromHeaderIfExists(request.getHeader(AUTH_HEADER));
        if (jwtRefreshToken != null) {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTVerifier jwtVerifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = jwtVerifier.verify(jwtRefreshToken);
            String email = decodedJWT.getSubject();
            User user = userService.loadUserByEmail(email);
            String jwtAccessToken = keToken.generateAccessToken(user.getEmail(), user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
            response.setContentType("application/json");
            new ObjectMapper().writeValue(response.getOutputStream(), keToken.getTokensMap(jwtAccessToken, jwtRefreshToken));
        } else {
            throw new RuntimeException("Refresh token required"); //@ToDo log and try to avoid RTE
        }
    }

    @PutMapping("/user/pass")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    public UserDTO updatePasswordUser(@RequestBody UserDTO userDTO) {
        return userService.changePassword(userDTO);
    }

    @PutMapping("/user/email")
    @PreAuthorize("hasAuthority('Admin') or hasAuthority('Staff')")
    public UserDTO updateEmailUser(@RequestBody UserDTO userDTO) {
        return userService.changeEmail(userDTO);
    }

    @PutMapping("/role")
    @PreAuthorize("hasAuthority('Admin')")
    public UserDTO updateSingleRoleUser(@RequestBody UserDTO userDTO) {
        return userService.changeSingleRole(userDTO);
    }

}

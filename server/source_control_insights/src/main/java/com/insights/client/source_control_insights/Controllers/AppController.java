package com.insights.client.source_control_insights.Controllers;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insights.client.source_control_insights.Entities.Role;
import com.insights.client.source_control_insights.Entities.User;
import com.insights.client.source_control_insights.Models.LoginRequestBody;
import com.insights.client.source_control_insights.Repositories.RoleRepository;
import com.insights.client.source_control_insights.Repositories.UserRepository;
import com.insights.client.source_control_insights.Services.GoogleAuthService;
import static com.insights.client.source_control_insights.lib.JwtHelpers.extractClaims;
import static com.insights.client.source_control_insights.lib.JwtHelpers.generateJWT;


@RestController
public class AppController {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public AppController(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("public/auth")
    public String getLogin(@RequestBody LoginRequestBody loginReq, GoogleAuthService googleAuthService)
            throws Exception {
        System.out.println(loginReq.getAuthCode());
        System.out.println(loginReq.authCode);
        // get the google jwt
        String jwt = googleAuthService.getJWT(loginReq.getAuthCode());
        Map<String, Object> claims = extractClaims(jwt);
        // get the email of this user
        String email = claims.get("email").toString();
        System.out.println(jwt);
        // look up their role in the database, if it doesn't exist then just assign them
        // the user role
        List<User> users = userRepository.findByEmail(email);
        System.out.println(users);
        if (users.isEmpty()) {
            System.out.println("The user array is of length 0");
            List<Role> roles = null;
            // no users with this email so we need to insert them
            roles = roleRepository.findByRoleName("User");
            User user = new User(email, email, email, roles);
            userRepository.save(user);
            users = userRepository.findByEmail(email);
        }
        List<String> userRoles = null;
        userRoles = (new ArrayList<Role>(users.get(0).getRoles())).stream().map((role) -> role.roleName)
                .collect(Collectors.toList());
        String[] rolesArray = userRoles.toArray(String[]::new);
        System.out.println(userRoles);
        String jwt_ = generateJWT(rolesArray, email);
        return jwt_;
    }

    @GetMapping("public/jwks.json")
    public JsonNode getJwks() throws IOException {
        ClassPathResource resource = new ClassPathResource("static/jwks.json");
        String content = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        return objectMapper.readTree(content);
    }

    @GetMapping("/api/test-admin")
    public String getTestOwner() {
        return "Only an admin should be seeing this";
    }

    @GetMapping("/api/test-user")
    public String getTestContributor() {
        return "Any user can see this";
    }
}

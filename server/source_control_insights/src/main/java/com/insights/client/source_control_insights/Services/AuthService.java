package com.insights.client.source_control_insights.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.insights.client.source_control_insights.Entities.Role;
import com.insights.client.source_control_insights.Entities.User;
import com.insights.client.source_control_insights.Models.LoginRequestBody;
import com.insights.client.source_control_insights.Repositories.RoleRepository;
import com.insights.client.source_control_insights.Repositories.UserRepository;
import static com.insights.client.source_control_insights.lib.JwtHelpers.extractClaims;
import static com.insights.client.source_control_insights.lib.JwtHelpers.generateJWT;
import java.time.temporal.ChronoUnit;
import java.time.Instant;

@Service
public class AuthService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public AuthService(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    public String login(LoginRequestBody loginReq, GoogleAuthService googleAuthService) throws Exception {
        // get the google jwt
        String jwt = googleAuthService.getJWT(loginReq.getAuthCode());
        Map<String, Object> claims = extractClaims(jwt);

        // get info from the user's jwt
        String email = claims.get("email").toString();
        String google_sub = claims.get("sub").toString();
        String username = claims.get("name").toString();
        long exp = ((Instant.now().toEpochMilli() + 3600000));

        // look up their role in the database, if they don't exist yet then
        // create them and just assign them the dev role
        List<User> users = userRepository.findByGoogleId(google_sub);
        if (users.isEmpty()) {
            List<Role> roles = roleRepository.findByRoleName("DEV");
            User user = new User(email, username, google_sub, roles);
            userRepository.save(user);
            users = userRepository.findByEmail(email);
        }
        List<String> userRoles = (new ArrayList<>(users.get(0).getRoles())).stream().map((role) -> role.roleName)
                .collect(Collectors.toList());

        String[] rolesArray = userRoles.toArray(String[]::new);
        return generateJWT(rolesArray, email, google_sub, username, exp);
    }

}

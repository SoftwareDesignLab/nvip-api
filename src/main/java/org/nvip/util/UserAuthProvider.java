package org.nvip.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.nvip.api.serializers.UserDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class UserAuthProvider {

    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;

    /**
     * Avoid storing the secret key as plain text in the source code.
     */
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(UserDTO user) {
        // created date
        Date now = new Date();
        // expiration date
        // old code had this at 3 hours for non-admins and 5 days for admins
        Date expirationDate = new Date(now.getTime() + 10800000); // 3 hours
        if (user.getUserID() == 2)
            expirationDate = new Date(now.getTime() + 432000000); // 5 days
        // create JSON Web Token based on user credentials
        return JWT.create()
                .withIssuer(user.getUserName())
                .withIssuedAt(now)
                .withExpiresAt(expirationDate)
                .withClaim("userID", user.getUserID())
                .withClaim("firstName", user.getFirstName())
                .withClaim("lastName", user.getLastName())
                .withClaim("roleID", user.getRoleId())
                .sign(Algorithm.HMAC256(secretKey));
    }

    public Authentication validateToken(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secretKey)).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        UserDTO user = UserDTO.builder()
                .userID(decodedJWT.getClaim("userID").asInt())
                .userName(decodedJWT.getIssuer())
                .firstName(decodedJWT.getClaim("firstName").asString())
                .lastName(decodedJWT.getClaim("lastName").asString())
                .roleId(decodedJWT.getClaim("roleID").asInt())
                .build();
        return new UsernamePasswordAuthenticationToken(user, null, null);
    }
}

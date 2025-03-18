package com.insights.client.source_control_insights.lib;


import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.jsonwebtoken.Jwts;

public class JwtHelpers {
    // used to extract the claims from the jwt
    public static Map<String, Object> extractClaims(String googleJwt) throws java.text.ParseException {
        SignedJWT signedJWT = SignedJWT.parse(googleJwt);
        JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
        return claimsSet.getClaims();
    }

    public static String generateJWT(String[] scopes, String subject, String google_sub, String name, long exp) throws Exception {
        String base64Key = System.getenv("PRIVATE_KEY");

        RSAPrivateKey privateKey = readPKCS8PrivateKey(base64Key);

        String jwt = Jwts.builder()
                .setSubject(subject)
                .setIssuer("insights.com")
                .setExpiration(new Date(exp))
                .claim("sub", google_sub)
                .claim("scope", scopes)
                .claim("username", name)
                .signWith(privateKey)
                .compact();
        return jwt;
    }

    public static RSAPrivateKey readPKCS8PrivateKey(String key) throws Exception {
        String privateKeyPEM = key
                .replaceAll(System.lineSeparator(), "");

        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

}

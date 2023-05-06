package com.ssp.platform.security.jwt;

import com.ssp.platform.property.SecurityJwtProperty;
import com.ssp.platform.security.service.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Конфигурация токена, токен в себе содержит username, дату создания и истечения срока действия и зашифровывается
 * кодовым словом
 * @author Василий Воробьев
 */
@Component
public class JwtUtils
{
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    private final Key key;
    private final long jwtTokenTime;

    @Autowired
    public JwtUtils(SecurityJwtProperty securityJwtProperty)
    {
        String jwtSecret = securityJwtProperty.getJwtSecret();
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.jwtTokenTime = securityJwtProperty.getJwtTokenTime() * 1000;
    }

    public String generateJwtToken(Authentication authentication)
    {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtTokenTime))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUserNameFromJwtToken(String token)
    {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken)
    {
        try
        {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        }
        catch (MalformedJwtException e)
        {
            logger.error("Invalid JWT token: {}", e.getMessage());
        }
        catch (ExpiredJwtException e)
        {
            logger.error("JWT token is expired: {}", e.getMessage());
        }
        catch (UnsupportedJwtException e)
        {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        }
        catch (IllegalArgumentException e)
        {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        catch (Exception e) {
            logger.error("Bad JWT token: {}", e.getMessage());
        }

        return false;
    }
}

package pl.ochnios.ninjabe.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    @Value("${custom.jwt.passphrase}")
    private String passphrase;

    @Value("${custom.jwt.expiration:1h}")
    private Duration expiration;

    @Value("${custom.jwt.cookie.name:accessToken}")
    private String jwtCookieName;

    @Value("${custom.jwt.cookie.secure:true}")
    private boolean secure;

    @Value("${custom.jwt.cookie.httpOnly:true}")
    private boolean httpOnly;

    @Value("${custom.jwt.cookie.acceptAsHeader:false}")
    private boolean acceptAsHeader;

    private SecretKey secret;

    @PostConstruct
    public void init() {
        secret = createSecretKey();
    }

    public String generateJwt(UserDetails userDetails) {
        final var currentDate = new Date();
        final var expirationMillis = currentDate.getTime() + expiration.getSeconds() * 1000;
        final var expirationDate = new Date(expirationMillis);
        return Jwts.builder()
                .issuedAt(currentDate)
                .expiration(expirationDate)
                .subject(userDetails.getUsername())
                .signWith(secret)
                .compact();
    }

    public String getUsername(String jwt) {
        return Jwts.parser()
                .verifyWith(secret)
                .build()
                .parseSignedClaims(jwt)
                .getPayload()
                .getSubject();
    }

    public boolean validateJwt(String jwt) {
        try {
            Jwts.parser().verifyWith(secret).build().parseSignedClaims(jwt);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public void setJwtCookie(HttpServletResponse response, String jwt) {
        setJwtCookie(response, jwt, (int) expiration.getSeconds());
    }

    public void unsetJwtCookie(HttpServletResponse response) {
        setJwtCookie(response, "", 0);
    }

    public Optional<String> getJwt(HttpServletRequest request) {
        final var cookies = request.getCookies();
        if (cookies != null) {
            final var jwtCookie =
                    Arrays.stream(cookies)
                            .filter(cookie -> cookie.getName().equals(jwtCookieName))
                            .findFirst();
            return jwtCookie.filter(this::isJwtCookieValid).map(Cookie::getValue);
        } else {
            return acceptAsHeader
                    ? Optional.of(request.getHeader(jwtCookieName))
                    : Optional.empty();
        }
    }

    private SecretKey createSecretKey() {
        final var passphraseBytes = passphrase.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(passphraseBytes);
    }

    private void setJwtCookie(HttpServletResponse response, String jwt, int maxAge) {
        final var jwtCookie = new Cookie(jwtCookieName, jwt);
        jwtCookie.setSecure(secure);
        jwtCookie.setHttpOnly(httpOnly);
        jwtCookie.setMaxAge(maxAge);
        jwtCookie.setPath("/");
        response.addCookie(jwtCookie);
    }

    private boolean isJwtCookieValid(Cookie cookie) {
        if (secure && !cookie.getSecure()) {
            return false;
        } else if (httpOnly && !cookie.isHttpOnly()) {
            return false;
        } else {
            return true;
        }
    }
}

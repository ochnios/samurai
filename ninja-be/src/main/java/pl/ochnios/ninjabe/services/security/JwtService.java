package pl.ochnios.ninjabe.services.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
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

    @Value("${custom.jwt.cookie.prefix:Bearer_}")
    private String prefix;

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
            return Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals(jwtCookieName))
                    .findFirst()
                    .map(Cookie::getValue)
                    .map(this::removeJwtPrefix);

        } else if (acceptAsHeader) {
            return Optional.ofNullable(request.getHeader(jwtCookieName)).map(this::removeJwtPrefix);
        } else {
            return Optional.empty();
        }
    }

    private SecretKey createSecretKey() {
        final var passphraseBytes = passphrase.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(passphraseBytes);
    }

    private void setJwtCookie(HttpServletResponse response, String jwt, int maxAge) {
        final var jwtWithPrefix = addJwtPrefix(jwt);
        final var jwtCookie = new Cookie(jwtCookieName, jwtWithPrefix);
        jwtCookie.setSecure(secure);
        jwtCookie.setHttpOnly(httpOnly);
        jwtCookie.setMaxAge(maxAge);
        jwtCookie.setPath("/");
        response.addCookie(jwtCookie);
    }

    private String addJwtPrefix(String jwt) {
        return prefix + jwt;
    }

    private String removeJwtPrefix(String jwtWithPrefix) {
        if (jwtWithPrefix.startsWith(prefix)) {
            return jwtWithPrefix.substring(prefix.length());
        } else {
            return jwtWithPrefix;
        }
    }
}

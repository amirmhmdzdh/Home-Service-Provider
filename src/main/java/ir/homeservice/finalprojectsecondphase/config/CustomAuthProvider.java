package ir.homeservice.finalprojectsecondphase.config;

import ir.homeservice.finalprojectsecondphase.exception.NotFoundException;
import ir.homeservice.finalprojectsecondphase.model.user.Users;
import ir.homeservice.finalprojectsecondphase.service.CustomerUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class CustomAuthProvider implements AuthenticationProvider {

    private final CustomerUserDetailsService customerUserDetailsService;

    private final PasswordEncoder passwordEncoder;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        Users users = (Users) customerUserDetailsService
                .loadUserByUsername(authentication.getName()
                );
        if (passwordEncoder.matches(((String) authentication.getCredentials()), users.getPassword())) {
            return new UsernamePasswordAuthenticationToken(
                    users, null, users.getAuthorities()
            );
        }
        throw new NotFoundException("wrong information");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}

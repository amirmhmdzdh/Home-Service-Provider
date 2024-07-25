package ir.homeservice.finalprojectsecondphase.config;

import ir.homeservice.finalprojectsecondphase.model.user.enums.Role;
import ir.homeservice.finalprojectsecondphase.service.CustomerUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final BCryptPasswordEncoder passwordEncoder;
    private final CustomerUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> {
                    authorize.requestMatchers("/customer/register-Customer", "/specialist/signUp-Specialist"
                            , "/admin/signUp-admin", "/customer/send-payment-info").permitAll();
                    authorize.requestMatchers("/admin/**").hasAnyAuthority(Role.ADMIN.name());
                    authorize.requestMatchers("/customer/**").hasAuthority(Role.CUSTOMER.name());
                    authorize.requestMatchers("/specialist/**").hasAuthority(Role.SPECIALIST.name());
                    authorize.anyRequest().authenticated();
                })
                .authenticationProvider(new CustomAuthProvider(userDetailsService, passwordEncoder))
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
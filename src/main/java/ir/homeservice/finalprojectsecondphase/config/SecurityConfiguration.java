package ir.homeservice.finalprojectsecondphase.config;

import ir.homeservice.finalprojectsecondphase.model.user.enums.Role;
import ir.homeservice.finalprojectsecondphase.service.CustomerUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {


    private final BCryptPasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                //.logout(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/customer/register-Customer").permitAll()
                        .requestMatchers("/specialist/signUp-Specialist").permitAll()
                        .requestMatchers("/admin/signUp-admin").permitAll()
                        .requestMatchers("/customer/send-payment-info").permitAll()
                        .requestMatchers("/admin/**").hasAnyAuthority(Role.ADMIN.name())
                        .requestMatchers("/customer/**").hasAuthority(Role.CUSTOMER.name())
                        .requestMatchers("/specialist/**").hasAuthority(Role.SPECIALIST.name())
                        .anyRequest().authenticated())
                .authenticationProvider(new CustomAuthProvider(
                        (CustomerUserDetailsService) userDetailsService(), passwordEncoder))
                .httpBasic(basic -> {
                })
        ;
        return http.build();
    }

//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        auth
//                .userDetailsService(email -> userService
//                        .findByEmail(email)
//                        .orElseThrow(() -> new NotFoundException(String.format("This %s notFound!", email))))
//                .passwordEncoder(passwordEncoder);
//    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomerUserDetailsService();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
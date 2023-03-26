package com.iamjunhyeok.petSitterAndWalker.configuration;

import com.iamjunhyeok.petSitterAndWalker.constants.Security;
import com.iamjunhyeok.petSitterAndWalker.repository.LoginLogRepository;
import com.iamjunhyeok.petSitterAndWalker.security.AuthenticationFilter;
import com.iamjunhyeok.petSitterAndWalker.security.CustomAuthenticationManager;
import com.iamjunhyeok.petSitterAndWalker.security.ExceptionHandlerFilter;
import com.iamjunhyeok.petSitterAndWalker.security.JWTAuthorizationFilter;
import com.iamjunhyeok.petSitterAndWalker.service.UtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
public class SecurityConfiguration {

    private final CustomAuthenticationManager customAuthenticationManager;

    private final LoginLogRepository loginLogRepository;

    private final UtilService utilService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(customAuthenticationManager, loginLogRepository, utilService);
        authenticationFilter.setFilterProcessesUrl("/login");

        http
                .headers().frameOptions().disable()
                .and()
                .csrf().disable()
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/h2/**").permitAll()
                        .requestMatchers(HttpMethod.POST, Security.REGISTER_PATH).permitAll()
                        .requestMatchers(HttpMethod.POST, Security.VERIFICATION_PATH).permitAll()
                        .requestMatchers(HttpMethod.POST, Security.VERIFY_PATH).permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .addFilterBefore(new ExceptionHandlerFilter(), authenticationFilter.getClass())
                .addFilter(authenticationFilter)
                .addFilterAfter(new JWTAuthorizationFilter(), authenticationFilter.getClass());

        return http.build();
    }
}

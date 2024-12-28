package dev.kofe.kengine.security;

import dev.kofe.kengine.filter.JWTAuthenticationFilter;
import dev.kofe.kengine.filter.JWTAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import dev.kofe.kengine.token.KEToken;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private KEToken kEngineToken;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests().antMatchers("/refresh-token/**").permitAll();
        http.authorizeRequests().antMatchers("/issue/**").permitAll();
        http.authorizeRequests().antMatchers("/img/**").permitAll();
        http.authorizeRequests().antMatchers("/language/common/**").permitAll();
        http.authorizeRequests().antMatchers("/category/common/**").permitAll();
        http.authorizeRequests().antMatchers("/product/common/**").permitAll();
        http.authorizeRequests().antMatchers("/image/common/**").permitAll();
        http.authorizeRequests().antMatchers("/order/common/**").permitAll();
        http.authorizeRequests().antMatchers("/uishort/common/**").permitAll();
        http.authorizeRequests().antMatchers("/uibig/common/**").permitAll();
        http.authorizeRequests().anyRequest().authenticated();
        http.addFilter(new JWTAuthenticationFilter(authenticationManager(http.getSharedObject(AuthenticationConfiguration.class)), kEngineToken));
        http.addFilterBefore(new JWTAuthorizationFilter(kEngineToken), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

}

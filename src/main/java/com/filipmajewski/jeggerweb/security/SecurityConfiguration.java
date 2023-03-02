package com.filipmajewski.jeggerweb.security;

import com.filipmajewski.jeggerweb.repository.SessionRepository;
import com.filipmajewski.jeggerweb.session.LoginSuccessListener;
import com.filipmajewski.jeggerweb.session.LogoutSuccessListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private UserDetailsService userDetailsService;

    private LogoutSuccessListener logoutSuccessListener;

    private SessionRepository sessionRepository;

    public SecurityConfiguration(UserDetailsService userDetailsService, LogoutSuccessListener logoutSuccessListener, SessionRepository sessionRepository) {
        this.userDetailsService = userDetailsService;
        this.logoutSuccessListener = logoutSuccessListener;
        this.sessionRepository = sessionRepository;
    }

    @Bean
    public AuthenticationSuccessHandler loginSuccessHandler() {return new LoginSuccessListener();}

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(new BCryptPasswordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeRequests(request -> {
                    request.antMatchers("/login").permitAll();
                    request.anyRequest().authenticated();
                })
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(loginSuccessHandler())
                        .permitAll())
                .logout(logout -> logout
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .addLogoutHandler(logoutSuccessListener)
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() throws Exception{
        return (web) -> web.ignoring().antMatchers("/css/**", "/images/**", "/js/**");
    }

}

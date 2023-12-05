package service.ms_search_engine;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Value("spring.security.admin.username")
    private String adminUsername;

    @Value("spring.security.admin.password")
    private String adminPassword;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2Login(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
//                .formLogin(Customizer.withDefaults())


                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        //static page
                        .requestMatchers(HttpMethod.GET, "/batchSearch", "/taskView").authenticated()
                        .requestMatchers(HttpMethod.GET, "/","/msSearch").permitAll()
                        .requestMatchers("/login").permitAll()
                        //static resources
                        .requestMatchers(HttpMethod.GET, "/css/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/js/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/icon/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/picture/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/demoData/**").authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/compound/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/compoundData/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/spectrum/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/spectrum/**","/api/compound/**").hasRole("ADMIN")
                        .requestMatchers( "/api/batchSearch/**").authenticated()
                        .requestMatchers("/api/user").authenticated()
                        .anyRequest().denyAll()
                );


        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        return new InMemoryUserDetailsManager(
                User.withUsername("admin")
                        .password("{noop}ccl-22840485")
                        .authorities("ADMIN", "USER")
                        .roles("ADMIN", "USER")
                        .build());
    }

}

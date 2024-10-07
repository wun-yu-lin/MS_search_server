package service.ms_search_engine.config;

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
public class SecurityConfiguration extends BaseConfig {

    private static final String[] AUTH_WHITELIST = {
            "/api-doc/**",
            "/swagger-ui/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
//                .formLogin(Customizer.withDefaults())
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/logIn")
                        .defaultSuccessUrl("/OAuthSuccess", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .headers(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())

                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        //swagger hub
                        //.requestMatchers(AUTH_WHITELIST).authenticated()
                        .requestMatchers(AUTH_WHITELIST).hasAnyRole("ADMIN")


                        //static page
                        .requestMatchers(HttpMethod.GET, "/batchSearch", "/taskView", "/OAuthSuccess").authenticated()
                        .requestMatchers(HttpMethod.GET, "/be/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/be").authenticated()
                        .requestMatchers(HttpMethod.GET, "/","/msSearch").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/logIn").permitAll()
                        //static resources
                        //loaderio
                        .requestMatchers("/loaderio-4ec2846ed840a5bee0888f814d57dd62.txt").permitAll()
                        .requestMatchers(HttpMethod.GET, "/css/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/js/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/icon/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/picture/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/demoData/**").authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/config/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/config/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/compound/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/compoundData/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/spectrum/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/webStatus/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/spectrum/**","/api/compound/**").hasRole("ADMIN")
                        .requestMatchers( "/api/batchSearch/**").authenticated()
                        .requestMatchers("/api/member/auth").permitAll()
                        .requestMatchers("/api/member/**").authenticated()

                        .anyRequest().denyAll()
                );


        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        return new InMemoryUserDetailsManager(
                User.withUsername(serverConfig.getAdminUsername())
                        .password("{noop}"+serverConfig.getAdminPassword())
                        .authorities("ADMIN", "USER")
                        .roles("ADMIN", "USER")
                        .build());
    }

}

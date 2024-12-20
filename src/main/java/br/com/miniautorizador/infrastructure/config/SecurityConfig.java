package br.com.miniautorizador.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * Configura a cadeia de filtros de segurança.
     *
     * @param http Objeto HttpSecurity para configuração.
     * @return Instância de SecurityFilterChain.
     * @throws Exception Caso ocorra algum erro na configuração.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desativa a proteção CSRF para simplificação
                .csrf(AbstractHttpConfigurer::disable)

                // Configura a autorização de requisições
                .authorizeHttpRequests(auth -> auth
                        // Qualquer requisição requer autenticação
                        .anyRequest().authenticated()
                )

                // Configura a autenticação HTTP Basic
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    /**
     * Define o encoder de senhas utilizando BCrypt.
     *
     * @return Instância de PasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Define o serviço de detalhes do usuário com autenticação em memória.
     *
     * @param passwordEncoder Encoder de senhas para hash.
     * @return Instância de UserDetailsService.
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user = User.builder()
                .username("username")
                .password(passwordEncoder.encode("password"))
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }
}
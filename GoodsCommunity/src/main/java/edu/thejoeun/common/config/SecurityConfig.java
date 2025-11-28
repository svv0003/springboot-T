package edu.thejoeun.common.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * @Configuration - 환경설정용 클래스임을 명시
 * 스프링부트는 프로젝트 실행할 때 @Configuration 어노테이션을 가장 먼저 확인
 * 객체로 생성해서 내부 코드를  서버 실행시 모두 실행
 *
 * @Bean
 * 개발자가 수동으로 생성한 객체의 관리를
 * 스프링부트에서 자체적으로 관리하라고 넘기는 어노테이션
 */
@Configuration
public class SecurityConfig {


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/profile_images/**").permitAll()
                        .anyRequest().authenticated()

                )
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .permitAll()
                );
            return http.build();
    }
     */
}

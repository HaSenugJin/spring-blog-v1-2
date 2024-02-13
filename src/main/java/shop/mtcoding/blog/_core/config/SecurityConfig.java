package shop.mtcoding.blog._core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

@Configuration // 컴퍼넌트 스캔
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder(); // IOC 등록, 시큐리티가 고르인할때 어떤 해시로 비교해야하는지 알게됨
    }

    // configure 보다 먼저 적용됨
    // 이러면 저 url은 제외하고 configure가 실행됨
    @Bean
    public WebSecurityCustomizer ignore() { // 정적자원 시큐리티 폴더에서 제외시키기.
        return w -> w.ignoring().requestMatchers("/static/**", "/h2-console/**");
    }

    @Bean
    SecurityFilterChain configure(HttpSecurity http) throws Exception {

        http.csrf(c -> c.disable());

        // 인증이 필요한 페이지 설정
        http.authorizeHttpRequests(a -> {
            // board/** = /board/{id}/delete 등을 모두 포함한다.
            a.requestMatchers(RegexRequestMatcher.regexMatcher("/board/\\d+")).permitAll()
                    .requestMatchers("/user/**", "/board/**").authenticated()
                    .anyRequest().permitAll();
        });

        // 인증이 필요한 페이지라면 loginForm으로 전부 보냄
        http.formLogin(f -> {
            f.loginPage("/loginForm").loginProcessingUrl("/login").defaultSuccessUrl("/").failureUrl("/loginForm");
        });

        return http.build();
    }
}
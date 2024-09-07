package net.datasa.ruruplan.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 시큐리티 환경설정
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
   //로그인 없이 접근 가능 경로
    private static final String[] PUBLIC_URLS = {
           "/"                       //root
           , "/join/**"              //JoinController
           , "/login/**"             //FindId, FindPw, Login, ResetController
           , "/member/login"         //loginForm.html의 action url
           , "/custom/**"            //customPlanController
           , "/css/**"               //css파일
           , "/fonts/**"             //fonts파일
           , "/images/**"            //이미지 파일
           , "/javascript.member/**" //자바스크립트 멤버 파일
           , "/js/**"                //js 파일
    };

    @Bean
    protected SecurityFilterChain config(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(author -> author
                        .requestMatchers(PUBLIC_URLS).permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .formLogin(formLogin -> formLogin // 여기에는 html 이름이 아닌, 컨트롤러로 만든 url로 들어가야함
                        .loginPage("/login/loginForm")
                        .usernameParameter("memberId")
                        .passwordParameter("memberPw")
                        .loginProcessingUrl("/member/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/member/logout")
                        .logoutSuccessUrl("/")
                );

        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}

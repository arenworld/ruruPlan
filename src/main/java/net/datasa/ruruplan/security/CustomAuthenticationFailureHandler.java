package net.datasa.ruruplan.security;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import java.io.IOException;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        // 로그인 실패 이유를 쿼리 파라미터로 전달
        String errorMessage = "Invalid credentials"; // 기본 메시지
        if (exception.getMessage().equalsIgnoreCase("User is disabled")) {
            errorMessage = "Your account is disabled";
        } else if (exception.getMessage().equalsIgnoreCase("User account has expired")) {
            errorMessage = "Your account has expired";
        }
        response.sendRedirect("/login/loginForm?error=true&exception=" + errorMessage);
    }
}

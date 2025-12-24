package com.thisjowi.auth.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.thisjowi.auth.utils.JwtUtil;
import com.thisjowi.auth.service.UserService;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;

/**
 * Handler for OAuth2 authentication success.
 * Generates JWT token and redirects to client.
 */
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(OAuth2SuccessHandler.class);

    private final JwtUtil jwtUtil;
    private final UserService userService;

    public OAuth2SuccessHandler(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            String email = oauth2User.getAttribute("email");
            
            if (email == null) {
                // Fallback for providers that might not return email directly or use different attribute
                // For GitHub, we might need to make a separate API call if email is private, 
                // but for now let's assume email is available or use login as fallback if it looks like an email
                String login = oauth2User.getAttribute("login");
                if (login != null && login.contains("@")) {
                    email = login;
                } else {
                    log.warn("Email not found in OAuth2 attributes");
                    // We could throw exception or handle it, but let's proceed and let getUserByEmail fail
                }
            }
            
            log.info("OAuth2 success for user: {}", email);

            // Get user from DB (must have been created by OAuth2UserService)
            var user = userService.getUserByEmail(email);

            // Generate JWT token
            String jwtToken = jwtUtil.generateToken(user.getId(), email);

            // Redirect with token as query parameter
            String redirectUrl = "myapp://oauth-callback?token=" + jwtToken;
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);

        } catch (Exception e) {
            log.error("Error in OAuth2 success handler", e);
            getRedirectStrategy().sendRedirect(request, response, "/login?error=true");
        }
    }
}

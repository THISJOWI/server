package uk.thisjowi.Authentication.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.thisjowi.Authentication.service.UserService;
import uk.thisjowi.Authentication.utils.JwtUtil;

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
            String username = oauth2User.getAttribute("login");
            
            log.info("OAuth2 success for user: {}", username);

            // Get user from DB (must have been created by OAuth2UserService)
            var user = userService.getUserByUsername(username);

            // Generate JWT token
            String jwtToken = jwtUtil.generateToken(user.getId(), username);

            // Redirect with token as query parameter
            String redirectUrl = "myapp://oauth-callback?token=" + jwtToken;
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);

        } catch (Exception e) {
            log.error("Error in OAuth2 success handler", e);
            getRedirectStrategy().sendRedirect(request, response, "/login?error=true");
        }
    }
}

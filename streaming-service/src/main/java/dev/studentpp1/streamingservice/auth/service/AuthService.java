package dev.studentpp1.streamingservice.auth.service;

import dev.studentpp1.streamingservice.auth.dto.LoginUserRequest;
import dev.studentpp1.streamingservice.auth.dto.RegisterUserRequest;
import dev.studentpp1.streamingservice.auth.persistence.AuthenticatedUser;
import dev.studentpp1.streamingservice.users.application.usecase.UserService;
import dev.studentpp1.streamingservice.users.domain.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public void register(RegisterUserRequest request, HttpServletRequest httpServletRequest) throws Exception {
        User user = userService.createUser(request);
        UserDetails userDetails = new AuthenticatedUser(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );
        SecurityContext context = saveUserDetailsToSecurityContext(authentication);
        createHttpSession(httpServletRequest, context);
    }

    public void login(LoginUserRequest request, HttpServletRequest httpServletRequest) {
        UsernamePasswordAuthenticationToken authenticationTokenRequest = new UsernamePasswordAuthenticationToken(
                request.email(),
                request.password()
        );
        // now token is not authenticated
        // we go to ProviderManager with Authentication(principal = email, credentials = password, authenticated=false)
        // find provider that work with this type of token & delegate them authenticate()
        // DaoAuthenticationProvider -> UserServiceImpl -> find by email in db -> check password hash
        // create new token with roles
        // return Authentication(principal=UserDetails, credentials=null (password don't saved), authenticated=true, authorities=roles)
        Authentication authentication = authenticationManager.authenticate(authenticationTokenRequest);
        SecurityContext context = saveUserDetailsToSecurityContext(authentication);
        createHttpSession(httpServletRequest, context);
    }

    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
    }

    private SecurityContext saveUserDetailsToSecurityContext(Authentication authentication) {
        // SecurityContextHolder use ThreadLocal to save Context -> live during current request
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        return context;
    }

    private void createHttpSession(HttpServletRequest httpServletRequest, SecurityContext context) {
        // Save SecurityContext to HttpSession on server side
        // Cookie contains only sessionId (JSESSIONID)
        // Next request: SecurityContextPersistenceFilter
        //  - reads sessionId from cookie
        //  - finds HttpSession (save HashMap on server side with sessions)
        //  - loads SecurityContext from session into SecurityContextHolder
        HttpSession session = httpServletRequest.getSession(true);
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                context
        );
    }
}

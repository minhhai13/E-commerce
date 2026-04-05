package com.group2.ecommerce.filter;

import com.group2.ecommerce.entity.User;
import com.group2.ecommerce.entity.enums.Role;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Session-based security filter for role-based access control.
 * Checks HttpSession for "loggedInUser" attribute.
 */
@Component
public class SecurityFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String relativePath = path.substring(contextPath.length());

        // Allow public resources
        if (relativePath.startsWith("/static/")
                || relativePath.equals("/")
                || relativePath.equals("/home")
                || relativePath.equals("/login")
                || relativePath.equals("/logout")
                || relativePath.equals("/register")
                || relativePath.equals("/403")
                || relativePath.equals("/shop")
                || relativePath.equals("/categories")
                || relativePath.startsWith("/products/")
                || relativePath.startsWith("/cart")
                || relativePath.startsWith("/images/")
                || relativePath.startsWith("/api/sepay-webhook")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        User loggedInUser = (session != null) ? (User) session.getAttribute("loggedInUser") : null;

        if (loggedInUser == null) {
            httpResponse.sendRedirect(contextPath + "/login");
            return;
        }
        Role role = loggedInUser.getRole();

        // Role-based URL access control
        if (relativePath.startsWith("/admin/")) {
            if (role != Role.ADMIN) {
                httpResponse.sendRedirect(contextPath + "/403");
                return;
            }
        } else if (relativePath.startsWith("/staff/")) {
            if (role != Role.STAFF) {
                httpResponse.sendRedirect(contextPath + "/403");
                return;
            }
        } else if (relativePath.startsWith("/api/")) {
            if (role != Role.ADMIN && role != Role.STAFF) {
            }
        }

        chain.doFilter(request, response);
    }
}

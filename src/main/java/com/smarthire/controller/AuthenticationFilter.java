package com.smarthire.controller;

import com.smarthire.model.Role;
import com.smarthire.model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * AuthenticationFilter
 *
 * Central Role-Based Access Control (RBAC) filter guarding protected URL routes.
 * Intercepts requests for /student/*, /tpo/*, /recruiter/*, /profile/*
 */
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {
        "/student/*",
        "/tpo/*",
        "/recruiter/*",
        "/profile/*"
})
public class AuthenticationFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("AuthenticationFilter initialized successfully.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Prevent browser caching of protected views
        httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        httpResponse.setHeader("Pragma", "no-cache");
        httpResponse.setDateHeader("Expires", 0);

        HttpSession session = httpRequest.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        String requestUri = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String relativePath = requestUri.substring(contextPath.length());

        // 1. If not authenticated, redirect to login page
        if (currentUser == null) {
            logger.warn("Unauthenticated access attempt to: {}", relativePath);
            String encodedRedirect = URLEncoder.encode(relativePath, StandardCharsets.UTF_8);
            httpResponse.sendRedirect(contextPath + "/auth/login?error=unauthorized&redirect=" + encodedRedirect);
            return;
        }

        // 2. Role-based authorization rules
        Role role = currentUser.getRole();
        boolean authorized = false;

        if (relativePath.startsWith("/student/")) {
            authorized = (role == Role.STUDENT || role == Role.TPO_ADMIN);
        } else if (relativePath.startsWith("/tpo/")) {
            authorized = (role == Role.TPO_ADMIN);
        } else if (relativePath.startsWith("/recruiter/")) {
            authorized = (role == Role.RECRUITER || role == Role.TPO_ADMIN);
        } else if (relativePath.startsWith("/profile/")) {
            authorized = true; // Any authenticated user can manage their profile
        }

        if (!authorized) {
            logger.warn("Access Denied: User ID {} with role {} attempted to access {}", 
                    currentUser.getId(), role, relativePath);
            httpRequest.setAttribute("attemptedPath", relativePath);
            httpRequest.setAttribute("userRole", role.getDisplayName());
            httpRequest.getRequestDispatcher("/WEB-INF/views/auth/unauthorized.jsp").forward(httpRequest, httpResponse);
            return;
        }

        // 3. User is authorized -> continue along filter chain
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        logger.info("AuthenticationFilter destroyed.");
    }
}

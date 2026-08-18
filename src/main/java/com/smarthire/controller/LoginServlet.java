package com.smarthire.controller;

import com.smarthire.exception.AuthenticationException;
import com.smarthire.model.Role;
import com.smarthire.model.User;
import com.smarthire.service.AuthService;
import com.smarthire.service.AuthServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * LoginServlet
 *
 * Handles user authentication requests, session creation, and role-based redirects.
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/auth/login"})
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private AuthService authService;

    @Override
    public void init() throws ServletException {
        this.authService = new AuthServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // If already logged in, redirect straight to their dashboard
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            User currentUser = (User) session.getAttribute("currentUser");
            redirectToDashboard(currentUser.getRole(), request, response);
            return;
        }

        // Check for error parameters (e.g. redirected from filter)
        String errorParam = request.getParameter("error");
        if ("unauthorized".equalsIgnoreCase(errorParam)) {
            request.setAttribute("errorMessage", "Please log in to access the requested page.");
        } else if ("logged_out".equalsIgnoreCase(errorParam)) {
            request.setAttribute("successMessage", "You have been logged out successfully.");
        }

        String redirectUrl = request.getParameter("redirect");
        if (redirectUrl != null && !redirectUrl.isEmpty()) {
            request.setAttribute("redirectUrl", redirectUrl);
        }

        String preselectedRole = request.getParameter("role");
        if (preselectedRole != null && !preselectedRole.isEmpty()) {
            request.setAttribute("selectedRole", preselectedRole.toUpperCase());
        }

        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String redirectUrl = request.getParameter("redirectUrl");

        try {
            User authenticatedUser = authService.authenticate(email, password);

            // Create fresh session and store identity
            HttpSession session = request.getSession(true);
            session.setAttribute("currentUser", authenticatedUser);
            session.setAttribute("userRole", authenticatedUser.getRole().name());
            session.setAttribute("userEmail", authenticatedUser.getEmail());

            // If a valid safe redirect URL was requested, send user there
            if (redirectUrl != null && !redirectUrl.isEmpty() && redirectUrl.startsWith("/")) {
                response.sendRedirect(request.getContextPath() + redirectUrl);
                return;
            }

            // Otherwise, route to their role dashboard
            redirectToDashboard(authenticatedUser.getRole(), request, response);

        } catch (AuthenticationException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("email", email);
            request.setAttribute("redirectUrl", redirectUrl);
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
        }
    }

    private void redirectToDashboard(Role role, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String contextPath = request.getContextPath();
        switch (role) {
            case STUDENT:
                response.sendRedirect(contextPath + "/student/dashboard");
                break;
            case TPO_ADMIN:
                response.sendRedirect(contextPath + "/tpo/dashboard");
                break;
            case RECRUITER:
                response.sendRedirect(contextPath + "/recruiter/dashboard");
                break;
            default:
                response.sendRedirect(contextPath + "/");
                break;
        }
    }
}

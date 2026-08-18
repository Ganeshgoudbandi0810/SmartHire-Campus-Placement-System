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

import java.io.IOException;

/**
 * RegisterServlet
 *
 * Handles candidate (Student) and corporate (Recruiter) user registrations.
 */
@WebServlet(name = "RegisterServlet", urlPatterns = {"/auth/register"})
public class RegisterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private AuthService authService;

    @Override
    public void init() throws ServletException {
        this.authService = new AuthServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String roleParam = request.getParameter("role");
        if (roleParam != null && !roleParam.isEmpty()) {
            request.setAttribute("selectedRole", roleParam.toUpperCase());
        }
        request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String roleStr = request.getParameter("role");

        try {
            if (password == null || !password.equals(confirmPassword)) {
                throw new AuthenticationException("Passwords do not match. Please re-enter.");
            }

            Role role = Role.fromString(roleStr);
            if (role == null || role == Role.TPO_ADMIN) {
                throw new AuthenticationException("Please choose either Student or Recruiter as your role.");
            }

            User createdUser = authService.register(email, password, role);

            // Redirect to login with success indicator
            response.sendRedirect(request.getContextPath() + "/auth/login?success=registered&email=" + createdUser.getEmail());

        } catch (AuthenticationException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("email", email);
            request.setAttribute("selectedRole", roleStr);
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
        }
    }
}

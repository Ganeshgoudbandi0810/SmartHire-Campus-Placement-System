package com.smarthire.controller;

import com.smarthire.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * TpoDashboardServlet
 *
 * Gateway controller for authenticated Placement Officer (TPO) admin portal.
 */
@WebServlet(name = "TpoDashboardServlet", urlPatterns = {"/tpo/dashboard"})
public class TpoDashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        request.setAttribute("user", currentUser);
        request.getRequestDispatcher("/WEB-INF/views/tpo/dashboard.jsp").forward(request, response);
    }
}

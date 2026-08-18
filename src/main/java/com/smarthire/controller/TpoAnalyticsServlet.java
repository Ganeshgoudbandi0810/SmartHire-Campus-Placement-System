package com.smarthire.controller;

import com.smarthire.model.PlacementAnalyticsDTO;
import com.smarthire.service.AnalyticsService;
import com.smarthire.service.AnalyticsServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * TpoAnalyticsServlet
 *
 * Controller rendering the University Placement Intelligence & Accreditation Dashboard.
 */
@WebServlet(name = "TpoAnalyticsServlet", urlPatterns = {"/tpo/analytics"})
public class TpoAnalyticsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private AnalyticsService analyticsService;

    @Override
    public void init() throws ServletException {
        this.analyticsService = new AnalyticsServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        PlacementAnalyticsDTO analytics = analyticsService.getPlacementAnalytics();
        request.setAttribute("analytics", analytics);

        request.getRequestDispatcher("/WEB-INF/views/tpo/analytics.jsp").forward(request, response);
    }
}

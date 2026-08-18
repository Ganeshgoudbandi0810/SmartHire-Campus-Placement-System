package com.smarthire.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * HealthCheckServlet
 *
 * Verifies that the Jakarta Servlet container (Tomcat 10.1.x),
 * Java 21 LTS runtime, and MVC routing pipeline are functioning correctly.
 *
 * Mapped to:
 *   - /health      -> Forwards to protected JSP view (/WEB-INF/views/health.jsp)
 *   - /api/health  -> Returns diagnostic JSON payload
 */
@WebServlet(name = "HealthCheckServlet", urlPatterns = {"/health", "/api/health"})
public class HealthCheckServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Collect runtime metrics and system metadata
        Runtime runtime = Runtime.getRuntime();
        long totalMemoryMB = runtime.totalMemory() / (1024 * 1024);
        long freeMemoryMB = runtime.freeMemory() / (1024 * 1024);
        long usedMemoryMB = totalMemoryMB - freeMemoryMB;

        Map<String, Object> serverInfo = new HashMap<>();
        serverInfo.put("status", "UP");
        serverInfo.put("application", "SmartHire - Campus Placement System");
        serverInfo.put("javaVersion", System.getProperty("java.version"));
        serverInfo.put("javaVendor", System.getProperty("java.vendor"));
        serverInfo.put("osName", System.getProperty("os.name"));
        serverInfo.put("osArch", System.getProperty("os.arch"));
        serverInfo.put("serverContainer", getServletContext().getServerInfo());
        serverInfo.put("servletVersion", getServletContext().getMajorVersion() + "." + getServletContext().getMinorVersion());
        serverInfo.put("contextPath", request.getContextPath());
        serverInfo.put("totalMemoryMB", totalMemoryMB);
        serverInfo.put("usedMemoryMB", usedMemoryMB);
        serverInfo.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        String requestUri = request.getRequestURI();
        String acceptHeader = request.getHeader("Accept");

        // If JSON endpoint or JSON requested via header, render JSON directly
        if (requestUri.endsWith("/api/health") || (acceptHeader != null && acceptHeader.contains("application/json"))) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            try (PrintWriter out = response.getWriter()) {
                out.print(gson.toJson(serverInfo));
                out.flush();
            }
            return;
        }

        // Standard MVC flow: attach diagnostic model to request and forward to view
        request.setAttribute("serverInfo", serverInfo);
        request.getRequestDispatcher("/WEB-INF/views/health.jsp").forward(request, response);
    }
}

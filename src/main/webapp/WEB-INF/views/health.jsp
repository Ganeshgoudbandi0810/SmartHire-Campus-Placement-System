<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SmartHire | System Health Diagnostics</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

    <!-- Top Navigation Bar -->
    <nav class="navbar">
        <div class="container nav-container">
            <a href="${pageContext.request.contextPath}/" class="brand-logo">
                🎓 SmartHire <span class="brand-badge">v1.0</span>
            </a>
            <ul class="nav-links">
                <li><a href="${pageContext.request.contextPath}/" class="nav-link">Home</a></li>
                <li><a href="${pageContext.request.contextPath}/health" class="nav-link" style="color: var(--primary); font-weight: 700;">System Health</a></li>
                <li><a href="${pageContext.request.contextPath}/auth/login" class="btn btn-outline">Log In</a></li>
            </ul>
        </div>
    </nav>

    <!-- Health Diagnostic Card -->
    <main class="container">
        <div class="diag-container">
            <div class="diag-card">
                <div class="diag-header">
                    <h2>🩺 SmartHire Runtime Diagnostics</h2>
                    <span class="status-badge ok">● STATUS: ONLINE</span>
                </div>
                <div class="diag-body">
                    <p style="color: var(--text-secondary); margin-bottom: 1.5rem;">
                        This diagnostic dashboard verifies that the <strong>Jakarta Servlet 6.0 Engine</strong>, 
                        <strong>Apache Tomcat 10.1.x</strong>, and <strong>Java 21 LTS</strong> runtime environments are operational.
                    </p>

                    <table class="diag-table">
                        <tbody>
                            <tr>
                                <th>Application Name</th>
                                <td><strong>SmartHire - Campus Placement System</strong></td>
                            </tr>
                            <tr>
                                <th>Servlet Engine Status</th>
                                <td><span style="color: var(--success); font-weight: 600;">ACTIVE (Jakarta Servlet 6.0)</span></td>
                            </tr>
                            <tr>
                                <th>Context Path</th>
                                <td><code>${pageContext.request.contextPath == '' ? '/' : pageContext.request.contextPath}</code></td>
                            </tr>
                            <tr>
                                <th>Server Info</th>
                                <td><code>${serverInfo.serverContainer}</code></td>
                            </tr>
                            <tr>
                                <th>Java Runtime Version</th>
                                <td><code>${serverInfo.javaVersion} (${serverInfo.javaVendor})</code></td>
                            </tr>
                            <tr>
                                <th>Operating System</th>
                                <td><code>${serverInfo.osName} (${serverInfo.osArch})</code></td>
                            </tr>
                            <tr>
                                <th>Server Timestamp</th>
                                <td><code>${serverInfo.timestamp}</code></td>
                            </tr>
                            <tr>
                                <th>Allocated JVM Memory</th>
                                <td><code>${serverInfo.usedMemoryMB} MB used / ${serverInfo.totalMemoryMB} MB total</code></td>
                            </tr>
                            <tr>
                                <th>MVC Architecture Layer</th>
                                <td>
                                    <span class="brand-badge">Controller: HealthCheckServlet</span>
                                    <span class="brand-badge" style="margin-left: 0.5rem;">View: /WEB-INF/views/health.jsp</span>
                                </td>
                            </tr>
                        </tbody>
                    </table>

                    <div style="margin-top: 2rem; display: flex; justify-content: space-between; align-items: center;">
                        <a href="${pageContext.request.contextPath}/" class="btn btn-outline">&larr; Back to Home</a>
                        <a href="${pageContext.request.contextPath}/api/health" class="btn btn-outline" style="font-size: 0.85rem;" target="_blank">View Raw JSON API &rarr;</a>
                    </div>
                </div>
            </div>
        </div>
    </main>

    <!-- Footer -->
    <footer class="footer">
        <div class="container">
            <p>&copy; 2026 SmartHire Campus Placement System. Jakarta EE 10 + Java 21 LTS.</p>
        </div>
    </footer>

    <script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>

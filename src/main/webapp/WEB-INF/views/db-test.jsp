<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SmartHire | Database & Connection Pool Diagnostics</title>
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
                <li><a href="${pageContext.request.contextPath}/health" class="nav-link">System Health</a></li>
                <li><a href="${pageContext.request.contextPath}/db-test" class="nav-link" style="color: var(--primary); font-weight: 700;">Database Health</a></li>
                <li><a href="${pageContext.request.contextPath}/auth/login" class="btn btn-outline">Log In</a></li>
            </ul>
        </div>
    </nav>

    <!-- Database Diagnostic Dashboard -->
    <main class="container">
        <div class="diag-container">
            <div class="diag-card">
                <div class="diag-header" style="background-color: ${dbReport.status == 'CONNECTED' ? '#0f172a' : '#7f1d1d'};">
                    <h2>🗄️ MySQL & HikariCP Diagnostics</h2>
                    <c:choose>
                        <c:choose>
                            <c:when test="${dbReport.status == 'CONNECTED'}">
                                <span class="status-badge ok">● STATUS: CONNECTED</span>
                            </c:when>
                            <c:otherwise>
                                <span class="status-badge" style="background-color: rgba(239, 68, 68, 0.2); color: #f87171; border: 1px solid rgba(248, 113, 113, 0.4);">
                                    ● STATUS: CONNECTION FAILED
                                </span>
                            </c:otherwise>
                        </c:choose>
                    </c:choose>
                </div>
                
                <div class="diag-body">
                    <c:if test="${dbReport.status != 'CONNECTED'}">
                        <div style="background-color: var(--danger-light); border: 1px solid var(--danger); border-radius: var(--radius-md); padding: 1.25rem; margin-bottom: 1.5rem;">
                            <h3 style="color: var(--danger); font-size: 1.1rem; margin-bottom: 0.5rem;">⚠️ Database Connection Error</h3>
                            <p style="color: var(--text-primary); font-size: 0.95rem; margin-bottom: 0.5rem;"><strong>Error Type:</strong> <code>${dbReport.errorType}</code></p>
                            <p style="color: var(--text-secondary); font-size: 0.9rem;"><strong>Message:</strong> ${dbReport.errorMessage}</p>
                            <hr style="margin: 0.75rem 0; border: none; border-top: 1px solid rgba(220, 38, 38, 0.2);">
                            <p style="font-size: 0.85rem; color: var(--text-secondary);">
                                💡 <em>Troubleshooting Tip:</em> Ensure MySQL 8.0 is running and that credentials in <code>src/main/resources/db.properties</code> match your local MySQL installation.
                            </p>
                        </div>
                    </c:if>

                    <c:if test="${dbReport.status == 'CONNECTED'}">
                        <h3 style="font-size: 1.15rem; margin-bottom: 0.75rem; color: var(--text-primary);">Database Server Details</h3>
                        <table class="diag-table" style="margin-bottom: 2rem;">
                            <tbody>
                                <tr>
                                    <th>Database Engine</th>
                                    <td><strong>${dbReport.databaseProduct}</strong> (Version: <code>${dbReport.databaseVersion}</code>)</td>
                                </tr>
                                <tr>
                                    <th>JDBC Driver</th>
                                    <td><code>${dbReport.driverName}</code> (v${dbReport.driverVersion})</td>
                                </tr>
                                <tr>
                                    <th>Connection URL</th>
                                    <td><code>${dbReport.url}</code></td>
                                </tr>
                                <tr>
                                    <th>Connection Latency</th>
                                    <td><span style="color: var(--success); font-weight: 600;">⚡ ${dbReport.connectionLatencyMs} ms</span></td>
                                </tr>
                            </tbody>
                        </table>

                        <h3 style="font-size: 1.15rem; margin-bottom: 0.75rem; color: var(--text-primary);">HikariCP Connection Pool Statistics</h3>
                        <table class="diag-table" style="margin-bottom: 2rem;">
                            <tbody>
                                <tr>
                                    <th>Pool Name</th>
                                    <td><code>${dbReport.poolStats.poolName}</code></td>
                                </tr>
                                <tr>
                                    <th>Active Connections (in use)</th>
                                    <td><code>${dbReport.poolStats.activeConnections}</code></td>
                                </tr>
                                <tr>
                                    <th>Idle Connections (ready)</th>
                                    <td><code>${dbReport.poolStats.idleConnections}</code></td>
                                </tr>
                                <tr>
                                    <th>Total Leased/Pool Capacity</th>
                                    <td><code>${dbReport.poolStats.totalConnections} / ${dbReport.poolStats.maxPoolSize}</code> (Min Idle: ${dbReport.poolStats.minIdle})</td>
                                </tr>
                            </tbody>
                        </table>

                        <h3 style="font-size: 1.15rem; margin-bottom: 0.75rem; color: var(--text-primary);">Schema Table Verification</h3>
                        <table class="diag-table">
                            <thead>
                                <tr>
                                    <th style="width: 50%;">Table Name</th>
                                    <th>Record Count</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="entry" items="${dbReport.tableCounts}">
                                    <tr>
                                        <td><code>${entry.key}</code></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${entry.value >= 0}">
                                                    <span style="color: var(--success); font-weight: 600;">✓ ${entry.value} rows</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span style="color: var(--danger); font-weight: 600;">✗ Table missing or unreadable</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:if>

                    <div style="margin-top: 2rem; display: flex; justify-content: space-between; align-items: center;">
                        <a href="${pageContext.request.contextPath}/" class="btn btn-outline">&larr; Back to Home</a>
                        <a href="${pageContext.request.contextPath}/api/db-test" class="btn btn-outline" style="font-size: 0.85rem;" target="_blank">View Raw JSON Pool Metrics &rarr;</a>
                    </div>
                </div>
            </div>
        </div>
    </main>

    <!-- Footer -->
    <footer class="footer">
        <div class="container">
            <p>&copy; 2026 SmartHire Campus Placement System. Powered by HikariCP & MySQL 8.0.</p>
        </div>
    </footer>

    <script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SmartHire | Access Denied (403)</title>
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
                <li><a href="${pageContext.request.contextPath}/auth/logout" class="btn btn-outline">Log Out</a></li>
            </ul>
        </div>
    </nav>

    <!-- 403 Forbidden Container -->
    <main class="container" style="max-width: 650px; margin: 4rem auto; text-align: center;">
        <div class="card" style="padding: 3rem 2rem; border-top: 4px solid var(--danger);">
            <div style="font-size: 3.5rem; margin-bottom: 1rem;">🚫</div>
            <h1 style="font-size: 2rem; font-weight: 800; color: var(--danger); margin-bottom: 0.75rem;">
                403 - Access Denied
            </h1>
            <p style="color: var(--text-secondary); font-size: 1.05rem; margin-bottom: 1.5rem;">
                You do not have permission to access the requested resource: <br>
                <code>${attemptedPath != null ? attemptedPath : 'Protected Area'}</code>
            </p>

            <div style="background-color: var(--bg-main); border: 1px solid var(--border-color); border-radius: var(--radius-md); padding: 1.25rem; margin-bottom: 2rem; text-align: left;">
                <p style="font-size: 0.9rem; color: var(--text-primary); margin-bottom: 0.35rem;">
                    <strong>Your Current Account:</strong> ${sessionScope.currentUser.email}
                </p>
                <p style="font-size: 0.9rem; color: var(--text-primary);">
                    <strong>Assigned Role:</strong> <span class="brand-badge">${sessionScope.currentUser.role.displayName}</span>
                </p>
            </div>

            <div style="display: flex; gap: 1rem; justify-content: center; flex-wrap: wrap;">
                <c:choose>
                    <c:when test="${sessionScope.currentUser.role == 'STUDENT'}">
                        <a href="${pageContext.request.contextPath}/student/dashboard" class="btn btn-primary">&larr; Return to Student Dashboard</a>
                    </c:when>
                    <c:when test="${sessionScope.currentUser.role == 'TPO_ADMIN'}">
                        <a href="${pageContext.request.contextPath}/tpo/dashboard" class="btn btn-primary">&larr; Return to TPO Dashboard</a>
                    </c:when>
                    <c:when test="${sessionScope.currentUser.role == 'RECRUITER'}">
                        <a href="${pageContext.request.contextPath}/recruiter/dashboard" class="btn btn-primary">&larr; Return to Recruiter Hub</a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/" class="btn btn-primary">&larr; Return to Home</a>
                    </c:otherwise>
                </c:choose>

                <a href="${pageContext.request.contextPath}/auth/logout" class="btn btn-outline">Switch Account / Sign Out</a>
            </div>
        </div>
    </main>

    <!-- Footer -->
    <footer class="footer">
        <div class="container">
            <p>&copy; 2026 SmartHire Campus Placement System. Role-Based Access Control Active.</p>
        </div>
    </footer>

</body>
</html>

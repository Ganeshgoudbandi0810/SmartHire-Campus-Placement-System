<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SmartHire | My Placement Applications</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .page-header {
            background-color: var(--bg-card);
            border-bottom: 1px solid var(--border-color);
            padding: 2rem 0;
            margin-bottom: 2rem;
        }
        .table-card {
            background-color: var(--bg-card);
            border: 1px solid var(--border-color);
            border-radius: var(--radius-lg);
            overflow: hidden;
            box-shadow: var(--shadow-sm);
        }
        .custom-table {
            width: 100%;
            border-collapse: collapse;
            font-size: 0.95rem;
        }
        .custom-table th, .custom-table td {
            padding: 1rem 1.25rem;
            text-align: left;
            border-bottom: 1px solid var(--border-color);
        }
        .custom-table th {
            background-color: var(--bg-main);
            color: var(--text-secondary);
            font-weight: 600;
            font-size: 0.85rem;
            text-transform: uppercase;
        }
        .custom-table tr:hover {
            background-color: #f8fafc;
        }
        .alert-success {
            background-color: var(--success-light);
            color: var(--success);
            border: 1px solid rgba(22, 163, 74, 0.3);
            padding: 0.85rem 1.25rem;
            border-radius: var(--radius-md);
            margin-bottom: 1.5rem;
            font-size: 0.95rem;
        }
    </style>
</head>
<body>

    <!-- Top Navigation Bar -->
    <nav class="navbar">
        <div class="container nav-container">
            <a href="${pageContext.request.contextPath}/student/dashboard" class="brand-logo">
                🎓 SmartHire <span class="brand-badge">Student Portal</span>
            </a>
            <ul class="nav-links">
                <li><a href="${pageContext.request.contextPath}/student/dashboard" class="nav-link">Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/student/profile" class="nav-link">My Profile</a></li>
                <li><a href="${pageContext.request.contextPath}/student/drives" class="nav-link">Placement Drives</a></li>
                <li><a href="${pageContext.request.contextPath}/student/applications" class="nav-link" style="color: var(--primary); font-weight: 700;">My Applications</a></li>
                <li><a href="${pageContext.request.contextPath}/auth/logout" class="btn btn-outline" style="font-size: 0.85rem;">Sign Out</a></li>
            </ul>
        </div>
    </nav>

    <!-- Header / Banner -->
    <header class="page-header">
        <div class="container">
            <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem;">
                <div>
                    <h1 style="font-size: 1.75rem; font-weight: 800; margin-bottom: 0.25rem;">
                        My Placement Applications & Selection Status
                    </h1>
                    <p style="color: var(--text-secondary); font-size: 0.95rem;">
                        Track your candidate status across all applied campus recruitment drives
                    </p>
                </div>
                <div>
                    <a href="${pageContext.request.contextPath}/student/drives" class="btn btn-primary">
                        🔍 Explore More Drives
                    </a>
                </div>
            </div>
        </div>
    </header>

    <!-- Main Content -->
    <main class="container" style="margin-bottom: 4rem;">

        <!-- Flash Messages -->
        <c:if test="${not empty successMessage}">
            <div class="alert-success">
                ✓ ${successMessage}
            </div>
        </c:if>

        <div class="table-card">
            <c:choose>
                <c:when test="${not empty applications}">
                    <table class="custom-table">
                        <thead>
                            <tr>
                                <th>Company & Job Title</th>
                                <th>Package (LPA)</th>
                                <th>Submission Date</th>
                                <th>Live Selection Status</th>
                                <th>Remarks / Feedback</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="app" items="${applications}">
                                <tr>
                                    <td>
                                        <strong style="font-size: 1rem; color: var(--text-primary);">${app.jobTitle}</strong>
                                        <div style="font-size: 0.85rem; color: var(--text-secondary);">
                                            🏢 <strong>${app.companyName}</strong> (📍 ${app.jobLocation})
                                        </div>
                                    </td>
                                    <td>
                                        <span style="font-weight: 800; color: var(--primary); font-size: 1.05rem;">
                                            ${app.packageLpa} LPA
                                        </span>
                                    </td>
                                    <td>
                                        <span style="font-size: 0.85rem; color: var(--text-secondary);">
                                            ${app.appliedAt}
                                        </span>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${app.currentStatus == 'OFFERED' || app.currentStatus == 'ACCEPTED'}">
                                                <span class="status-badge ok" style="background-color: rgba(34, 197, 94, 0.2); color: #16a34a; font-weight: 800;">
                                                    🎉 ${app.currentStatus.displayName}
                                                </span>
                                            </c:when>
                                            <c:when test="${app.currentStatus == 'REJECTED'}">
                                                <span class="status-badge" style="background-color: var(--danger-light); color: var(--danger);">
                                                    ✗ ${app.currentStatus.displayName}
                                                </span>
                                            </c:when>
                                            <c:when test="${app.currentStatus == 'SHORTLISTED' || app.currentStatus == 'IN_PROCESS'}">
                                                <span class="status-badge" style="background-color: #eff6ff; color: var(--primary); border: 1px solid rgba(37, 99, 235, 0.3);">
                                                    ⚡ ${app.currentStatus.displayName}
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="status-badge" style="background-color: var(--bg-main); color: var(--text-secondary); border: 1px solid var(--border-color);">
                                                    ⏳ ${app.currentStatus.displayName}
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty app.rejectionReason}">
                                                <span style="font-size: 0.85rem; color: var(--danger);">${app.rejectionReason}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="font-size: 0.85rem; color: var(--text-muted);">Application under review</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <div style="padding: 3.5rem; text-align: center;">
                        <div style="font-size: 3rem; margin-bottom: 1rem;">📝</div>
                        <h2 style="font-size: 1.4rem; font-weight: 700; margin-bottom: 0.5rem;">No Applications Submitted Yet</h2>
                        <p style="color: var(--text-secondary); max-width: 500px; margin: 0 auto 1.5rem auto;">
                            Browse open campus recruitment drives and apply with 1-click once your profile is verified!
                        </p>
                        <a href="${pageContext.request.contextPath}/student/drives" class="btn btn-primary">
                            Browse Placement Drives &rarr;
                        </a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </main>

    <!-- Footer -->
    <footer class="footer">
        <div class="container">
            <p>&copy; 2026 SmartHire Candidate Workspace.</p>
        </div>
    </footer>

</body>
</html>

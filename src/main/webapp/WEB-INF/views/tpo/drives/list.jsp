<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SmartHire | TPO - All Campus Drives</title>
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
    </style>
</head>
<body>

    <!-- Top Navigation Bar -->
    <nav class="navbar">
        <div class="container nav-container">
            <a href="${pageContext.request.contextPath}/tpo/dashboard" class="brand-logo">
                🎓 SmartHire <span class="brand-badge" style="background-color: #fef3c7; color: #b45309; border-color: rgba(180, 83, 9, 0.3);">TPO Admin</span>
            </a>
            <ul class="nav-links">
                <li><a href="${pageContext.request.contextPath}/tpo/dashboard" class="nav-link">Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/tpo/students" class="nav-link">Students</a></li>
                <li><a href="${pageContext.request.contextPath}/tpo/companies" class="nav-link">Companies</a></li>
                <li><a href="${pageContext.request.contextPath}/tpo/drives" class="nav-link" style="color: var(--primary); font-weight: 700;">Placement Drives</a></li>
                <li><a href="${pageContext.request.contextPath}/auth/logout" class="btn btn-outline" style="font-size: 0.85rem;">Sign Out</a></li>
            </ul>
        </div>
    </nav>

    <!-- Header / Banner -->
    <header class="page-header">
        <div class="container">
            <h1 style="font-size: 1.75rem; font-weight: 800; margin-bottom: 0.25rem;">
                All Campus Placement Drives
            </h1>
            <p style="color: var(--text-secondary); font-size: 0.95rem;">
                Live oversight of recruiter drives, eligibility cutoffs, and applicant metrics
            </p>
        </div>
    </header>

    <!-- Main Content -->
    <main class="container" style="margin-bottom: 3rem;">
        <div class="table-card">
            <c:choose>
                <c:when test="${not empty drives}">
                    <table class="custom-table">
                        <thead>
                            <tr>
                                <th>Company & Job Title</th>
                                <th>Package (LPA)</th>
                                <th>Criteria Cutoffs</th>
                                <th>Application Deadline</th>
                                <th>Applicants</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="drive" items="${drives}">
                                <tr>
                                    <td>
                                        <strong style="font-size: 1rem; color: var(--text-primary);">${drive.jobTitle}</strong>
                                        <div style="font-size: 0.85rem; color: var(--text-secondary);">
                                            🏢 <strong>${drive.companyName}</strong> (${drive.companyIndustry})
                                        </div>
                                    </td>
                                    <td>
                                        <span style="font-weight: 800; color: var(--primary); font-size: 1.05rem;">
                                            ${drive.packageLpa} LPA
                                        </span>
                                    </td>
                                    <td>
                                        <div style="font-size: 0.85rem;">
                                            <div>Min CGPA: <strong>${drive.minCgpa}</strong></div>
                                            <div>Backlogs Allowed: <strong>&le; ${drive.maxBacklogsAllowed}</strong></div>
                                            <div>Branches: <code>${drive.eligibleBranches}</code></div>
                                        </div>
                                    </td>
                                    <td>
                                        <span style="font-size: 0.85rem; color: var(--text-secondary);">
                                            ${drive.applicationDeadline}
                                        </span>
                                    </td>
                                    <td>
                                        <span class="brand-badge" style="font-size: 0.85rem;">
                                            👥 ${drive.applicantCount} Applied
                                        </span>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${drive.openForApplication}">
                                                <span class="status-badge ok">● OPEN</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="status-badge" style="background-color: var(--danger-light); color: var(--danger); border: 1px solid rgba(220, 38, 38, 0.3);">
                                                    ● ${drive.status.displayName}
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <div style="padding: 3rem; text-align: center;">
                        <p style="font-size: 1.1rem; color: var(--text-secondary);">No placement drives found in the system.</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </main>

    <!-- Footer -->
    <footer class="footer">
        <div class="container">
            <p>&copy; 2026 SmartHire TPO Management Portal.</p>
        </div>
    </footer>

</body>
</html>

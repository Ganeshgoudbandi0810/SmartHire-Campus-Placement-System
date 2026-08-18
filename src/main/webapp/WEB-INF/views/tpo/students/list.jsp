<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SmartHire | TPO - Student Verification Roster</title>
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
            <a href="${pageContext.request.contextPath}/tpo/dashboard" class="brand-logo">
                🎓 SmartHire <span class="brand-badge" style="background-color: #fef3c7; color: #b45309; border-color: rgba(180, 83, 9, 0.3);">TPO Admin</span>
            </a>
            <ul class="nav-links">
                <li><a href="${pageContext.request.contextPath}/tpo/dashboard" class="nav-link">Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/tpo/students" class="nav-link" style="color: var(--primary); font-weight: 700;">Students</a></li>
                <li><a href="${pageContext.request.contextPath}/tpo/companies" class="nav-link">Companies</a></li>
                <li><a href="${pageContext.request.contextPath}/tpo/drives" class="nav-link">Placement Drives</a></li>
                <li><a href="${pageContext.request.contextPath}/auth/logout" class="btn btn-outline" style="font-size: 0.85rem;">Sign Out</a></li>
            </ul>
        </div>
    </nav>

    <!-- Header / Banner -->
    <header class="page-header">
        <div class="container">
            <h1 style="font-size: 1.75rem; font-weight: 800; margin-bottom: 0.25rem;">
                Student Academic Verification Roster
            </h1>
            <p style="color: var(--text-secondary); font-size: 0.95rem;">
                Audit candidate CGPA against official transcripts and verify student placement eligibility
            </p>
        </div>
    </header>

    <!-- Main Content -->
    <main class="container" style="margin-bottom: 3rem;">

        <!-- Flash Messages -->
        <c:if test="${not empty successMessage}">
            <div class="alert-success">
                ✓ ${successMessage}
            </div>
        </c:if>

        <div class="table-card">
            <c:choose>
                <c:when test="${not empty students}">
                    <table class="custom-table">
                        <thead>
                            <tr>
                                <th>Student Details</th>
                                <th>Department & Batch</th>
                                <th>Academic Marks</th>
                                <th>Backlogs</th>
                                <th>Resume</th>
                                <th>Verification</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="s" items="${students}">
                                <tr>
                                    <td>
                                        <strong style="color: var(--text-primary); font-size: 1rem;">${s.fullName}</strong>
                                        <div style="font-size: 0.8rem; color: var(--text-secondary);">
                                            Roll: <code>${s.rollNumber}</code> | ✉️ ${s.userEmail}
                                        </div>
                                    </td>
                                    <td>
                                        <span class="brand-badge">${s.department}</span>
                                        <div style="font-size: 0.8rem; color: var(--text-secondary); margin-top: 0.2rem;">
                                            Graduation: ${s.graduationYear}
                                        </div>
                                    </td>
                                    <td>
                                        <div>CGPA: <strong style="color: var(--primary); font-size: 1rem;">${s.cgpa}</strong></div>
                                        <div style="font-size: 0.8rem; color: var(--text-secondary);">
                                            10th: ${s.tenthPercentage}% | 12th: ${s.twelfthPercentage}%
                                        </div>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${s.activeBacklogs == 0}">
                                                <span style="color: var(--success); font-weight: 600;">0 Active</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="color: var(--danger); font-weight: 600;">⚠️ ${s.activeBacklogs} Active</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty s.resumeFilePath}">
                                                <a href="${pageContext.request.contextPath}/resume/download?studentId=${s.id}" target="_blank" class="btn btn-outline" style="font-size: 0.75rem; padding: 0.3rem 0.6rem;">
                                                    👁️ PDF
                                                </a>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="font-size: 0.8rem; color: var(--text-muted);">None</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${s.verified}">
                                                <span class="status-badge ok">✓ VERIFIED</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="status-badge" style="background-color: var(--warning-light); color: var(--warning); border: 1px solid rgba(217, 119, 6, 0.3);">
                                                    ⏳ PENDING
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/tpo/students/verify" method="POST" style="display: inline;">
                                            <input type="hidden" name="studentId" value="${s.id}">
                                            <input type="hidden" name="isVerified" value="${!s.verified}">
                                            <button type="submit" class="btn btn-outline" style="font-size: 0.75rem; padding: 0.35rem 0.7rem;">
                                                ${s.verified ? 'Revoke' : 'Approve'}
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <div style="padding: 3rem; text-align: center;">
                        <p style="font-size: 1.1rem; color: var(--text-secondary);">No student profiles found.</p>
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

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SmartHire | Our Recruitment Drives</title>
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
            letter-spacing: 0.05em;
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
            <a href="${pageContext.request.contextPath}/recruiter/dashboard" class="brand-logo">
                🎓 SmartHire <span class="brand-badge" style="background-color: #f3e8ff; color: #7e22ce; border-color: rgba(126, 34, 206, 0.3);">Recruiter Hub</span>
            </a>
            <ul class="nav-links">
                <li><a href="${pageContext.request.contextPath}/recruiter/dashboard" class="nav-link">Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/recruiter/jobs" class="nav-link" style="color: var(--primary); font-weight: 700;">Our Job Postings</a></li>
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
                        Recruitment Drives & Job Postings
                    </h1>
                    <p style="color: var(--text-secondary); font-size: 0.95rem;">
                        Organization: <strong>${company.companyName}</strong> 
                        <span class="status-badge ok" style="margin-left: 0.5rem;">VERIFIED RECRUITER</span>
                    </p>
                </div>
                <div>
                    <a href="${pageContext.request.contextPath}/recruiter/jobs/create" class="btn btn-primary">
                        ➕ Post New Job Drive
                    </a>
                </div>
            </div>
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
                <c:when test="${not empty jobs}">
                    <table class="custom-table">
                        <thead>
                            <tr>
                                <th>Job Title & Location</th>
                                <th>Package (LPA)</th>
                                <th>Eligibility Criteria</th>
                                <th>Deadline</th>
                                <th>Applicants</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="job" items="${jobs}">
                                <tr>
                                    <td>
                                        <strong style="color: var(--text-primary); font-size: 1rem;">${job.jobTitle}</strong>
                                        <div style="font-size: 0.85rem; color: var(--text-secondary);">
                                            📍 ${job.jobLocation} • <em>${job.employmentType.displayName}</em>
                                        </div>
                                    </td>
                                    <td>
                                        <span style="font-weight: 800; color: var(--primary); font-size: 1.05rem;">
                                            ${job.packageLpa} LPA
                                        </span>
                                    </td>
                                    <td>
                                        <div style="font-size: 0.85rem;">
                                            <div>Min CGPA: <strong>${job.minCgpa}</strong></div>
                                            <div>Backlogs: <strong>&le; ${job.maxBacklogsAllowed}</strong></div>
                                            <div>Branches: <code>${job.eligibleBranches}</code></div>
                                        </div>
                                    </td>
                                    <td>
                                        <span style="font-size: 0.85rem; color: var(--text-secondary);">
                                            ${job.applicationDeadline}
                                        </span>
                                    </td>
                                    <td>
                                        <span class="brand-badge" style="font-size: 0.85rem;">
                                            👥 ${job.applicantCount} Candidates
                                        </span>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${job.openForApplication}">
                                                <span class="status-badge ok">● OPEN</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="status-badge" style="background-color: var(--danger-light); color: var(--danger); border: 1px solid rgba(220, 38, 38, 0.3);">
                                                    ● ${job.status.displayName}
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/recruiter/jobs/status" method="POST" style="display: inline;">
                                            <input type="hidden" name="jobId" value="${job.id}">
                                            <c:choose>
                                                <c:when test="${job.status == 'OPEN'}">
                                                    <input type="hidden" name="status" value="CLOSED">
                                                    <button type="submit" class="btn btn-outline" style="font-size: 0.75rem; padding: 0.3rem 0.6rem;">
                                                        Close Drive
                                                    </button>
                                                </c:when>
                                                <c:otherwise>
                                                    <input type="hidden" name="status" value="OPEN">
                                                    <button type="submit" class="btn btn-outline" style="font-size: 0.75rem; padding: 0.3rem 0.6rem;">
                                                        Reopen Drive
                                                    </button>
                                                </c:otherwise>
                                            </c:choose>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <div style="padding: 3rem; text-align: center;">
                        <p style="font-size: 1.1rem; color: var(--text-secondary); margin-bottom: 1rem;">
                            No campus placement drives posted yet.
                        </p>
                        <a href="${pageContext.request.contextPath}/recruiter/jobs/create" class="btn btn-primary">
                            Create Your First Job Drive &rarr;
                        </a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

    </main>

    <!-- Footer -->
    <footer class="footer">
        <div class="container">
            <p>&copy; 2026 SmartHire Corporate Recruiter Portal.</p>
        </div>
    </footer>

</body>
</html>

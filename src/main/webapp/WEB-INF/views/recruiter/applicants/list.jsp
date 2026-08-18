<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SmartHire | Candidate Evaluation Roster</title>
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
            <a href="${pageContext.request.contextPath}/recruiter/dashboard" class="brand-logo">
                🎓 SmartHire <span class="brand-badge" style="background-color: #f3e8ff; color: #7e22ce; border-color: rgba(126, 34, 206, 0.3);">Recruiter Hub</span>
            </a>
            <ul class="nav-links">
                <li><a href="${pageContext.request.contextPath}/recruiter/dashboard" class="nav-link">Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/recruiter/jobs" class="nav-link">Our Job Postings</a></li>
                <li><a href="${pageContext.request.contextPath}/recruiter/applicants?jobId=${selectedJob.id}" class="nav-link" style="color: var(--primary); font-weight: 700;">Candidate Roster</a></li>
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
                        Candidate Review & Shortlisting Roster
                    </h1>
                    <p style="color: var(--text-secondary); font-size: 0.95rem;">
                        Drive: <strong>${selectedJob.jobTitle}</strong> (${selectedJob.packageLpa} LPA) • Organization: <strong>${company.companyName}</strong>
                    </p>
                </div>
                <div style="display: flex; gap: 0.75rem;">
                    <a href="${pageContext.request.contextPath}/recruiter/rounds?jobId=${selectedJob.id}" class="btn btn-primary">
                        🎯 Manage Interview Rounds (${rounds.size()}) &rarr;
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

        <c:choose>
            <c:when test="${not empty applicants}">
                <div class="table-card">
                    <table class="custom-table">
                        <thead>
                            <tr>
                                <th>Candidate Details</th>
                                <th>Department</th>
                                <th>Academic Marks</th>
                                <th>Resume PDF</th>
                                <th>Current Status</th>
                                <th>Status Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="app" items="${applicants}">
                                <tr>
                                    <td>
                                        <strong style="color: var(--text-primary); font-size: 1rem;">${app.studentFullName}</strong>
                                        <div style="font-size: 0.8rem; color: var(--text-secondary);">
                                            Roll: <code>${app.rollNumber}</code> | ✉️ ${app.studentEmail}
                                        </div>
                                    </td>
                                    <td>
                                        <span class="brand-badge">${app.studentDepartment}</span>
                                    </td>
                                    <td>
                                        <div>CGPA: <strong style="color: var(--primary); font-size: 1rem;">${app.studentCgpa}</strong></div>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty app.resumeFilePath}">
                                                <a href="${pageContext.request.contextPath}/resume/download?studentId=${app.studentId}" target="_blank" class="btn btn-outline" style="font-size: 0.75rem; padding: 0.3rem 0.6rem;">
                                                    👁️ View Resume
                                                </a>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="font-size: 0.8rem; color: var(--text-muted);">None</span>
                                            </c:otherwise>
                                        </c:choose>
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
                                        <div style="display: flex; align-items: center; gap: 0.5rem; flex-wrap: wrap;">
                                            <!-- Status Updater Form -->
                                            <form action="${pageContext.request.contextPath}/recruiter/applicants/status" method="POST" style="display: flex; gap: 0.3rem;">
                                                <input type="hidden" name="jobId" value="${selectedJob.id}">
                                                <input type="hidden" name="applicationId" value="${app.id}">
                                                <select name="status" style="font-size: 0.8rem; padding: 0.3rem 0.5rem; border: 1px solid var(--border-color); border-radius: var(--radius-sm);">
                                                    <option value="SHORTLISTED" ${app.currentStatus == 'SHORTLISTED' ? 'selected' : ''}>Shortlist</option>
                                                    <option value="IN_PROCESS" ${app.currentStatus == 'IN_PROCESS' ? 'selected' : ''}>In Process</option>
                                                    <option value="REJECTED" ${app.currentStatus == 'REJECTED' ? 'selected' : ''}>Reject</option>
                                                </select>
                                                <button type="submit" class="btn btn-outline" style="font-size: 0.75rem; padding: 0.3rem 0.6rem;">
                                                    Update
                                                </button>
                                            </form>

                                            <!-- Direct Offer Button -->
                                            <c:if test="${app.currentStatus != 'OFFERED' && app.currentStatus != 'ACCEPTED'}">
                                                <form action="${pageContext.request.contextPath}/recruiter/applicants/offer" method="POST" onsubmit="return confirm('Confirm release of official job offer to ${app.studentFullName}?');">
                                                    <input type="hidden" name="jobId" value="${selectedJob.id}">
                                                    <input type="hidden" name="applicationId" value="${app.id}">
                                                    <button type="submit" class="btn btn-primary" style="font-size: 0.75rem; padding: 0.35rem 0.75rem; background-color: #16a34a;">
                                                        🎉 Release Offer
                                                    </button>
                                                </form>
                                            </c:if>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <div class="card" style="padding: 3.5rem; text-align: center;">
                    <div style="font-size: 3rem; margin-bottom: 1rem;">👥</div>
                    <h2 style="font-size: 1.4rem; font-weight: 700; margin-bottom: 0.5rem;">No Candidate Submissions Yet</h2>
                    <p style="color: var(--text-secondary); max-width: 500px; margin: 0 auto;">
                        Eligible students are currently reviewing this placement drive. When candidates apply via the 1-Click workflow, their verified academic profiles and resumes will appear here.
                    </p>
                </div>
            </c:otherwise>
        </c:choose>
    </main>

    <!-- Footer -->
    <footer class="footer">
        <div class="container">
            <p>&copy; 2026 SmartHire Recruiter Evaluation Hub.</p>
        </div>
    </footer>

</body>
</html>

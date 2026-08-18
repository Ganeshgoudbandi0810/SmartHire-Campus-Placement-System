<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SmartHire | Recruiter Hub</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .dashboard-header {
            background-color: var(--bg-card);
            border-bottom: 1px solid var(--border-color);
            padding: 2rem 0;
            margin-bottom: 2rem;
        }
        .user-welcome-title {
            font-size: 1.75rem;
            font-weight: 800;
            margin-bottom: 0.25rem;
        }
        .stat-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
            gap: 1.5rem;
            margin-bottom: 2.5rem;
        }
        .stat-card {
            background-color: var(--bg-card);
            border: 1px solid var(--border-color);
            border-radius: var(--radius-lg);
            padding: 1.5rem;
            box-shadow: var(--shadow-sm);
        }
        .stat-value {
            font-size: 2rem;
            font-weight: 800;
            color: var(--primary);
            margin: 0.5rem 0;
        }
        .stat-label {
            font-size: 0.85rem;
            font-weight: 600;
            color: var(--text-secondary);
            text-transform: uppercase;
            letter-spacing: 0.05em;
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
                <li><a href="${pageContext.request.contextPath}/recruiter/dashboard" class="nav-link" style="color: var(--primary); font-weight: 700;">Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/recruiter/jobs" class="nav-link">Our Job Postings</a></li>
                <li><a href="${pageContext.request.contextPath}/auth/logout" class="btn btn-outline" style="font-size: 0.85rem;">Sign Out</a></li>
            </ul>
        </div>
    </nav>

    <!-- Header / Banner -->
    <section class="dashboard-header">
        <div class="container">
            <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem;">
                <div>
                    <h1 class="user-welcome-title">Corporate Recruiter Portal</h1>
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
    </section>

    <!-- Main Content -->
    <main class="container">
        <!-- Quick Stats Grid -->
        <div class="stat-grid">
            <div class="stat-card">
                <div class="stat-label">Active Job Drives</div>
                <div class="stat-value">${activeDrivesCount}</div>
                <p style="font-size: 0.85rem; color: var(--text-secondary);">Open for application</p>
            </div>
            <div class="stat-card">
                <div class="stat-label">Total Applicants</div>
                <div class="stat-value">${totalApplicants}</div>
                <p style="font-size: 0.85rem; color: var(--text-secondary);">Student submissions</p>
            </div>
            <div class="stat-card">
                <div class="stat-label">Partner Standing</div>
                <div class="stat-value" style="font-size: 1.35rem; color: var(--success); margin: 0.85rem 0;">APPROVED ✓</div>
                <p style="font-size: 0.85rem; color: var(--text-secondary);">Verified by Placement Cell</p>
            </div>
            <div class="stat-card">
                <div class="stat-label">Total Postings</div>
                <div class="stat-value">${jobs.size()}</div>
                <p style="font-size: 0.85rem; color: var(--text-secondary);">All drives created</p>
            </div>
        </div>

        <!-- Drive Management Quick Summary -->
        <div class="card" style="padding: 2rem;">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; border-bottom: 1px solid var(--border-color); padding-bottom: 0.75rem;">
                <div>
                    <h2 style="font-size: 1.35rem; font-weight: 700;">Our Placement Drives</h2>
                    <p style="color: var(--text-secondary); font-size: 0.9rem;">Manage compensation, cutoffs, and applicant lists</p>
                </div>
                <a href="${pageContext.request.contextPath}/recruiter/jobs" class="btn btn-outline" style="font-size: 0.85rem;">
                    View All Drives (${jobs.size()}) &rarr;
                </a>
            </div>

            <c:choose>
                <c:when test="${not empty jobs}">
                    <c:forEach var="j" items="${jobs}">
                        <div style="background-color: var(--bg-main); border: 1px solid var(--border-color); border-radius: var(--radius-md); padding: 1.25rem; margin-bottom: 1rem;">
                            <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 0.5rem;">
                                <div>
                                    <h3 style="font-size: 1.1rem; font-weight: 700; color: var(--text-primary); margin-bottom: 0.25rem;">
                                        ${j.jobTitle}
                                    </h3>
                                    <p style="font-size: 0.85rem; color: var(--text-secondary);">
                                        Package: <strong>${j.packageLpa} LPA</strong> | Location: <strong>${j.jobLocation}</strong> | Min CGPA: <strong>${j.minCgpa}</strong> | Backlogs: <strong>&le; ${j.maxBacklogsAllowed}</strong>
                                    </p>
                                </div>
                                <div style="display: flex; align-items: center; gap: 1rem;">
                                    <span class="brand-badge">${j.applicantCount} Candidates</span>
                                    <c:choose>
                                        <c:when test="${j.openForApplication}">
                                            <span class="status-badge ok">● OPEN</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="status-badge" style="background-color: var(--danger-light); color: var(--danger);">● ${j.status.displayName}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <p style="color: var(--text-secondary); text-align: center; padding: 2rem;">
                        No placement drives posted yet.
                    </p>
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

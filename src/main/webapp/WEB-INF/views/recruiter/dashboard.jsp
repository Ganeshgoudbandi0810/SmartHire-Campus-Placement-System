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
                <li><a href="${pageContext.request.contextPath}/recruiter/applicants" class="nav-link">Candidates</a></li>
                <li><a href="${pageContext.request.contextPath}/recruiter/rounds" class="nav-link">Interview Rounds</a></li>
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
                        Logged in as: <strong>${sessionScope.currentUser.email}</strong> 
                        <span class="status-badge ok" style="margin-left: 0.5rem;">TechCorp Solutions</span>
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
                <div class="stat-label">Active Drives</div>
                <div class="stat-value">1</div>
                <p style="font-size: 0.85rem; color: var(--text-secondary);">Live recruitment drive</p>
            </div>
            <div class="stat-card">
                <div class="stat-label">Total Applicants</div>
                <div class="stat-value">0</div>
                <p style="font-size: 0.85rem; color: var(--text-secondary);">Eligible student submissions</p>
            </div>
            <div class="stat-card">
                <div class="stat-label">In Interview Process</div>
                <div class="stat-value">0</div>
                <p style="font-size: 0.85rem; color: var(--text-secondary);">Currently in round evaluation</p>
            </div>
            <div class="stat-card">
                <div class="stat-label">Offers Released</div>
                <div class="stat-value" style="color: var(--success);">0</div>
                <p style="font-size: 0.85rem; color: var(--text-secondary);">Final offers issued</p>
            </div>
        </div>

        <!-- Drive Management Quick Summary -->
        <div class="card" style="padding: 2rem;">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;">
                <div>
                    <h2 style="font-size: 1.35rem; font-weight: 700;">Current Recruitment Drives</h2>
                    <p style="color: var(--text-secondary); font-size: 0.9rem;">Manage applicant shortlists and interview rounds</p>
                </div>
            </div>

            <div style="background-color: var(--bg-main); border: 1px solid var(--border-color); border-radius: var(--radius-md); padding: 1.25rem;">
                <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 0.5rem;">
                    <div>
                        <h3 style="font-size: 1.15rem; font-weight: 700; color: var(--text-primary); margin-bottom: 0.25rem;">
                            Associate Software Engineer
                        </h3>
                        <p style="font-size: 0.85rem; color: var(--text-secondary);">
                            Package: 9.50 LPA | Min CGPA: 7.50 | Cutoff: 0 Backlogs | Target Batch: 2026
                        </p>
                    </div>
                    <div>
                        <span class="status-badge ok">● STATUS: OPEN</span>
                    </div>
                </div>
            </div>
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

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SmartHire | Placement Officer (TPO) Portal</title>
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
            <a href="${pageContext.request.contextPath}/tpo/dashboard" class="brand-logo">
                🎓 SmartHire <span class="brand-badge" style="background-color: #fef3c7; color: #b45309; border-color: rgba(180, 83, 9, 0.3);">TPO Admin</span>
            </a>
            <ul class="nav-links">
                <li><a href="${pageContext.request.contextPath}/tpo/dashboard" class="nav-link" style="color: var(--primary); font-weight: 700;">Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/tpo/students" class="nav-link">Students</a></li>
                <li><a href="${pageContext.request.contextPath}/tpo/companies" class="nav-link">Companies</a></li>
                <li><a href="${pageContext.request.contextPath}/tpo/drives" class="nav-link">Placement Drives</a></li>
                <li><a href="${pageContext.request.contextPath}/tpo/reports" class="nav-link">Analytics</a></li>
                <li><a href="${pageContext.request.contextPath}/auth/logout" class="btn btn-outline" style="font-size: 0.85rem;">Sign Out</a></li>
            </ul>
        </div>
    </nav>

    <!-- Header / Banner -->
    <section class="dashboard-header">
        <div class="container">
            <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem;">
                <div>
                    <h1 class="user-welcome-title">Training & Placement Officer Workspace</h1>
                    <p style="color: var(--text-secondary); font-size: 0.95rem;">
                        Logged in as: <strong>${sessionScope.currentUser.email}</strong> 
                        <span class="status-badge ok" style="margin-left: 0.5rem;">ADMINISTRATOR</span>
                    </p>
                </div>
                <div style="display: flex; gap: 0.75rem;">
                    <a href="${pageContext.request.contextPath}/db-test" class="btn btn-outline">
                        🗄️ DB Diagnostics
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
                <div class="stat-label">Registered Students</div>
                <div class="stat-value">1</div>
                <p style="font-size: 0.85rem; color: var(--text-secondary);">Batch of 2026</p>
            </div>
            <div class="stat-card">
                <div class="stat-label">Partner Companies</div>
                <div class="stat-value">1</div>
                <p style="font-size: 0.85rem; color: var(--text-secondary);">Verified recruiters</p>
            </div>
            <div class="stat-card">
                <div class="stat-label">Active Drives</div>
                <div class="stat-value">1</div>
                <p style="font-size: 0.85rem; color: var(--text-secondary);">Open for application</p>
            </div>
            <div class="stat-card">
                <div class="stat-label">Placement Percentage</div>
                <div class="stat-value" style="color: var(--success);">0.0 %</div>
                <p style="font-size: 0.85rem; color: var(--text-secondary);">Recruitment season live</p>
            </div>
        </div>

        <!-- Management Modules Quick Access -->
        <div class="card-grid">
            <div class="card">
                <div class="card-icon">👨‍🎓</div>
                <h3 class="card-title">Student Verification</h3>
                <p class="card-text">Audit academic marks, verify CGPA against official transcripts, and approve resumes.</p>
                <a href="${pageContext.request.contextPath}/tpo/students" class="btn btn-outline">Review Students &rarr;</a>
            </div>

            <div class="card">
                <div class="card-icon">🏢</div>
                <h3 class="card-title">Company Onboarding</h3>
                <p class="card-text">Approve new company registrations, verify recruiter credentials, and manage relationships.</p>
                <a href="${pageContext.request.contextPath}/tpo/companies" class="btn btn-outline">Manage Companies &rarr;</a>
            </div>

            <div class="card">
                <div class="card-icon">📊</div>
                <h3 class="card-title">Placement Reports</h3>
                <p class="card-text">Generate branch-wise placement statistics, package histograms, and export CSV/PDF reports.</p>
                <a href="${pageContext.request.contextPath}/tpo/reports" class="btn btn-outline">View Reports &rarr;</a>
            </div>
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

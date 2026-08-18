<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SmartHire | Student Dashboard</title>
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
            <a href="${pageContext.request.contextPath}/student/dashboard" class="brand-logo">
                🎓 SmartHire <span class="brand-badge">Student Portal</span>
            </a>
            <ul class="nav-links">
                <li><a href="${pageContext.request.contextPath}/student/dashboard" class="nav-link" style="color: var(--primary); font-weight: 700;">Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/student/profile" class="nav-link">My Profile</a></li>
                <li><a href="${pageContext.request.contextPath}/student/drives" class="nav-link">Placement Drives</a></li>
                <li><a href="${pageContext.request.contextPath}/student/applications" class="nav-link">My Applications</a></li>
                <li><a href="${pageContext.request.contextPath}/auth/logout" class="btn btn-outline" style="font-size: 0.85rem;">Sign Out</a></li>
            </ul>
        </div>
    </nav>

    <!-- Header / Banner -->
    <section class="dashboard-header">
        <div class="container">
            <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem;">
                <div>
                    <h1 class="user-welcome-title">Welcome back, Student!</h1>
                    <p style="color: var(--text-secondary); font-size: 0.95rem;">
                        Logged in as: <strong>${sessionScope.currentUser.email}</strong> 
                        <span class="status-badge ok" style="margin-left: 0.5rem;">ACTIVE ACCOUNT</span>
                    </p>
                </div>
                <div>
                    <a href="${pageContext.request.contextPath}/student/profile" class="btn btn-primary">
                        ✏️ Edit Academic Profile
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
                <p style="font-size: 0.85rem; color: var(--text-secondary);">Live companies hiring</p>
            </div>
            <div class="stat-card">
                <div class="stat-label">Applied Jobs</div>
                <div class="stat-value">0</div>
                <p style="font-size: 0.85rem; color: var(--text-secondary);">Applications in progress</p>
            </div>
            <div class="stat-card">
                <div class="stat-label">Interviews Scheduled</div>
                <div class="stat-value">0</div>
                <p style="font-size: 0.85rem; color: var(--text-secondary);">Upcoming interview rounds</p>
            </div>
            <div class="stat-card">
                <div class="stat-label">Profile Verification</div>
                <div class="stat-value" style="font-size: 1.35rem; color: var(--success); margin: 0.85rem 0;">VERIFIED ✓</div>
                <p style="font-size: 0.85rem; color: var(--text-secondary);">Approved by Placement Cell</p>
            </div>
        </div>

        <!-- Section: Available Drives Spotlight -->
        <div class="card" style="padding: 2rem; margin-bottom: 2.5rem;">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;">
                <div>
                    <h2 style="font-size: 1.35rem; font-weight: 700;">Featured Placement Drive</h2>
                    <p style="color: var(--text-secondary); font-size: 0.9rem;">Check eligibility and apply in Stage 3–5</p>
                </div>
                <span class="brand-badge">TechCorp Solutions</span>
            </div>

            <div style="background-color: var(--bg-main); border: 1px solid var(--border-color); border-radius: var(--radius-md); padding: 1.25rem;">
                <h3 style="font-size: 1.15rem; font-weight: 700; color: var(--text-primary); margin-bottom: 0.5rem;">
                    Associate Software Engineer (Java / Backend)
                </h3>
                <p style="color: var(--text-secondary); font-size: 0.95rem; margin-bottom: 1rem;">
                    Package: <strong>9.50 LPA</strong> | Location: <strong>Bangalore / Hybrid</strong> | Eligible Branches: <strong>CSE, IT, ECE</strong>
                </p>
                <div style="display: flex; gap: 0.75rem; flex-wrap: wrap;">
                    <span class="brand-badge">Min CGPA: 7.50</span>
                    <span class="brand-badge">Max Backlogs: 0</span>
                    <span class="brand-badge">Batch: 2026</span>
                </div>
            </div>
        </div>
    </main>

    <!-- Footer -->
    <footer class="footer">
        <div class="container">
            <p>&copy; 2026 SmartHire Student Portal.</p>
        </div>
    </footer>

</body>
</html>

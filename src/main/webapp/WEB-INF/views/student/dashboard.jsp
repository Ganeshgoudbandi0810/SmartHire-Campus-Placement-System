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
        .profile-summary-box {
            background-color: var(--bg-card);
            border: 1px solid var(--border-color);
            border-radius: var(--radius-lg);
            padding: 2rem;
            margin-bottom: 2.5rem;
            box-shadow: var(--shadow-sm);
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
                    <h1 class="user-welcome-title">
                        Welcome back, ${not empty profile.firstName ? profile.fullName : 'Candidate'}!
                    </h1>
                    <p style="color: var(--text-secondary); font-size: 0.95rem;">
                        Logged in as: <strong>${sessionScope.currentUser.email}</strong> 
                        <span class="status-badge ok" style="margin-left: 0.5rem;">ACTIVE CANDIDATE</span>
                    </p>
                </div>
                <div style="display: flex; gap: 0.75rem;">
                    <a href="${pageContext.request.contextPath}/student/drives" class="btn btn-primary">
                        🔍 Browse Live Drives (${activeDrives})
                    </a>
                </div>
            </div>
        </div>
    </section>

    <!-- Main Content -->
    <main class="container">

        <!-- Quick Profile Warning if incomplete -->
        <c:if test="${profile.completionPercentage < 80}">
            <div style="background-color: var(--warning-light); border: 1px solid var(--warning); border-radius: var(--radius-md); padding: 1.25rem; margin-bottom: 2rem; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem;">
                <div>
                    <h3 style="color: var(--warning); font-size: 1.05rem; margin-bottom: 0.25rem;">⚠️ Profile Incomplete (${profile.completionPercentage}%)</h3>
                    <p style="color: var(--text-primary); font-size: 0.9rem;">
                        Please complete your academic records (CGPA, marks, department) and upload your resume to qualify for campus placement drives.
                    </p>
                </div>
                <a href="${pageContext.request.contextPath}/student/profile" class="btn btn-primary" style="font-size: 0.85rem;">
                    Complete Profile &rarr;
                </a>
            </div>
        </c:if>

        <!-- Quick Stats Grid -->
        <div class="stat-grid">
            <div class="stat-card">
                <div class="stat-label">Active Campus Drives</div>
                <div class="stat-value">${activeDrives}</div>
                <p style="font-size: 0.85rem; color: var(--text-secondary);"><a href="${pageContext.request.contextPath}/student/drives" style="color: var(--primary); text-decoration: none;">View Open Drives &rarr;</a></p>
            </div>
            <div class="stat-card">
                <div class="stat-label">Applied Jobs</div>
                <div class="stat-value">${appliedCount}</div>
                <p style="font-size: 0.85rem; color: var(--text-secondary);"><a href="${pageContext.request.contextPath}/student/applications" style="color: var(--primary); text-decoration: none;">Track Applications &rarr;</a></p>
            </div>
            <div class="stat-card">
                <div class="stat-label">Current CGPA</div>
                <div class="stat-value">${profile.cgpa}</div>
                <p style="font-size: 0.85rem; color: var(--text-secondary);">Department: <strong>${profile.department}</strong></p>
            </div>
            <div class="stat-card">
                <div class="stat-label">Verification Status</div>
                <div class="stat-value" style="font-size: 1.35rem; color: ${profile.verified ? 'var(--success)' : 'var(--warning)'}; margin: 0.85rem 0;">
                    ${profile.verified ? 'VERIFIED ✓' : 'PENDING ⏳'}
                </div>
                <p style="font-size: 0.85rem; color: var(--text-secondary);">
                    ${profile.verified ? 'Approved by Placement Cell' : 'Awaiting TPO Review'}
                </p>
            </div>
        </div>

        <!-- Section: Academic Profile Snapshot -->
        <div class="profile-summary-box">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; border-bottom: 1px solid var(--border-color); padding-bottom: 0.75rem;">
                <h2 style="font-size: 1.25rem; font-weight: 700;">Candidate Academic Summary</h2>
                <a href="${pageContext.request.contextPath}/student/profile" class="btn btn-outline" style="font-size: 0.85rem;">
                    Update Details &rarr;
                </a>
            </div>

            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1.5rem; margin-bottom: 1.5rem;">
                <div>
                    <span style="font-size: 0.8rem; color: var(--text-secondary); text-transform: uppercase;">Roll Number</span>
                    <p style="font-weight: 700; font-size: 1.05rem;">${profile.rollNumber}</p>
                </div>
                <div>
                    <span style="font-size: 0.8rem; color: var(--text-secondary); text-transform: uppercase;">10th Percentage</span>
                    <p style="font-weight: 700; font-size: 1.05rem;">${profile.tenthPercentage}%</p>
                </div>
                <div>
                    <span style="font-size: 0.8rem; color: var(--text-secondary); text-transform: uppercase;">12th / Diploma</span>
                    <p style="font-weight: 700; font-size: 1.05rem;">${profile.twelfthPercentage}%</p>
                </div>
                <div>
                    <span style="font-size: 0.8rem; color: var(--text-secondary); text-transform: uppercase;">Resume Document</span>
                    <p style="font-weight: 700; font-size: 0.95rem;">
                        <c:choose>
                            <c:when test="${not empty profile.resumeFilePath}">
                                <a href="${pageContext.request.contextPath}/student/resume/download" target="_blank" style="color: var(--primary); text-decoration: none;">
                                    📎 View Uploaded PDF
                                </a>
                            </c:when>
                            <c:otherwise>
                                <span style="color: var(--danger);">No Resume Attached</span>
                            </c:otherwise>
                        </c:choose>
                    </p>
                </div>
            </div>

            <c:if test="${not empty profile.skills}">
                <div style="background-color: var(--bg-main); border: 1px solid var(--border-color); border-radius: var(--radius-md); padding: 1rem;">
                    <span style="font-size: 0.8rem; color: var(--text-secondary); text-transform: uppercase; font-weight: 700; display: block; margin-bottom: 0.35rem;">
                        Technical Skills
                    </span>
                    <p style="font-size: 0.95rem; color: var(--text-primary);">${profile.skills}</p>
                </div>
            </c:if>
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

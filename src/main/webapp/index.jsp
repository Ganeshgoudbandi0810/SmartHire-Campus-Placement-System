<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SmartHire | Campus Placement & Recruitment System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

    <!-- Top Navigation Bar -->
    <nav class="navbar">
        <div class="container nav-container">
            <a href="${pageContext.request.contextPath}/" class="brand-logo">
                🎓 SmartHire <span class="brand-badge">v1.0</span>
            </a>
            <ul class="nav-links">
                <li><a href="${pageContext.request.contextPath}/" class="nav-link">Home</a></li>
                <li><a href="${pageContext.request.contextPath}/health" class="nav-link">System Health</a></li>
                <li><a href="${pageContext.request.contextPath}/auth/login" class="btn btn-outline">Log In</a></li>
                <li><a href="${pageContext.request.contextPath}/auth/register" class="btn btn-primary">Sign Up</a></li>
            </ul>
        </div>
    </nav>

    <!-- Hero Section -->
    <header class="hero">
        <div class="container">
            <div class="hero-badge">Next-Gen Campus Placement Management</div>
            <h1 class="hero-title">Connecting Talent with <span>Opportunity</span></h1>
            <p class="hero-desc">
                An intelligent, automated campus recruitment platform designed for Students, Placement Officers, and Corporate Recruiters.
            </p>
            <div class="hero-actions">
                <a href="${pageContext.request.contextPath}/health" class="btn btn-primary">
                    🩺 Check Runtime Health
                </a>
                <a href="#roles" class="btn btn-outline">
                    Explore Roles & Modules
                </a>
            </div>
        </div>
    </header>

    <!-- Role Portals / Features Section -->
    <section id="roles" class="features-section">
        <div class="container">
            <div class="section-header">
                <h2 class="section-title">Built for Everyone in the Placement Lifecycle</h2>
                <p class="section-subtitle">Dedicated portals and automated workflows for each stakeholder</p>
            </div>

            <div class="card-grid">
                <!-- Student Card -->
                <div class="card">
                    <div class="card-icon">👨‍🎓</div>
                    <h3 class="card-title">Student Portal</h3>
                    <p class="card-text">
                        Maintain academic profile, upload verified resumes, check real-time eligibility for campus drives, and track interview round progression.
                    </p>
                    <a href="${pageContext.request.contextPath}/auth/login?role=STUDENT" class="btn btn-outline">Student Access &rarr;</a>
                </div>

                <!-- TPO Admin Card -->
                <div class="card">
                    <div class="card-icon">🏛️</div>
                    <h3 class="card-title">Placement Officer (TPO)</h3>
                    <p class="card-text">
                        Verify student academic records, approve company drives, manage placement policies, and generate comprehensive department-wise statistics.
                    </p>
                    <a href="${pageContext.request.contextPath}/auth/login?role=TPO_ADMIN" class="btn btn-outline">TPO Portal &rarr;</a>
                </div>

                <!-- Recruiter Card -->
                <div class="card">
                    <div class="card-icon">💼</div>
                    <h3 class="card-title">Corporate Recruiter</h3>
                    <p class="card-text">
                        Post job criteria and packages, automatically filter eligible candidates, schedule interview rounds, and submit evaluation feedback.
                    </p>
                    <a href="${pageContext.request.contextPath}/auth/login?role=RECRUITER" class="btn btn-outline">Recruiter Hub &rarr;</a>
                </div>
            </div>
        </div>
    </section>

    <!-- Footer -->
    <footer class="footer">
        <div class="container">
            <p>&copy; 2026 SmartHire Campus Placement System. Built with pure Java, Servlets, JSP & MySQL.</p>
        </div>
    </footer>

    <script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>

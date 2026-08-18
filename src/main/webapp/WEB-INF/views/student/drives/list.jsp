<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SmartHire | Live Campus Placement Drives</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .page-header {
            background-color: var(--bg-card);
            border-bottom: 1px solid var(--border-color);
            padding: 2rem 0;
            margin-bottom: 2rem;
        }
        .drive-card {
            background-color: var(--bg-card);
            border: 1px solid var(--border-color);
            border-radius: var(--radius-lg);
            padding: 2rem;
            margin-bottom: 1.75rem;
            box-shadow: var(--shadow-sm);
            transition: all 0.2s ease;
        }
        .drive-card:hover {
            border-color: #cbd5e1;
            box-shadow: var(--shadow-md);
        }
        .drive-header {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            flex-wrap: wrap;
            gap: 1rem;
            margin-bottom: 1.25rem;
            border-bottom: 1px solid var(--border-color);
            padding-bottom: 1rem;
        }
        .package-badge {
            font-size: 1.5rem;
            font-weight: 800;
            color: var(--primary);
        }
        .criteria-box {
            background-color: var(--bg-main);
            border: 1px solid var(--border-color);
            border-radius: var(--radius-md);
            padding: 1rem 1.25rem;
            margin: 1.25rem 0;
        }
        .criteria-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
            gap: 0.75rem;
            font-size: 0.85rem;
        }
        .eligibility-pill {
            display: inline-flex;
            align-items: center;
            gap: 0.4rem;
            padding: 0.4rem 0.9rem;
            border-radius: var(--radius-full);
            font-weight: 700;
            font-size: 0.85rem;
        }
        .eligibility-pill.eligible {
            background-color: var(--success-light);
            color: var(--success);
            border: 1px solid rgba(22, 163, 74, 0.4);
        }
        .eligibility-pill.ineligible {
            background-color: var(--danger-light);
            color: var(--danger);
            border: 1px solid rgba(220, 38, 38, 0.4);
        }
        .eligibility-pill.applied {
            background-color: var(--primary-light);
            color: var(--primary);
            border: 1px solid rgba(37, 99, 235, 0.4);
        }
        .alert-danger {
            background-color: var(--danger-light);
            color: var(--danger);
            border: 1px solid rgba(220, 38, 38, 0.3);
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
            <a href="${pageContext.request.contextPath}/student/dashboard" class="brand-logo">
                🎓 SmartHire <span class="brand-badge">Student Portal</span>
            </a>
            <ul class="nav-links">
                <li><a href="${pageContext.request.contextPath}/student/dashboard" class="nav-link">Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/student/profile" class="nav-link">My Profile</a></li>
                <li><a href="${pageContext.request.contextPath}/student/drives" class="nav-link" style="color: var(--primary); font-weight: 700;">Placement Drives</a></li>
                <li><a href="${pageContext.request.contextPath}/student/applications" class="nav-link">My Applications</a></li>
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
                        Campus Placement Drives
                    </h1>
                    <p style="color: var(--text-secondary); font-size: 0.95rem;">
                        Candidate: <strong>${student.fullName}</strong> | Dept: <strong>${student.department}</strong> | CGPA: <strong>${student.cgpa}</strong>
                    </p>
                </div>
                <div>
                    <a href="${pageContext.request.contextPath}/student/applications" class="btn btn-outline">
                        📋 View My Applications &rarr;
                    </a>
                </div>
            </div>
        </div>
    </header>

    <!-- Main Content -->
    <main class="container" style="margin-bottom: 4rem;">

        <!-- Error Messages -->
        <c:if test="${not empty errorMessage}">
            <div class="alert-danger">
                ⚠️ ${errorMessage}
            </div>
        </c:if>

        <c:choose>
            <c:when test="${not empty driveDTOs}">
                <c:forEach var="item" items="${driveDTOs}">
                    <c:set var="job" value="${item.job}" />
                    <c:set var="eligibility" value="${item.eligibility}" />

                    <div class="drive-card">
                        <!-- Top Header -->
                        <div class="drive-header">
                            <div>
                                <h2 style="font-size: 1.4rem; font-weight: 800; color: var(--text-primary); margin-bottom: 0.25rem;">
                                    ${job.jobTitle}
                                </h2>
                                <p style="color: var(--text-secondary); font-size: 0.95rem;">
                                    🏢 <strong>${job.companyName}</strong> • 📍 ${job.jobLocation} • <em>${job.employmentType.displayName}</em>
                                </p>
                            </div>
                            <div style="text-align: right;">
                                <div class="package-badge">${job.packageLpa} LPA</div>
                                <span style="font-size: 0.8rem; color: var(--text-secondary);">Annual CTC</span>
                            </div>
                        </div>

                        <!-- Job Description -->
                        <p style="font-size: 0.95rem; color: var(--text-primary); line-height: 1.5; margin-bottom: 1rem;">
                            ${job.jobDescription}
                        </p>

                        <!-- Eligibility Cutoff Box -->
                        <div class="criteria-box">
                            <div style="font-size: 0.8rem; font-weight: 700; color: var(--text-secondary); text-transform: uppercase; margin-bottom: 0.5rem;">
                                🎯 Company Eligibility Requirements:
                            </div>
                            <div class="criteria-grid">
                                <div>Min CGPA: <strong>${job.minCgpa}</strong></div>
                                <div>Max Backlogs: <strong>&le; ${job.maxBacklogsAllowed}</strong></div>
                                <div>Eligible Branches: <code>${job.eligibleBranches}</code></div>
                                <div>10th / 12th Cutoffs: <strong>${job.minTenthPercentage}% / ${job.minTwelfthPercentage}%</strong></div>
                                <div>Graduation Batch: <strong>${job.graduationYear}</strong></div>
                                <div>Deadline: <strong>${job.applicationDeadline}</strong></div>
                            </div>
                        </div>

                        <!-- Eligibility Decision & Application Action Bar -->
                        <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem; margin-top: 1.25rem;">
                            <div>
                                <c:choose>
                                    <c:when test="${item.applied}">
                                        <span class="eligibility-pill applied">
                                            ✓ ALREADY APPLIED
                                        </span>
                                    </c:when>
                                    <c:when test="${eligibility.eligible}">
                                        <span class="eligibility-pill eligible">
                                            ● ELIGIBLE TO APPLY ✓
                                        </span>
                                        <span style="font-size: 0.85rem; color: var(--success); margin-left: 0.5rem;">
                                            Your profile meets all cutoff criteria!
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="eligibility-pill ineligible">
                                            ✗ INELIGIBLE
                                        </span>
                                        <span style="font-size: 0.85rem; color: var(--danger); margin-left: 0.5rem;">
                                            ${eligibility.primaryReason}
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <div>
                                <c:choose>
                                    <c:when test="${item.applied}">
                                        <a href="${pageContext.request.contextPath}/student/applications" class="btn btn-outline" style="font-size: 0.85rem;">
                                            Track Selection Status &rarr;
                                        </a>
                                    </c:when>
                                    <c:when test="${item.eligibleToApply}">
                                        <form action="${pageContext.request.contextPath}/student/drives/apply" method="POST" onsubmit="return confirm('Confirm 1-Click application submission for ${job.jobTitle} at ${job.companyName}?');">
                                            <input type="hidden" name="jobId" value="${job.id}">
                                            <button type="submit" class="btn btn-primary" style="padding: 0.65rem 1.5rem; font-size: 0.95rem;">
                                                🚀 Apply for Drive (1-Click)
                                            </button>
                                        </form>
                                    </c:when>
                                    <c:otherwise>
                                        <button type="button" class="btn btn-outline" style="cursor: not-allowed; opacity: 0.6;" disabled>
                                            Application Locked
                                        </button>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>

                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <div class="card" style="padding: 3.5rem; text-align: center;">
                    <div style="font-size: 3rem; margin-bottom: 1rem;">🏢</div>
                    <h2 style="font-size: 1.4rem; font-weight: 700; margin-bottom: 0.5rem;">No Active Placement Drives</h2>
                    <p style="color: var(--text-secondary); max-width: 500px; margin: 0 auto;">
                        There are currently no open recruitment drives available. Check back soon as new campus drives are scheduled by partner recruiters!
                    </p>
                </div>
            </c:otherwise>
        </c:choose>

    </main>

    <!-- Footer -->
    <footer class="footer">
        <div class="container">
            <p>&copy; 2026 SmartHire Automated Eligibility Engine.</p>
        </div>
    </footer>

</body>
</html>

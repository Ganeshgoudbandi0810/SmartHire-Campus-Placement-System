<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SmartHire | Post New Campus Placement Drive</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .page-header {
            background-color: var(--bg-card);
            border-bottom: 1px solid var(--border-color);
            padding: 2rem 0;
            margin-bottom: 2rem;
        }
        .form-card {
            background-color: var(--bg-card);
            border: 1px solid var(--border-color);
            border-radius: var(--radius-lg);
            padding: 2.5rem 2rem;
            margin-bottom: 2rem;
            box-shadow: var(--shadow-sm);
        }
        .form-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
            gap: 1.25rem;
            margin-bottom: 1.5rem;
        }
        .form-group {
            margin-bottom: 1.25rem;
        }
        .form-label {
            display: block;
            font-size: 0.85rem;
            font-weight: 600;
            margin-bottom: 0.35rem;
            color: var(--text-primary);
        }
        .form-control {
            width: 100%;
            padding: 0.7rem 0.9rem;
            border: 1px solid var(--border-color);
            border-radius: var(--radius-md);
            font-size: 0.95rem;
            font-family: inherit;
            transition: all 0.2s;
        }
        .form-control:focus {
            outline: none;
            border-color: var(--primary);
            box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.15);
        }
        .branch-checkbox-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(130px, 1fr));
            gap: 0.75rem;
            margin-top: 0.5rem;
        }
        .branch-item {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            background-color: var(--bg-main);
            border: 1px solid var(--border-color);
            border-radius: var(--radius-sm);
            padding: 0.5rem 0.75rem;
            font-size: 0.85rem;
            cursor: pointer;
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
            <a href="${pageContext.request.contextPath}/recruiter/dashboard" class="brand-logo">
                🎓 SmartHire <span class="brand-badge" style="background-color: #f3e8ff; color: #7e22ce; border-color: rgba(126, 34, 206, 0.3);">Recruiter Hub</span>
            </a>
            <ul class="nav-links">
                <li><a href="${pageContext.request.contextPath}/recruiter/dashboard" class="nav-link">Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/recruiter/jobs" class="nav-link">Our Job Postings</a></li>
                <li><a href="${pageContext.request.contextPath}/auth/logout" class="btn btn-outline" style="font-size: 0.85rem;">Sign Out</a></li>
            </ul>
        </div>
    </nav>

    <!-- Header / Banner -->
    <header class="page-header">
        <div class="container">
            <h1 style="font-size: 1.75rem; font-weight: 800; margin-bottom: 0.25rem;">
                Post New Campus Placement Drive
            </h1>
            <p style="color: var(--text-secondary); font-size: 0.95rem;">
                Configure compensation details, candidate cutoffs, and selection drive schedule
            </p>
        </div>
    </header>

    <!-- Main Content -->
    <main class="container" style="max-width: 900px; margin-bottom: 4rem;">

        <!-- Error Messages -->
        <c:if test="${not empty errorMessage}">
            <div class="alert-danger">
                ⚠️ ${errorMessage}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/recruiter/jobs/create" method="POST">
            
            <!-- Section 1: Role Overview -->
            <div class="form-card">
                <h2 style="font-size: 1.2rem; font-weight: 700; margin-bottom: 1.25rem; border-bottom: 1px solid var(--border-color); padding-bottom: 0.5rem;">
                    1. Role & Compensation Details
                </h2>

                <div class="form-grid">
                    <div class="form-group" style="grid-column: span 2;">
                        <label class="form-label" for="jobTitle">Job Title / Role Name</label>
                        <input type="text" id="jobTitle" name="jobTitle" class="form-control" 
                               placeholder="e.g. Associate Software Engineer (Backend)" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="packageLpa">Annual CTC Package (LPA in ₹)</label>
                        <input type="number" step="0.01" min="1.00" max="100.00" id="packageLpa" name="packageLpa" class="form-control" 
                               placeholder="e.g. 10.50" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="employmentType">Employment Type</label>
                        <select id="employmentType" name="employmentType" class="form-control">
                            <option value="FULL_TIME">Full Time (FTE)</option>
                            <option value="INTERNSHIP">Internship Only</option>
                            <option value="INTERN_TO_FTE">Internship to FTE Conversion</option>
                        </select>
                    </div>

                    <div class="form-group" style="grid-column: span 2;">
                        <label class="form-label" for="jobLocation">Primary Work Location</label>
                        <input type="text" id="jobLocation" name="jobLocation" class="form-control" 
                               placeholder="e.g. Bangalore / Hyderabad / Hybrid" required>
                    </div>

                    <div class="form-group" style="grid-column: span 2;">
                        <label class="form-label" for="jobDescription">Role Description & Responsibilities</label>
                        <textarea id="jobDescription" name="jobDescription" rows="4" class="form-control" 
                                  placeholder="Describe the technical requirements, stack, and interview process..." required></textarea>
                    </div>
                </div>
            </div>

            <!-- Section 2: Automated Eligibility Cutoffs -->
            <div class="form-card">
                <h2 style="font-size: 1.2rem; font-weight: 700; margin-bottom: 1.25rem; border-bottom: 1px solid var(--border-color); padding-bottom: 0.5rem;">
                    2. Strict Eligibility Cutoffs (Enforced by Automated Eligibility Engine)
                </h2>

                <div class="form-grid">
                    <div class="form-group">
                        <label class="form-label" for="minCgpa">Minimum CGPA Cutoff (0.0 - 10.0)</label>
                        <input type="number" step="0.01" min="0.00" max="10.00" id="minCgpa" name="minCgpa" class="form-control" 
                               value="7.00" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="maxBacklogsAllowed">Maximum Allowed Active Backlogs</label>
                        <input type="number" min="0" max="10" id="maxBacklogsAllowed" name="maxBacklogsAllowed" class="form-control" 
                               value="0" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="minTenthPercentage">Min 10th Percentage (%)</label>
                        <input type="number" step="0.01" min="0.00" max="100.00" id="minTenthPercentage" name="minTenthPercentage" class="form-control" 
                               value="65.00" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="minTwelfthPercentage">Min 12th / Diploma (%)</label>
                        <input type="number" step="0.01" min="0.00" max="100.00" id="minTwelfthPercentage" name="minTwelfthPercentage" class="form-control" 
                               value="65.00" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="graduationYear">Eligible Graduation Batch Year</label>
                        <input type="number" min="2020" max="2035" id="graduationYear" name="graduationYear" class="form-control" 
                               value="2026" required>
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label">Eligible Academic Departments / Branches</label>
                    <div class="branch-checkbox-grid">
                        <label class="branch-item"><input type="checkbox" name="branches" value="CSE" checked> Computer Science (CSE)</label>
                        <label class="branch-item"><input type="checkbox" name="branches" value="IT" checked> Info Tech (IT)</label>
                        <label class="branch-item"><input type="checkbox" name="branches" value="ECE" checked> Electronics (ECE)</label>
                        <label class="branch-item"><input type="checkbox" name="branches" value="EEE"> Electrical (EEE)</label>
                        <label class="branch-item"><input type="checkbox" name="branches" value="MECH"> Mechanical (MECH)</label>
                        <label class="branch-item"><input type="checkbox" name="branches" value="CIVIL"> Civil (CIVIL)</label>
                    </div>
                </div>
            </div>

            <!-- Section 3: Drive Schedule & Deadlines -->
            <div class="form-card">
                <h2 style="font-size: 1.2rem; font-weight: 700; margin-bottom: 1.25rem; border-bottom: 1px solid var(--border-color); padding-bottom: 0.5rem;">
                    3. Drive Schedule & Application Window
                </h2>

                <div class="form-grid">
                    <div class="form-group">
                        <label class="form-label" for="applicationDeadline">Application Deadline</label>
                        <input type="datetime-local" id="applicationDeadline" name="applicationDeadline" class="form-control" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="driveDate">Interview / Assessment Date</label>
                        <input type="datetime-local" id="driveDate" name="driveDate" class="form-control" required>
                    </div>
                </div>
            </div>

            <div style="display: flex; justify-content: flex-end; gap: 1rem;">
                <a href="${pageContext.request.contextPath}/recruiter/jobs" class="btn btn-outline">Cancel</a>
                <button type="submit" class="btn btn-primary" style="padding: 0.75rem 2rem; font-size: 1rem;">
                    🚀 Publish Placement Drive
                </button>
            </div>

        </form>
    </main>

    <!-- Footer -->
    <footer class="footer">
        <div class="container">
            <p>&copy; 2026 SmartHire Corporate Recruiter Portal.</p>
        </div>
    </footer>

    <script>
        // Set default application deadline to 14 days from today
        const now = new Date();
        now.setDate(now.getDate() + 14);
        const deadlineIso = now.toISOString().slice(0, 16);
        document.getElementById('applicationDeadline').value = deadlineIso;

        // Set drive date to 21 days from today
        now.setDate(now.getDate() + 7);
        const driveIso = now.toISOString().slice(0, 16);
        document.getElementById('driveDate').value = driveIso;
    </script>
</body>
</html>

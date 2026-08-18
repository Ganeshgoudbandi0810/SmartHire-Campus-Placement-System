<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SmartHire | Manage Selection Rounds</title>
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
            padding: 2rem;
            margin-bottom: 2rem;
            box-shadow: var(--shadow-sm);
        }
        .round-card {
            background-color: var(--bg-card);
            border: 1px solid var(--border-color);
            border-radius: var(--radius-lg);
            padding: 1.75rem;
            margin-bottom: 1.5rem;
            box-shadow: var(--shadow-sm);
        }
        .custom-table {
            width: 100%;
            border-collapse: collapse;
            font-size: 0.9rem;
            margin-top: 1rem;
        }
        .custom-table th, .custom-table td {
            padding: 0.75rem 1rem;
            text-align: left;
            border-bottom: 1px solid var(--border-color);
        }
        .custom-table th {
            background-color: var(--bg-main);
            color: var(--text-secondary);
            font-weight: 600;
            font-size: 0.8rem;
            text-transform: uppercase;
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
                <li><a href="${pageContext.request.contextPath}/recruiter/applicants?jobId=${job.id}" class="nav-link">Candidate Roster</a></li>
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
                        Multi-Stage Interview & Selection Rounds
                    </h1>
                    <p style="color: var(--text-secondary); font-size: 0.95rem;">
                        Drive: <strong>${job.jobTitle}</strong> (${job.packageLpa} LPA)
                    </p>
                </div>
                <div>
                    <a href="${pageContext.request.contextPath}/recruiter/applicants?jobId=${job.id}" class="btn btn-outline">
                        &larr; Back to Candidate Roster
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

        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap: 2rem;">
            
            <!-- Column 1: Schedule New Selection Round Form -->
            <div>
                <div class="form-card">
                    <h2 style="font-size: 1.25rem; font-weight: 700; margin-bottom: 1.25rem; border-bottom: 1px solid var(--border-color); padding-bottom: 0.5rem;">
                        ➕ Schedule Selection Round
                    </h2>

                    <form action="${pageContext.request.contextPath}/recruiter/rounds/create" method="POST">
                        <input type="hidden" name="jobId" value="${job.id}">

                        <div class="form-group" style="margin-bottom: 1rem;">
                            <label class="form-label" style="display: block; font-size: 0.85rem; font-weight: 600; margin-bottom: 0.35rem;">Round Sequence Number</label>
                            <input type="number" name="roundNumber" value="${rounds.size() + 1}" min="1" max="10" class="form-control" style="width: 100%; padding: 0.6rem; border: 1px solid var(--border-color); border-radius: var(--radius-sm);" required>
                        </div>

                        <div class="form-group" style="margin-bottom: 1rem;">
                            <label class="form-label" style="display: block; font-size: 0.85rem; font-weight: 600; margin-bottom: 0.35rem;">Round Name</label>
                            <input type="text" name="roundName" placeholder="e.g. Round 1: Online Coding Assessment" class="form-control" style="width: 100%; padding: 0.6rem; border: 1px solid var(--border-color); border-radius: var(--radius-sm);" required>
                        </div>

                        <div class="form-group" style="margin-bottom: 1rem;">
                            <label class="form-label" style="display: block; font-size: 0.85rem; font-weight: 600; margin-bottom: 0.35rem;">Round Type</label>
                            <select name="roundType" class="form-control" style="width: 100%; padding: 0.6rem; border: 1px solid var(--border-color); border-radius: var(--radius-sm);">
                                <option value="ONLINE_ASSESSMENT">Online Assessment / Aptitude</option>
                                <option value="CODING_TEST">Technical Coding Challenge</option>
                                <option value="TECHNICAL_INTERVIEW_1">Technical Interview 1</option>
                                <option value="TECHNICAL_INTERVIEW_2">Technical Interview 2</option>
                                <option value="HR_INTERVIEW">HR & Leadership Interview</option>
                            </select>
                        </div>

                        <div class="form-group" style="margin-bottom: 1rem;">
                            <label class="form-label" style="display: block; font-size: 0.85rem; font-weight: 600; margin-bottom: 0.35rem;">Scheduled Date & Time</label>
                            <input type="datetime-local" id="scheduledDate" name="scheduledDate" class="form-control" style="width: 100%; padding: 0.6rem; border: 1px solid var(--border-color); border-radius: var(--radius-sm);">
                        </div>

                        <div class="form-group" style="margin-bottom: 1.5rem;">
                            <label class="form-label" style="display: block; font-size: 0.85rem; font-weight: 600; margin-bottom: 0.35rem;">Venue or Virtual Meeting Link</label>
                            <input type="text" name="venueOrLink" placeholder="e.g. Google Meet Link / Campus Lab 402" class="form-control" style="width: 100%; padding: 0.6rem; border: 1px solid var(--border-color); border-radius: var(--radius-sm);">
                        </div>

                        <button type="submit" class="btn btn-primary" style="width: 100%; padding: 0.75rem;">
                            🚀 Schedule Round
                        </button>
                    </form>
                </div>
            </div>

            <!-- Column 2: Scheduled Rounds & Candidate Scorecards -->
            <div>
                <c:choose>
                    <c:when test="${not empty rounds}">
                        <c:forEach var="r" items="${rounds}">
                            <div class="round-card">
                                <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 0.75rem; border-bottom: 1px solid var(--border-color); padding-bottom: 0.5rem;">
                                    <div>
                                        <h3 style="font-size: 1.15rem; font-weight: 700; color: var(--text-primary);">
                                            ${r.roundName}
                                        </h3>
                                        <div style="font-size: 0.8rem; color: var(--text-secondary);">
                                            Type: <strong>${r.roundType.displayName}</strong> | Date: <strong>${r.scheduledDate}</strong>
                                        </div>
                                    </div>
                                    <span class="status-badge ok">Round #${r.roundNumber}</span>
                                </div>

                                <p style="font-size: 0.85rem; color: var(--text-secondary); margin-bottom: 0.75rem;">
                                    📍 Venue/Link: <code>${not empty r.venueOrLink ? r.venueOrLink : 'To be announced'}</code>
                                </p>

                                <h4 style="font-size: 0.9rem; font-weight: 700; margin-top: 1rem; color: var(--text-primary);">
                                    Evaluate Candidates for this Round:
                                </h4>

                                <c:choose>
                                    <c:when test="${not empty applicants}">
                                        <table class="custom-table">
                                            <thead>
                                                <tr>
                                                    <th>Candidate</th>
                                                    <th>Score</th>
                                                    <th>Feedback</th>
                                                    <th>Outcome</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="cand" items="${applicants}">
                                                    <tr>
                                                        <td>
                                                            <strong>${cand.studentFullName}</strong>
                                                            <div style="font-size: 0.75rem; color: var(--text-secondary);">CGPA: ${cand.studentCgpa}</div>
                                                        </td>
                                                        <td colspan="3">
                                                            <form action="${pageContext.request.contextPath}/recruiter/rounds/evaluate" method="POST" style="display: flex; gap: 0.4rem; align-items: center; flex-wrap: wrap;">
                                                                <input type="hidden" name="jobId" value="${job.id}">
                                                                <input type="hidden" name="roundId" value="${r.id}">
                                                                <input type="hidden" name="applicationId" value="${cand.id}">

                                                                <input type="text" name="score" placeholder="Score (e.g. 85/100)" style="width: 110px; font-size: 0.8rem; padding: 0.3rem 0.5rem; border: 1px solid var(--border-color); border-radius: var(--radius-sm);">
                                                                <input type="text" name="feedback" placeholder="Remarks" style="width: 140px; font-size: 0.8rem; padding: 0.3rem 0.5rem; border: 1px solid var(--border-color); border-radius: var(--radius-sm);">

                                                                <select name="status" style="font-size: 0.8rem; padding: 0.3rem 0.4rem; border: 1px solid var(--border-color); border-radius: var(--radius-sm);">
                                                                    <option value="QUALIFIED">Qualified ✓</option>
                                                                    <option value="DISQUALIFIED">Disqualified ✗</option>
                                                                    <option value="PENDING">Pending</option>
                                                                </select>

                                                                <button type="submit" class="btn btn-outline" style="font-size: 0.75rem; padding: 0.3rem 0.6rem;">
                                                                    Save
                                                                </button>
                                                            </form>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </c:when>
                                    <c:otherwise>
                                        <p style="font-size: 0.85rem; color: var(--text-secondary); margin-top: 0.5rem;">
                                            No applicants available to evaluate.
                                        </p>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="card" style="padding: 2.5rem; text-align: center;">
                            <div style="font-size: 2.5rem; margin-bottom: 0.5rem;">📅</div>
                            <h3 style="font-size: 1.15rem; font-weight: 700; margin-bottom: 0.5rem;">No Rounds Configured</h3>
                            <p style="font-size: 0.85rem; color: var(--text-secondary);">
                                Use the form on the left to schedule Round 1 (e.g. Online Assessment, Coding Round) for this placement drive.
                            </p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

        </div>
    </main>

    <!-- Footer -->
    <footer class="footer">
        <div class="container">
            <p>&copy; 2026 SmartHire Interview & Evaluation Hub.</p>
        </div>
    </footer>

    <script>
        const now = new Date();
        now.setDate(now.getDate() + 7);
        document.getElementById('scheduledDate').value = now.toISOString().slice(0, 16);
    </script>
</body>
</html>

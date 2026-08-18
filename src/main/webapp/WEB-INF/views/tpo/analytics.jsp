<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SmartHire | Placement Analytics & Accreditation</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .page-header {
            background-color: var(--bg-card);
            border-bottom: 1px solid var(--border-color);
            padding: 2rem 0;
            margin-bottom: 2rem;
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
            font-size: 2.2rem;
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
        .table-card {
            background-color: var(--bg-card);
            border: 1px solid var(--border-color);
            border-radius: var(--radius-lg);
            overflow: hidden;
            box-shadow: var(--shadow-sm);
            margin-bottom: 2.5rem;
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
        .progress-bar-bg {
            background-color: var(--border-color);
            border-radius: var(--radius-full);
            height: 10px;
            width: 120px;
            overflow: hidden;
            display: inline-block;
            vertical-align: middle;
            margin-right: 0.5rem;
        }
        .progress-bar-fill {
            background: linear-gradient(90deg, var(--primary), var(--secondary));
            height: 100%;
            border-radius: var(--radius-full);
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
                <li><a href="${pageContext.request.contextPath}/tpo/dashboard" class="nav-link">Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/tpo/students" class="nav-link">Students</a></li>
                <li><a href="${pageContext.request.contextPath}/tpo/companies" class="nav-link">Companies</a></li>
                <li><a href="${pageContext.request.contextPath}/tpo/drives" class="nav-link">Placement Drives</a></li>
                <li><a href="${pageContext.request.contextPath}/tpo/analytics" class="nav-link" style="color: var(--primary); font-weight: 700;">Analytics</a></li>
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
                        Placement Intelligence & Accreditation Analytics
                    </h1>
                    <p style="color: var(--text-secondary); font-size: 0.95rem;">
                        Real-time salary distribution, department-wise placement percentages, and institutional KPIs
                    </p>
                </div>
                <div>
                    <a href="${pageContext.request.contextPath}/tpo/export/students" class="btn btn-primary">
                        📥 Export Master Roster (CSV)
                    </a>
                </div>
            </div>
        </div>
    </header>

    <!-- Main Content -->
    <main class="container" style="margin-bottom: 4rem;">

        <!-- Primary KPI Metrics Grid -->
        <div class="stat-grid">
            <div class="stat-card">
                <div class="stat-label">Overall Placement Rate</div>
                <div class="stat-value" style="color: var(--success);">${analytics.overallPlacementRate}%</div>
                <p style="font-size: 0.85rem; color: var(--text-secondary);">
                    <strong>${analytics.placedStudents}</strong> Placed out of ${analytics.totalStudents} Candidates
                </p>
            </div>

            <div class="stat-card">
                <div class="stat-label">Highest Package (CTC)</div>
                <div class="stat-value">${analytics.highestPackageLpa} <span style="font-size: 1.1rem; color: var(--text-secondary);">LPA</span></div>
                <p style="font-size: 0.85rem; color: var(--text-secondary);">Top offer in current recruitment season</p>
            </div>

            <div class="stat-card">
                <div class="stat-label">Average Package (CTC)</div>
                <div class="stat-value">${analytics.averagePackageLpa} <span style="font-size: 1.1rem; color: var(--text-secondary);">LPA</span></div>
                <p style="font-size: 0.85rem; color: var(--text-secondary);">Across all accepted & offered roles</p>
            </div>

            <div class="stat-card">
                <div class="stat-label">Partner Recruiters</div>
                <div class="stat-value">${analytics.totalCompanies}</div>
                <p style="font-size: 0.85rem; color: var(--text-secondary);">
                    <strong>${analytics.activeDrives}</strong> Active Drives Open
                </p>
            </div>
        </div>

        <!-- Department Breakdown Table -->
        <div class="table-card">
            <div style="padding: 1.5rem; border-bottom: 1px solid var(--border-color);">
                <h2 style="font-size: 1.25rem; font-weight: 700;">Department-Wise Placement Performance</h2>
                <p style="color: var(--text-secondary); font-size: 0.85rem;">Branch breakdown required for NIRF / NAAC university accreditation reports</p>
            </div>

            <c:choose>
                <c:when test="${not empty analytics.departmentStats}">
                    <table class="custom-table">
                        <thead>
                            <tr>
                                <th>Academic Department</th>
                                <th>Total Candidates</th>
                                <th>Placed Candidates</th>
                                <th>Placement Rate</th>
                                <th>Average Package (LPA)</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="d" items="${analytics.departmentStats}">
                                <tr>
                                    <td>
                                        <strong style="font-size: 1rem; color: var(--text-primary);">${d.department}</strong>
                                    </td>
                                    <td>
                                        <span>${d.totalStudents} Registered</span>
                                    </td>
                                    <td>
                                        <span style="font-weight: 700; color: var(--success);">${d.placedStudents} Placed</span>
                                    </td>
                                    <td>
                                        <div class="progress-bar-bg">
                                            <div class="progress-bar-fill" style="width: ${d.placementRate}%;"></div>
                                        </div>
                                        <strong style="font-size: 0.9rem;">${d.placementRate}%</strong>
                                    </td>
                                    <td>
                                        <span style="font-weight: 700; color: var(--primary);">
                                            ${d.averagePackage} LPA
                                        </span>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <div style="padding: 2.5rem; text-align: center; color: var(--text-secondary);">
                        No departmental placement records recorded yet.
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

    </main>

    <!-- Footer -->
    <footer class="footer">
        <div class="container">
            <p>&copy; 2026 SmartHire Institutional Analytics Hub.</p>
        </div>
    </footer>

</body>
</html>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SmartHire | Academic Profile & Resume</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .profile-header {
            background-color: var(--bg-card);
            border-bottom: 1px solid var(--border-color);
            padding: 2rem 0;
            margin-bottom: 2rem;
        }
        .form-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 1.25rem;
            margin-bottom: 1.5rem;
        }
        .form-group {
            margin-bottom: 1rem;
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
            padding: 0.65rem 0.9rem;
            border: 1px solid var(--border-color);
            border-radius: var(--radius-md);
            font-size: 0.9rem;
            font-family: inherit;
            transition: all 0.2s;
        }
        .form-control:focus {
            outline: none;
            border-color: var(--primary);
            box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.15);
        }
        .section-card {
            background-color: var(--bg-card);
            border: 1px solid var(--border-color);
            border-radius: var(--radius-lg);
            padding: 2rem;
            margin-bottom: 2rem;
            box-shadow: var(--shadow-sm);
        }
        .section-title-box {
            display: flex;
            align-items: center;
            gap: 0.6rem;
            margin-bottom: 1.5rem;
            padding-bottom: 0.75rem;
            border-bottom: 1px solid var(--border-color);
        }
        .section-title-box h2 {
            font-size: 1.2rem;
            font-weight: 700;
        }
        .progress-bar-container {
            background-color: var(--border-color);
            height: 12px;
            border-radius: var(--radius-full);
            overflow: hidden;
            margin: 0.75rem 0 0.5rem 0;
        }
        .progress-bar-fill {
            height: 100%;
            border-radius: var(--radius-full);
            background: linear-gradient(90deg, var(--primary) 0%, #3b82f6 100%);
            transition: width 0.5s ease;
        }
        .alert {
            padding: 0.85rem 1.25rem;
            border-radius: var(--radius-md);
            margin-bottom: 1.5rem;
            font-size: 0.95rem;
            font-weight: 500;
        }
        .alert-success {
            background-color: var(--success-light);
            color: var(--success);
            border: 1px solid rgba(22, 163, 74, 0.3);
        }
        .alert-danger {
            background-color: var(--danger-light);
            color: var(--danger);
            border: 1px solid rgba(220, 38, 38, 0.3);
        }
        .resume-badge {
            display: inline-flex;
            align-items: center;
            gap: 0.5rem;
            background-color: var(--primary-light);
            color: var(--primary);
            padding: 0.5rem 1rem;
            border-radius: var(--radius-md);
            font-weight: 600;
            font-size: 0.85rem;
            border: 1px solid rgba(37, 99, 235, 0.2);
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
                <li><a href="${pageContext.request.contextPath}/student/profile" class="nav-link" style="color: var(--primary); font-weight: 700;">My Profile</a></li>
                <li><a href="${pageContext.request.contextPath}/auth/logout" class="btn btn-outline" style="font-size: 0.85rem;">Sign Out</a></li>
            </ul>
        </div>
    </nav>

    <!-- Header & Profile Completion Progress -->
    <header class="profile-header">
        <div class="container">
            <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem; margin-bottom: 1.5rem;">
                <div>
                    <h1 style="font-size: 1.75rem; font-weight: 800; margin-bottom: 0.25rem;">
                        ${profile.fullName}
                    </h1>
                    <p style="color: var(--text-secondary); font-size: 0.95rem;">
                        Roll No: <strong>${profile.rollNumber}</strong> | Department: <strong>${profile.department}</strong> | Batch: <strong>${profile.graduationYear}</strong>
                    </p>
                </div>
                <div>
                    <c:choose>
                        <c:when test="${profile.verified}">
                            <span class="status-badge ok" style="padding: 0.5rem 1rem; font-size: 0.85rem;">
                                ✓ VERIFIED BY TPO
                            </span>
                        </c:when>
                        <c:otherwise>
                            <span class="status-badge" style="background-color: var(--warning-light); color: var(--warning); border: 1px solid rgba(217, 119, 6, 0.3); padding: 0.5rem 1rem; font-size: 0.85rem;">
                                ⏳ PENDING TPO VERIFICATION
                            </span>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <!-- Profile Completion Meter -->
            <div style="background-color: var(--bg-main); border: 1px solid var(--border-color); border-radius: var(--radius-md); padding: 1rem 1.25rem;">
                <div style="display: flex; justify-content: space-between; align-items: center;">
                    <span style="font-size: 0.85rem; font-weight: 700; color: var(--text-primary);">
                        Profile Completeness:
                    </span>
                    <span style="font-size: 0.9rem; font-weight: 800; color: var(--primary);">
                        ${profile.completionPercentage}% Complete
                    </span>
                </div>
                <div class="progress-bar-container">
                    <div class="progress-bar-fill" style="width: ${profile.completionPercentage}%;"></div>
                </div>
                <p style="font-size: 0.75rem; color: var(--text-secondary); margin-top: 0.25rem;">
                    💡 A minimum 80% complete profile is required to apply for campus placement drives.
                </p>
            </div>
        </div>
    </header>

    <!-- Main Profile Edit Form -->
    <main class="container">

        <!-- Flash Messages -->
        <c:if test="${not empty successMessage}">
            <div class="alert alert-success">
                ✓ ${successMessage}
            </div>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">
                ⚠️ ${errorMessage}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/student/profile" method="POST" enctype="multipart/form-data">
            
            <!-- Section 1: Personal Details -->
            <div class="section-card">
                <div class="section-title-box">
                    <span>👤</span>
                    <h2>1. Personal & Contact Information</h2>
                </div>

                <div class="form-grid">
                    <div class="form-group">
                        <label class="form-label" for="firstName">First Name</label>
                        <input type="text" id="firstName" name="firstName" class="form-control" 
                               value="${profile.firstName}" placeholder="e.g. Rahul" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="lastName">Last Name</label>
                        <input type="text" id="lastName" name="lastName" class="form-control" 
                               value="${profile.lastName}" placeholder="e.g. Verma" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="phone">Contact Mobile Number</label>
                        <input type="tel" id="phone" name="phone" class="form-control" 
                               value="${profile.phone}" placeholder="e.g. 9876543210" pattern="[0-9]{10}" title="10-digit mobile number">
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="gender">Gender</label>
                        <select id="gender" name="gender" class="form-control">
                            <option value="MALE" ${profile.gender == 'MALE' ? 'selected' : ''}>Male</option>
                            <option value="FEMALE" ${profile.gender == 'FEMALE' ? 'selected' : ''}>Female</option>
                            <option value="OTHER" ${profile.gender == 'OTHER' ? 'selected' : ''}>Other</option>
                        </select>
                    </div>
                </div>
            </div>

            <!-- Section 2: Academic Records (Eligibility Critical) -->
            <div class="section-card">
                <div class="section-title-box">
                    <span>📚</span>
                    <h2>2. Academic Credentials (Used for Placement Drive Eligibility)</h2>
                </div>

                <div class="form-grid">
                    <div class="form-group">
                        <label class="form-label" for="rollNumber">Institutional Roll Number</label>
                        <input type="text" id="rollNumber" name="rollNumber" class="form-control" 
                               value="${profile.rollNumber}" placeholder="e.g. CS2026001" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="department">Department / Branch</label>
                        <select id="department" name="department" class="form-control" required>
                            <option value="CSE" ${profile.department == 'CSE' ? 'selected' : ''}>Computer Science & Engineering (CSE)</option>
                            <option value="IT" ${profile.department == 'IT' ? 'selected' : ''}>Information Technology (IT)</option>
                            <option value="ECE" ${profile.department == 'ECE' ? 'selected' : ''}>Electronics & Communication (ECE)</option>
                            <option value="EEE" ${profile.department == 'EEE' ? 'selected' : ''}>Electrical & Electronics (EEE)</option>
                            <option value="MECH" ${profile.department == 'MECH' ? 'selected' : ''}>Mechanical Engineering (MECH)</option>
                            <option value="CIVIL" ${profile.department == 'CIVIL' ? 'selected' : ''}>Civil Engineering (CIVIL)</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="cgpa">Current Cumulative CGPA (0.00 - 10.00)</label>
                        <input type="number" step="0.01" min="0.00" max="10.00" id="cgpa" name="cgpa" class="form-control" 
                               value="${profile.cgpa}" placeholder="e.g. 8.65" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="graduationYear">Passing Out / Graduation Year</label>
                        <input type="number" min="2020" max="2035" id="graduationYear" name="graduationYear" class="form-control" 
                               value="${profile.graduationYear}" placeholder="e.g. 2026" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="tenthPercentage">10th Standard Percentage (%)</label>
                        <input type="number" step="0.01" min="0.00" max="100.00" id="tenthPercentage" name="tenthPercentage" class="form-control" 
                               value="${profile.tenthPercentage}" placeholder="e.g. 91.50" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="twelfthPercentage">12th / Diploma Percentage (%)</label>
                        <input type="number" step="0.01" min="0.00" max="100.00" id="twelfthPercentage" name="twelfthPercentage" class="form-control" 
                               value="${profile.twelfthPercentage}" placeholder="e.g. 88.20" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="activeBacklogs">Active / Standing Backlogs Count</label>
                        <input type="number" min="0" max="20" id="activeBacklogs" name="activeBacklogs" class="form-control" 
                               value="${profile.activeBacklogs}" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="totalBacklogsHistory">Total Backlogs in Academic History</label>
                        <input type="number" min="0" max="30" id="totalBacklogsHistory" name="totalBacklogsHistory" class="form-control" 
                               value="${profile.totalBacklogsHistory}" required>
                    </div>
                </div>
            </div>

            <!-- Section 3: Skills & Resume Document -->
            <div class="section-card">
                <div class="section-title-box">
                    <span>📄</span>
                    <h2>3. Skills & Verified PDF Resume</h2>
                </div>

                <div class="form-group" style="margin-bottom: 1.5rem;">
                    <label class="form-label" for="skills">Key Technical Skills (Comma-separated)</label>
                    <textarea id="skills" name="skills" rows="3" class="form-control" 
                              placeholder="e.g. Java, Python, Spring Boot, MySQL, Data Structures, Git, HTML/CSS">${profile.skills}</textarea>
                </div>

                <div style="background-color: var(--bg-main); border: 1px dashed var(--border-color); border-radius: var(--radius-md); padding: 1.5rem;">
                    <label class="form-label" style="font-size: 0.95rem; margin-bottom: 0.5rem;">
                        Upload Verified Resume (PDF Only, Max 5 MB)
                    </label>

                    <c:if test="${not empty profile.resumeFilePath}">
                        <div style="display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: 1rem; margin-bottom: 1rem;">
                            <div class="resume-badge">
                                📎 Current Document: <code>${profile.rollNumber}_Resume.pdf</code>
                            </div>
                            <a href="${pageContext.request.contextPath}/student/resume/download" target="_blank" class="btn btn-outline" style="font-size: 0.85rem;">
                                👁️ View / Download Resume &rarr;
                            </a>
                        </div>
                    </c:if>

                    <input type="file" id="resumeFile" name="resumeFile" class="form-control" accept="application/pdf">
                    <p style="font-size: 0.8rem; color: var(--text-secondary); margin-top: 0.35rem;">
                        Uploading a new file will automatically replace your current resume document.
                    </p>
                </div>
            </div>

            <!-- Action Buttons -->
            <div style="display: flex; justify-content: flex-end; gap: 1rem; margin-bottom: 3rem;">
                <a href="${pageContext.request.contextPath}/student/dashboard" class="btn btn-outline">Cancel</a>
                <button type="submit" class="btn btn-primary" style="padding: 0.75rem 2rem; font-size: 1rem;">
                    💾 Save Academic Profile
                </button>
            </div>

        </form>
    </main>

    <!-- Footer -->
    <footer class="footer">
        <div class="container">
            <p>&copy; 2026 SmartHire Student Profile Engine.</p>
        </div>
    </footer>

</body>
</html>

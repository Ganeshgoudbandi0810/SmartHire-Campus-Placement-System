<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SmartHire | Create Account</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .auth-container {
            max-width: 520px;
            margin: 3rem auto;
            padding: 0 1rem;
        }
        .form-group {
            margin-bottom: 1.25rem;
        }
        .form-label {
            display: block;
            font-size: 0.9rem;
            font-weight: 600;
            margin-bottom: 0.4rem;
            color: var(--text-primary);
        }
        .form-control {
            width: 100%;
            padding: 0.75rem 1rem;
            border: 1px solid var(--border-color);
            border-radius: var(--radius-md);
            font-size: 0.95rem;
            font-family: inherit;
            transition: border-color 0.2s, box-shadow 0.2s;
        }
        .form-control:focus {
            outline: none;
            border-color: var(--primary);
            box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.15);
        }
        .role-selector-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 1rem;
            margin-bottom: 1.25rem;
        }
        .role-option {
            border: 2px solid var(--border-color);
            border-radius: var(--radius-md);
            padding: 1rem;
            text-align: center;
            cursor: pointer;
            transition: all 0.2s ease;
        }
        .role-option:hover {
            border-color: var(--primary);
            background-color: var(--primary-light);
        }
        .role-option input[type="radio"] {
            display: none;
        }
        .role-option.selected {
            border-color: var(--primary);
            background-color: var(--primary-light);
        }
        .alert-danger {
            background-color: var(--danger-light);
            color: var(--danger);
            border: 1px solid rgba(220, 38, 38, 0.3);
            padding: 0.85rem 1rem;
            border-radius: var(--radius-md);
            margin-bottom: 1.25rem;
            font-size: 0.9rem;
            font-weight: 500;
        }
    </style>
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
                <li><a href="${pageContext.request.contextPath}/auth/login" class="btn btn-outline">Log In</a></li>
            </ul>
        </div>
    </nav>

    <!-- Main Registration Card -->
    <main class="container">
        <div class="auth-container">
            <div class="card" style="padding: 2.5rem 2rem;">
                <div style="text-align: center; margin-bottom: 2rem;">
                    <div style="font-size: 2.5rem; margin-bottom: 0.5rem;">📝</div>
                    <h1 style="font-size: 1.6rem; font-weight: 800; color: var(--text-primary);">Create Your Account</h1>
                    <p style="color: var(--text-secondary); font-size: 0.95rem; margin-top: 0.25rem;">
                        Join SmartHire to streamline campus placements
                    </p>
                </div>

                <!-- Error Messages -->
                <c:if test="${not empty errorMessage}">
                    <div class="alert-danger">
                        ⚠️ ${errorMessage}
                    </div>
                </c:if>

                <!-- Registration Form -->
                <form action="${pageContext.request.contextPath}/auth/register" method="POST">
                    
                    <label class="form-label">Select Your Account Type</label>
                    <div class="role-selector-grid">
                        <label class="role-option ${selectedRole == 'STUDENT' || empty selectedRole ? 'selected' : ''}" id="role-student-label">
                            <input type="radio" name="role" value="STUDENT" ${selectedRole == 'STUDENT' || empty selectedRole ? 'checked' : ''} onchange="selectRole('STUDENT')">
                            <div style="font-size: 1.5rem; margin-bottom: 0.25rem;">👨‍🎓</div>
                            <div style="font-weight: 700; font-size: 0.95rem;">Student</div>
                            <div style="font-size: 0.75rem; color: var(--text-secondary);">Looking for placement drives</div>
                        </label>

                        <label class="role-option ${selectedRole == 'RECRUITER' ? 'selected' : ''}" id="role-recruiter-label">
                            <input type="radio" name="role" value="RECRUITER" ${selectedRole == 'RECRUITER' ? 'checked' : ''} onchange="selectRole('RECRUITER')">
                            <div style="font-size: 1.5rem; margin-bottom: 0.25rem;">💼</div>
                            <div style="font-weight: 700; font-size: 0.95rem;">Recruiter</div>
                            <div style="font-size: 0.75rem; color: var(--text-secondary);">Hiring fresh campus talent</div>
                        </label>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="email">Institutional / Company Email</label>
                        <input type="email" id="email" name="email" class="form-control" 
                               value="${email}" placeholder="name@domain.edu or hr@company.com" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="password">Create Password</label>
                        <input type="password" id="password" name="password" class="form-control" 
                               placeholder="Minimum 6 characters" minlength="6" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="confirmPassword">Confirm Password</label>
                        <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" 
                               placeholder="Re-enter password" minlength="6" required>
                    </div>

                    <button type="submit" class="btn btn-primary" style="width: 100%; padding: 0.75rem; font-size: 1rem; margin-top: 0.5rem;">
                        Register Account &rarr;
                    </button>
                </form>

                <p style="text-align: center; margin-top: 1.5rem; font-size: 0.9rem; color: var(--text-secondary);">
                    Already have an account? 
                    <a href="${pageContext.request.contextPath}/auth/login" style="color: var(--primary); font-weight: 600; text-decoration: none;">Sign in here</a>
                </p>
            </div>
        </div>
    </main>

    <!-- Footer -->
    <footer class="footer">
        <div class="container">
            <p>&copy; 2026 SmartHire Campus Placement System.</p>
        </div>
    </footer>

    <script>
        function selectRole(role) {
            document.getElementById('role-student-label').classList.toggle('selected', role === 'STUDENT');
            document.getElementById('role-recruiter-label').classList.toggle('selected', role === 'RECRUITER');
        }
    </script>
</body>
</html>

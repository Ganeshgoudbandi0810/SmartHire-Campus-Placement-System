<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SmartHire | Sign In</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .auth-container {
            max-width: 480px;
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
        .alert {
            padding: 0.85rem 1rem;
            border-radius: var(--radius-md);
            margin-bottom: 1.25rem;
            font-size: 0.9rem;
            font-weight: 500;
        }
        .alert-danger {
            background-color: var(--danger-light);
            color: var(--danger);
            border: 1px solid rgba(220, 38, 38, 0.3);
        }
        .alert-success {
            background-color: var(--success-light);
            color: var(--success);
            border: 1px solid rgba(22, 163, 74, 0.3);
        }
        .demo-box {
            background-color: var(--bg-main);
            border: 1px dashed var(--border-color);
            border-radius: var(--radius-md);
            padding: 1rem;
            margin-top: 1.5rem;
        }
        .demo-pill {
            display: inline-block;
            background-color: var(--bg-card);
            border: 1px solid var(--border-color);
            border-radius: var(--radius-sm);
            padding: 0.25rem 0.6rem;
            font-size: 0.75rem;
            font-weight: 600;
            color: var(--primary);
            cursor: pointer;
            margin: 0.25rem 0.2rem;
            transition: all 0.2s;
        }
        .demo-pill:hover {
            background-color: var(--primary-light);
            border-color: var(--primary);
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
                <li><a href="${pageContext.request.contextPath}/health" class="nav-link">System Health</a></li>
                <li><a href="${pageContext.request.contextPath}/db-test" class="nav-link">Database Health</a></li>
                <li><a href="${pageContext.request.contextPath}/auth/register" class="btn btn-outline">Sign Up</a></li>
            </ul>
        </div>
    </nav>

    <!-- Main Auth Container -->
    <main class="container">
        <div class="auth-container">
            <div class="card" style="padding: 2.5rem 2rem;">
                <div style="text-align: center; margin-bottom: 2rem;">
                    <div style="font-size: 2.5rem; margin-bottom: 0.5rem;">🔐</div>
                    <h1 style="font-size: 1.6rem; font-weight: 800; color: var(--text-primary);">Sign in to SmartHire</h1>
                    <p style="color: var(--text-secondary); font-size: 0.95rem; margin-top: 0.25rem;">
                        Access your placement and recruitment workspace
                    </p>
                </div>

                <!-- Error Messages -->
                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger">
                        ⚠️ ${errorMessage}
                    </div>
                </c:if>

                <!-- Success Messages -->
                <c:if test="${not empty successMessage}">
                    <div class="alert alert-success">
                        ✓ ${successMessage}
                    </div>
                </c:if>
                <c:if test="${param.success == 'registered'}">
                    <div class="alert alert-success">
                        ✓ Registration successful! Please sign in with your credentials.
                    </div>
                </c:if>

                <!-- Login Form -->
                <form action="${pageContext.request.contextPath}/auth/login" method="POST">
                    <input type="hidden" name="redirectUrl" value="${redirectUrl}">

                    <div class="form-group">
                        <label class="form-label" for="email">Email Address</label>
                        <input type="email" id="email" name="email" class="form-control" 
                               value="${email != null ? email : param.email}" 
                               placeholder="e.g. rahul.verma@smarthire.edu" required autofocus>
                    </div>

                    <div class="form-group">
                        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.4rem;">
                            <label class="form-label" for="password" style="margin-bottom: 0;">Password</label>
                        </div>
                        <input type="password" id="password" name="password" class="form-control" 
                               placeholder="Enter your password" required>
                    </div>

                    <button type="submit" class="btn btn-primary" style="width: 100%; padding: 0.75rem; font-size: 1rem; margin-top: 0.5rem;">
                        Sign In &rarr;
                    </button>
                </form>

                <!-- Quick Demo Login Auto-Fill Helpers -->
                <div class="demo-box">
                    <p style="font-size: 0.8rem; font-weight: 700; color: var(--text-secondary); margin-bottom: 0.35rem;">
                        ⚡ Quick Demo Autofill (Password: <code>Password@123</code>):
                    </p>
                    <div>
                        <button type="button" class="demo-pill" onclick="fillCredentials('admin@smarthire.edu')">👨‍💼 TPO Admin</button>
                        <button type="button" class="demo-pill" onclick="fillCredentials('rahul.verma@smarthire.edu')">👨‍🎓 Student</button>
                        <button type="button" class="demo-pill" onclick="fillCredentials('recruiter@techcorp.com')">💼 Recruiter</button>
                    </div>
                </div>

                <p style="text-align: center; margin-top: 1.5rem; font-size: 0.9rem; color: var(--text-secondary);">
                    Don't have an account? 
                    <a href="${pageContext.request.contextPath}/auth/register" style="color: var(--primary); font-weight: 600; text-decoration: none;">Create one here</a>
                </p>
            </div>
        </div>
    </main>

    <!-- Footer -->
    <footer class="footer">
        <div class="container">
            <p>&copy; 2026 SmartHire Campus Placement System. Secure BCrypt Authentication.</p>
        </div>
    </footer>

    <script>
        function fillCredentials(email) {
            document.getElementById('email').value = email;
            document.getElementById('password').value = 'Password@123';
        }
    </script>
</body>
</html>

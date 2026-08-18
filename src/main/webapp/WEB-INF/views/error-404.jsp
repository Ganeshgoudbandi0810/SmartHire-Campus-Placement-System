<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SmartHire | 404 - Page Not Found</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .error-container {
            min-height: 80vh;
            display: flex;
            align-items: center;
            justify-content: center;
            text-align: center;
            padding: 2rem;
        }
        .error-card {
            background-color: var(--bg-card);
            border: 1px solid var(--border-color);
            border-radius: var(--radius-lg);
            padding: 3.5rem 2.5rem;
            max-width: 550px;
            box-shadow: var(--shadow-md);
        }
        .error-code {
            font-size: 5rem;
            font-weight: 900;
            color: var(--primary);
            line-height: 1;
            margin-bottom: 1rem;
        }
    </style>
</head>
<body>

    <nav class="navbar">
        <div class="container nav-container">
            <a href="${pageContext.request.contextPath}/" class="brand-logo">🎓 SmartHire</a>
        </div>
    </nav>

    <div class="error-container">
        <div class="error-card">
            <div class="error-code">404</div>
            <h1 style="font-size: 1.5rem; font-weight: 800; margin-bottom: 0.75rem;">Page Not Found</h1>
            <p style="color: var(--text-secondary); margin-bottom: 2rem; line-height: 1.5;">
                The resource or portal route you requested could not be located on the SmartHire Placement Server.
            </p>
            <a href="${pageContext.request.contextPath}/" class="btn btn-primary" style="padding: 0.75rem 2rem;">
                &larr; Return to Home Portal
            </a>
        </div>
    </div>

    <footer class="footer">
        <div class="container">
            <p>&copy; 2026 SmartHire Campus Recruitment Management System.</p>
        </div>
    </footer>

</body>
</html>

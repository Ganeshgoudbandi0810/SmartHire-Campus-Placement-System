/**
 * SmartHire - Core Client-Side Utilities
 */

document.addEventListener('DOMContentLoaded', () => {
    // Auto-dismiss alert banners after 5 seconds if any exist
    const alerts = document.querySelectorAll('.alert-auto-dismiss');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.5s ease';
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 500);
        }, 5000);
    });

    console.log('SmartHire UI Client Engine Loaded.');
});

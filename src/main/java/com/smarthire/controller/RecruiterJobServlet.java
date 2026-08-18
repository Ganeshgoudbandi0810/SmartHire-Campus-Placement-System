package com.smarthire.controller;

import com.smarthire.exception.ValidationException;
import com.smarthire.model.*;
import com.smarthire.service.CompanyService;
import com.smarthire.service.CompanyServiceImpl;
import com.smarthire.service.JobService;
import com.smarthire.service.JobServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * RecruiterJobServlet
 *
 * Controller handling placement drive management, creation of new job postings,
 * and drive lifecycle state transitions for Corporate Recruiters.
 */
@WebServlet(name = "RecruiterJobServlet", urlPatterns = {
        "/recruiter/jobs",
        "/recruiter/jobs/create",
        "/recruiter/jobs/status"
})
public class RecruiterJobServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(RecruiterJobServlet.class);
    private static final DateTimeFormatter DATETIME_LOCAL_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private JobService jobService;
    private CompanyService companyService;

    @Override
    public void init() throws ServletException {
        this.jobService = new JobServiceImpl();
        this.companyService = new CompanyServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("currentUser");
        Company company = companyService.getCompanyByUserId(currentUser.getId());

        String path = request.getServletPath();

        if ("/recruiter/jobs/create".equals(path)) {
            request.setAttribute("company", company);
            request.getRequestDispatcher("/WEB-INF/views/recruiter/jobs/create.jsp").forward(request, response);
            return;
        }

        // Default: /recruiter/jobs (Listing)
        List<JobPosting> jobs = jobService.getJobsByCompany(company.getId());
        request.setAttribute("company", company);
        request.setAttribute("jobs", jobs);

        if ("created".equalsIgnoreCase(request.getParameter("success"))) {
            request.setAttribute("successMessage", "Placement drive posted successfully!");
        } else if ("status_updated".equalsIgnoreCase(request.getParameter("success"))) {
            request.setAttribute("successMessage", "Drive status updated successfully!");
        }

        request.getRequestDispatcher("/WEB-INF/views/recruiter/jobs/list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("currentUser");
        Company company = companyService.getCompanyByUserId(currentUser.getId());

        String path = request.getServletPath();

        if ("/recruiter/jobs/status".equals(path)) {
            // Status update action
            try {
                int jobId = Integer.parseInt(request.getParameter("jobId"));
                JobStatus status = JobStatus.fromString(request.getParameter("status"));
                jobService.setJobStatus(jobId, status);
                response.sendRedirect(request.getContextPath() + "/recruiter/jobs?success=status_updated");
            } catch (Exception e) {
                logger.error("Error updating status: {}", e.getMessage());
                response.sendRedirect(request.getContextPath() + "/recruiter/jobs?error=" + e.getMessage());
            }
            return;
        }

        // Action: Create New Job Drive
        try {
            JobPosting job = new JobPosting();
            job.setCompanyId(company.getId());
            job.setJobTitle(request.getParameter("jobTitle"));
            job.setJobDescription(request.getParameter("jobDescription"));
            job.setJobLocation(request.getParameter("jobLocation"));
            job.setEmploymentType(EmploymentType.fromString(request.getParameter("employmentType")));

            String packageStr = request.getParameter("packageLpa");
            job.setPackageLpa(packageStr != null && !packageStr.trim().isEmpty() ? new BigDecimal(packageStr.trim()) : BigDecimal.ZERO);

            String minCgpaStr = request.getParameter("minCgpa");
            job.setMinCgpa(minCgpaStr != null && !minCgpaStr.trim().isEmpty() ? new BigDecimal(minCgpaStr.trim()) : new BigDecimal("6.00"));

            String tenthStr = request.getParameter("minTenthPercentage");
            job.setMinTenthPercentage(tenthStr != null && !tenthStr.trim().isEmpty() ? new BigDecimal(tenthStr.trim()) : new BigDecimal("60.00"));

            String twelfthStr = request.getParameter("minTwelfthPercentage");
            job.setMinTwelfthPercentage(twelfthStr != null && !twelfthStr.trim().isEmpty() ? new BigDecimal(twelfthStr.trim()) : new BigDecimal("60.00"));

            String backlogsStr = request.getParameter("maxBacklogsAllowed");
            job.setMaxBacklogsAllowed(backlogsStr != null && !backlogsStr.trim().isEmpty() ? Integer.parseInt(backlogsStr.trim()) : 0);

            String gradYearStr = request.getParameter("graduationYear");
            job.setGraduationYear(gradYearStr != null && !gradYearStr.trim().isEmpty() ? Integer.parseInt(gradYearStr.trim()) : 2026);

            // Parse branch checkboxes
            String[] branches = request.getParameterValues("branches");
            if (branches != null && branches.length > 0) {
                job.setEligibleBranches(String.join(",", branches));
            } else {
                job.setEligibleBranches("ALL");
            }

            // Parse Deadlines
            String deadlineStr = request.getParameter("applicationDeadline");
            if (deadlineStr != null && !deadlineStr.trim().isEmpty()) {
                job.setApplicationDeadline(LocalDateTime.parse(deadlineStr.trim(), DATETIME_LOCAL_FORMAT));
            }

            String driveDateStr = request.getParameter("driveDate");
            if (driveDateStr != null && !driveDateStr.trim().isEmpty()) {
                job.setDriveDate(LocalDateTime.parse(driveDateStr.trim(), DATETIME_LOCAL_FORMAT));
            } else if (job.getApplicationDeadline() != null) {
                job.setDriveDate(job.getApplicationDeadline().plusDays(3));
            }

            jobService.createJobDrive(job);
            response.sendRedirect(request.getContextPath() + "/recruiter/jobs?success=created");

        } catch (ValidationException e) {
            logger.warn("Validation error creating drive: {}", e.getMessage());
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("company", company);
            request.getRequestDispatcher("/WEB-INF/views/recruiter/jobs/create.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Error creating job drive: {}", e.getMessage(), e);
            request.setAttribute("errorMessage", "Failed to create drive: " + e.getMessage());
            request.setAttribute("company", company);
            request.getRequestDispatcher("/WEB-INF/views/recruiter/jobs/create.jsp").forward(request, response);
        }
    }
}

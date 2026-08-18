package com.smarthire.controller;

import com.smarthire.model.*;
import com.smarthire.service.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * RecruiterApplicantServlet
 *
 * Controller enabling recruiters to review candidate applications, filter by qualifications,
 * inspect PDF resumes, transition candidate statuses, and release official offers.
 */
@WebServlet(name = "RecruiterApplicantServlet", urlPatterns = {
        "/recruiter/applicants",
        "/recruiter/applicants/status",
        "/recruiter/applicants/offer"
})
public class RecruiterApplicantServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(RecruiterApplicantServlet.class);

    private CompanyService companyService;
    private JobService jobService;
    private ApplicationService applicationService;
    private SelectionService selectionService;

    @Override
    public void init() throws ServletException {
        this.companyService = new CompanyServiceImpl();
        this.jobService = new JobServiceImpl();
        this.applicationService = new ApplicationServiceImpl();
        this.selectionService = new SelectionServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("currentUser");
        Company company = companyService.getCompanyByUserId(currentUser.getId());

        List<JobPosting> companyJobs = jobService.getJobsByCompany(company.getId());

        String jobIdParam = request.getParameter("jobId");
        JobPosting selectedJob = null;
        if (jobIdParam != null && !jobIdParam.trim().isEmpty()) {
            try {
                selectedJob = jobService.getJobById(Integer.parseInt(jobIdParam.trim()));
            } catch (Exception ignored) {
            }
        }

        if (selectedJob == null && !companyJobs.isEmpty()) {
            selectedJob = companyJobs.get(0);
        }

        List<JobApplication> applicants = null;
        List<SelectionRound> rounds = null;

        if (selectedJob != null) {
            applicants = applicationService.getJobApplicants(selectedJob.getId());
            rounds = selectionService.getRoundsByJobId(selectedJob.getId());
        }

        request.setAttribute("company", company);
        request.setAttribute("companyJobs", companyJobs);
        request.setAttribute("selectedJob", selectedJob);
        request.setAttribute("applicants", applicants);
        request.setAttribute("rounds", rounds);

        if ("status_updated".equalsIgnoreCase(request.getParameter("success"))) {
            request.setAttribute("successMessage", "Candidate status updated successfully!");
        } else if ("offer_released".equalsIgnoreCase(request.getParameter("success"))) {
            request.setAttribute("successMessage", "🎉 Official Job Offer released to candidate!");
        }

        request.getRequestDispatcher("/WEB-INF/views/recruiter/applicants/list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();
        String jobId = request.getParameter("jobId");

        try {
            int applicationId = Integer.parseInt(request.getParameter("applicationId"));

            if ("/recruiter/applicants/offer".equals(path)) {
                selectionService.releaseOffer(applicationId);
                response.sendRedirect(request.getContextPath() + "/recruiter/applicants?jobId=" + jobId + "&success=offer_released");
                return;
            }

            if ("/recruiter/applicants/status".equals(path)) {
                ApplicationStatus status = ApplicationStatus.fromString(request.getParameter("status"));
                String reason = request.getParameter("rejectionReason");
                if (status == ApplicationStatus.REJECTED) {
                    selectionService.rejectCandidate(applicationId, reason != null ? reason : "Candidate not selected");
                } else {
                    selectionService.updateApplicationStatus(applicationId, status);
                }
                response.sendRedirect(request.getContextPath() + "/recruiter/applicants?jobId=" + jobId + "&success=status_updated");
            }

        } catch (Exception e) {
            logger.error("Error processing applicant action: {}", e.getMessage(), e);
            response.sendRedirect(request.getContextPath() + "/recruiter/applicants?jobId=" + jobId + "&error=" + e.getMessage());
        }
    }
}

package com.smarthire.controller;

import com.smarthire.model.Company;
import com.smarthire.model.JobPosting;
import com.smarthire.model.User;
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

import java.io.IOException;
import java.util.List;

/**
 * RecruiterDashboardServlet
 *
 * Gateway controller for authenticated Corporate Recruiter portal.
 * Loads live company details and active campus recruitment drives.
 */
@WebServlet(name = "RecruiterDashboardServlet", urlPatterns = {"/recruiter/dashboard"})
public class RecruiterDashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private CompanyService companyService;
    private JobService jobService;

    @Override
    public void init() throws ServletException {
        this.companyService = new CompanyServiceImpl();
        this.jobService = new JobServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        Company company = null;
        List<JobPosting> jobs = null;

        if (currentUser != null) {
            company = companyService.getCompanyByUserId(currentUser.getId());
            jobs = jobService.getJobsByCompany(company.getId());
        }

        long activeDrivesCount = (jobs != null) ? jobs.stream().filter(JobPosting::isOpenForApplication).count() : 0;
        int totalApplicants = (jobs != null) ? jobs.stream().mapToInt(JobPosting::getApplicantCount).sum() : 0;

        request.setAttribute("user", currentUser);
        request.setAttribute("company", company);
        request.setAttribute("jobs", jobs);
        request.setAttribute("activeDrivesCount", activeDrivesCount);
        request.setAttribute("totalApplicants", totalApplicants);

        request.getRequestDispatcher("/WEB-INF/views/recruiter/dashboard.jsp").forward(request, response);
    }
}

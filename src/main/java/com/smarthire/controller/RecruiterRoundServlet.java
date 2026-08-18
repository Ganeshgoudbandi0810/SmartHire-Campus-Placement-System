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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * RecruiterRoundServlet
 *
 * Controller managing the configuration of multi-stage interview rounds and candidate grading.
 */
@WebServlet(name = "RecruiterRoundServlet", urlPatterns = {
        "/recruiter/rounds",
        "/recruiter/rounds/create",
        "/recruiter/rounds/evaluate"
})
public class RecruiterRoundServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(RecruiterRoundServlet.class);
    private static final DateTimeFormatter DATETIME_LOCAL_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private JobService jobService;
    private ApplicationService applicationService;
    private SelectionService selectionService;

    @Override
    public void init() throws ServletException {
        this.jobService = new JobServiceImpl();
        this.applicationService = new ApplicationServiceImpl();
        this.selectionService = new SelectionServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String jobIdParam = request.getParameter("jobId");
        if (jobIdParam == null || jobIdParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/recruiter/jobs");
            return;
        }

        int jobId = Integer.parseInt(jobIdParam.trim());
        JobPosting job = jobService.getJobById(jobId);
        List<SelectionRound> rounds = selectionService.getRoundsByJobId(jobId);
        List<JobApplication> applicants = applicationService.getJobApplicants(jobId);

        request.setAttribute("job", job);
        request.setAttribute("rounds", rounds);
        request.setAttribute("applicants", applicants);

        if ("round_created".equalsIgnoreCase(request.getParameter("success"))) {
            request.setAttribute("successMessage", "Interview round scheduled successfully!");
        } else if ("evaluated".equalsIgnoreCase(request.getParameter("success"))) {
            request.setAttribute("successMessage", "Candidate scorecard recorded successfully!");
        }

        request.getRequestDispatcher("/WEB-INF/views/recruiter/rounds/manage.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();
        String jobId = request.getParameter("jobId");

        try {
            if ("/recruiter/rounds/create".equals(path)) {
                SelectionRound round = new SelectionRound();
                round.setJobId(Integer.parseInt(jobId));
                round.setRoundNumber(Integer.parseInt(request.getParameter("roundNumber")));
                round.setRoundName(request.getParameter("roundName"));
                round.setRoundType(RoundType.fromString(request.getParameter("roundType")));
                round.setDescription(request.getParameter("description"));
                round.setVenueOrLink(request.getParameter("venueOrLink"));

                String scheduledDateStr = request.getParameter("scheduledDate");
                if (scheduledDateStr != null && !scheduledDateStr.trim().isEmpty()) {
                    round.setScheduledDate(LocalDateTime.parse(scheduledDateStr.trim(), DATETIME_LOCAL_FORMAT));
                }

                selectionService.createRound(round);
                response.sendRedirect(request.getContextPath() + "/recruiter/rounds?jobId=" + jobId + "&success=round_created");
                return;
            }

            if ("/recruiter/rounds/evaluate".equals(path)) {
                int roundId = Integer.parseInt(request.getParameter("roundId"));
                int applicationId = Integer.parseInt(request.getParameter("applicationId"));
                RoundResultStatus status = RoundResultStatus.fromString(request.getParameter("status"));
                String score = request.getParameter("score");
                String feedback = request.getParameter("feedback");

                selectionService.recordCandidateRoundResult(roundId, applicationId, status, score, feedback);
                response.sendRedirect(request.getContextPath() + "/recruiter/rounds?jobId=" + jobId + "&success=evaluated");
            }

        } catch (Exception e) {
            logger.error("Error processing round action: {}", e.getMessage(), e);
            response.sendRedirect(request.getContextPath() + "/recruiter/rounds?jobId=" + jobId + "&error=" + e.getMessage());
        }
    }
}

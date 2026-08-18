package com.smarthire.controller;

import com.smarthire.model.JobApplication;
import com.smarthire.model.StudentProfile;
import com.smarthire.service.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

/**
 * DataExportServlet
 *
 * Controller streaming downloadable CSV spreadsheets for university records
 * and recruiter offline hiring panels.
 */
@WebServlet(name = "DataExportServlet", urlPatterns = {
        "/tpo/export/students",
        "/recruiter/export/applicants"
})
public class DataExportServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(DataExportServlet.class);

    private StudentService studentService;
    private ApplicationService applicationService;
    private AnalyticsService analyticsService;

    @Override
    public void init() throws ServletException {
        this.studentService = new StudentServiceImpl();
        this.applicationService = new ApplicationServiceImpl();
        this.analyticsService = new AnalyticsServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if ("/tpo/export/students".equals(path)) {
            List<StudentProfile> students = studentService.getAllStudents();
            String csvData = analyticsService.exportStudentsToCSV(students);

            String filename = "SmartHire_Students_Master_" + LocalDate.now() + ".csv";
            response.setContentType("text/csv; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

            try (PrintWriter writer = response.getWriter()) {
                writer.write(csvData);
                writer.flush();
            }
            logger.info("Exported {} student records to CSV", students.size());
            return;
        }

        if ("/recruiter/export/applicants".equals(path)) {
            String jobIdStr = request.getParameter("jobId");
            if (jobIdStr != null && !jobIdStr.trim().isEmpty()) {
                int jobId = Integer.parseInt(jobIdStr.trim());
                List<JobApplication> applicants = applicationService.getJobApplicants(jobId);
                String csvData = analyticsService.exportApplicantsToCSV(applicants);

                String filename = "SmartHire_Applicants_Job" + jobId + "_" + LocalDate.now() + ".csv";
                response.setContentType("text/csv; charset=UTF-8");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

                try (PrintWriter writer = response.getWriter()) {
                    writer.write(csvData);
                    writer.flush();
                }
                logger.info("Exported {} applicant records to CSV for job ID {}", applicants.size(), jobId);
            } else {
                response.sendRedirect(request.getContextPath() + "/recruiter/jobs");
            }
        }
    }
}

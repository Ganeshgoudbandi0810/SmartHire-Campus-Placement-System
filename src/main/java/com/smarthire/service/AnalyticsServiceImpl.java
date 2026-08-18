package com.smarthire.service;

import com.smarthire.dao.AnalyticsDAO;
import com.smarthire.dao.AnalyticsDAOImpl;
import com.smarthire.model.JobApplication;
import com.smarthire.model.PlacementAnalyticsDTO;
import com.smarthire.model.StudentProfile;

import java.util.List;

/**
 * AnalyticsServiceImpl
 *
 * Implements business metrics compilation and CSV export formatting.
 */
public class AnalyticsServiceImpl implements AnalyticsService {

    private final AnalyticsDAO analyticsDAO;

    public AnalyticsServiceImpl() {
        this.analyticsDAO = new AnalyticsDAOImpl();
    }

    public AnalyticsServiceImpl(AnalyticsDAO analyticsDAO) {
        this.analyticsDAO = analyticsDAO;
    }

    @Override
    public PlacementAnalyticsDTO getPlacementAnalytics() {
        return analyticsDAO.getPlacementAnalytics();
    }

    @Override
    public String exportStudentsToCSV(List<StudentProfile> students) {
        StringBuilder sb = new StringBuilder();
        sb.append("Student ID,Roll Number,First Name,Last Name,Email,Department,Graduation Year,CGPA,10th Marks,12th Marks,Active Backlogs,Verified\n");

        if (students != null) {
            for (StudentProfile s : students) {
                sb.append(s.getId()).append(",")
                  .append(escape(s.getRollNumber())).append(",")
                  .append(escape(s.getFirstName())).append(",")
                  .append(escape(s.getLastName())).append(",")
                  .append(escape(s.getUserEmail())).append(",")
                  .append(escape(s.getDepartment())).append(",")
                  .append(s.getGraduationYear()).append(",")
                  .append(s.getCgpa()).append(",")
                  .append(s.getTenthPercentage()).append(",")
                  .append(s.getTwelfthPercentage()).append(",")
                  .append(s.getActiveBacklogs()).append(",")
                  .append(s.isVerified() ? "YES" : "NO").append("\n");
            }
        }
        return sb.toString();
    }

    @Override
    public String exportApplicantsToCSV(List<JobApplication> applicants) {
        StringBuilder sb = new StringBuilder();
        sb.append("Application ID,Roll Number,Candidate Name,Email,Department,CGPA,Job Title,Company,Package LPA,Applied Date,Status,Remarks\n");

        if (applicants != null) {
            for (JobApplication a : applicants) {
                sb.append(a.getId()).append(",")
                  .append(escape(a.getRollNumber())).append(",")
                  .append(escape(a.getStudentFullName())).append(",")
                  .append(escape(a.getStudentEmail())).append(",")
                  .append(escape(a.getStudentDepartment())).append(",")
                  .append(a.getStudentCgpa()).append(",")
                  .append(escape(a.getJobTitle())).append(",")
                  .append(escape(a.getCompanyName())).append(",")
                  .append(a.getPackageLpa()).append(",")
                  .append(a.getAppliedAt()).append(",")
                  .append(escape(a.getCurrentStatus().getDisplayName())).append(",")
                  .append(escape(a.getRejectionReason())).append("\n");
            }
        }
        return sb.toString();
    }

    private String escape(String val) {
        if (val == null) {
            return "";
        }
        String clean = val.replace("\"", "\"\"");
        if (clean.contains(",") || clean.contains("\n") || clean.contains("\r") || clean.contains("\"")) {
            return "\"" + clean + "\"";
        }
        return clean;
    }
}

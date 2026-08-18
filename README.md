# 🎓 SmartHire — Campus Placement & Recruitment Management System

[![Java 21](https://img.shields.io/badge/Java-21%20LTS-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![Jakarta Servlet](https://img.shields.io/badge/Jakarta%20Servlet-6.0-blue.svg)](https://jakarta.ee/specifications/servlet/6.0/)
[![Jakarta JSP](https://img.shields.io/badge/Jakarta%20JSP-3.1-blue.svg)](https://jakarta.ee/specifications/pages/3.1/)
[![HikariCP](https://img.shields.io/badge/HikariCP-5.1.0-green.svg)](https://github.com/brettwooldridge/HikariCP)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-4479A1.svg)](https://www.mysql.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

An enterprise-grade, full-stack **Campus Placement & Recruitment Management Platform** designed for University Placement Cells (TPO), Corporate Recruiters, and Student Candidates.

Built strictly with **Pure Java 21 LTS**, **Jakarta Servlet 6.0**, **Jakarta JSP 3.1 / JSTL 3.0**, **HikariCP Connection Pool**, and **MySQL 8.0+** using a robust 3-tier **MVC + Service + DAO** architecture — **without any Spring framework or Spring Boot dependencies**.

---

## 🏛️ System Architecture

```
                                  [ Browser / Client ]
                                           │
                                           ▼ (HTTP / HTTPS)
                            ┌───────────────────────────────┐
                            │      AuthenticationFilter     │ (RBAC & Cache Control)
                            └──────────────┬────────────────┘
                                           │
                    ┌──────────────────────┼──────────────────────┐
                    │                      │                      │
                    ▼                      ▼                      ▼
         [ Student Servlets ]       [ TPO Servlets ]     [ Recruiter Servlets ]
         • StudentDashboard         • TpoDashboard       • RecruiterDashboard
         • StudentProfile           • TpoManagement      • RecruiterJob
         • StudentDrives            • TpoAnalytics       • RecruiterRound
         • StudentApplications      • DataExport         • RecruiterApplicant
                    │                      │                      │
                    └──────────────────────┼──────────────────────┘
                                           │
                                           ▼
                                ┌─────────────────────┐
                                │    Service Layer    │
                                │ • AuthService       │
                                │ • StudentService    │
                                │ • CompanyService    │
                                │ • JobService        │
                                │ • EligibilityEngine │
                                │ • ApplicationService│
                                │ • SelectionService  │
                                │ • AnalyticsService  │
                                └──────────┬──────────┘
                                           │
                                           ▼
                                ┌─────────────────────┐
                                │      DAO Layer      │
                                │ • UserDAO           │
                                │ • StudentDAO        │
                                │ • CompanyDAO        │
                                │ • JobDAO            │
                                │ • JobApplicationDAO │
                                │ • RoundDAO          │
                                │ • AnalyticsDAO      │
                                └──────────┬──────────┘
                                           │
                                           ▼
                            ┌───────────────────────────────┐
                            │      DBConnectionManager      │ (HikariCP Pool)
                            └──────────────┬────────────────┘
                                           │
                                           ▼ (JDBC)
                                [ MySQL 8.0+ Database ]
```

---

## ✨ Core Features

### 1. 👨‍🎓 Student Candidate Portal
* **Academic Profile Completion**: Live profile strength indicator ($\ge 80\%$ required for placement eligibility).
* **Resume Management**: Multipart PDF resume upload and secure inline streaming.
* **Automated Eligibility Checking**: Instant real-time compatibility checks against company cutoff criteria before applying.
* **1-Click Application Workflow**: Duplicate-safe, atomic application submission.
* **Live Selection Status Tracker**: Round-by-round progress tracking (`APPLIED` $\rightarrow$ `SHORTLISTED` $\rightarrow$ `IN_PROCESS` $\rightarrow$ `OFFERED`).

### 2. 👨‍💼 Placement Officer (TPO Admin) Workspace
* **Candidate Verification Roster**: Audit CGPA, 10th/12th marks, and active backlogs against university transcripts with 1-click approval/revocation.
* **Corporate Partner Directory**: Onboard, verify, and manage corporate recruiter organizations.
* **Campus Placement Drives Oversight**: Comprehensive visibility into all company listings, cutoffs, and applicant metrics.
* **Institutional Placement Analytics**: Live NIRF/NAAC accreditation dashboard displaying placement rates, highest/average packages, and departmental breakdowns.
* **Master CSV Data Export**: Stream RFC-4180 compliant CSV master rosters.

### 3. 💼 Corporate Recruiter Hub
* **Placement Drive Publisher**: Configure compensation packages (LPA), job locations, employment types, and multi-dimensional academic cutoffs (Min CGPA, Backlogs, Eligible Branches).
* **Candidate Evaluation Roster**: Review applicant lists, view PDF resumes inline, and update statuses.
* **Multi-Stage Round Scheduling**: Schedule sequential interview rounds (Online Coding Test, Technical Interview, HR Interview) with venue/meeting links.
* **Candidate Scorecards**: Record numerical test marks and interviewer qualitative feedback.
* **Formal Offer Dispatch**: Issue official job offers (`OFFERED`) that instantly reflect on candidate dashboards.

---

## ⚡ Automated Eligibility Engine Rules

The pure algorithmic `EligibilityEngine` evaluates 7 multi-dimensional criteria simultaneously:

| Rule | Description | Condition |
| :--- | :--- | :--- |
| **1. Profile Strength** | Profile completion $\ge 80\%$ and attached PDF resume | `completionPercentage >= 80 && resume != null` |
| **2. CGPA Cutoff** | Candidate CGPA meets minimum job cutoff | `student.cgpa >= job.minCgpa` |
| **3. Backlogs Limit** | Active standing backlogs within allowed threshold | `student.activeBacklogs <= job.maxBacklogsAllowed` |
| **4. Branch Match** | Candidate's academic department is eligible | `job.eligibleBranches.contains(student.department)` |
| **5. Secondary Marks** | 10th standard percentage meets cutoff | `student.tenthPercentage >= job.minTenthPercentage` |
| **6. Higher Secondary** | 12th / Diploma percentage meets cutoff | `student.twelfthPercentage >= job.minTwelfthPercentage` |
| **7. Target Batch** | Candidate graduation year matches target batch | `student.graduationYear == job.graduationYear` |

---

## 🔑 Pre-Seeded Test Credentials

| Role | Email Address | Password | Direct Portal Route |
| :--- | :--- | :--- | :--- |
| **Placement Officer (TPO Admin)** | `admin@smarthire.edu` | `Password@123` | `/tpo/dashboard` |
| **Corporate Recruiter** | `recruiter@techcorp.com` | `Password@123` | `/recruiter/dashboard` |
| **Student Candidate** | `rahul.verma@smarthire.edu` | `Password@123` | `/student/dashboard` |

---

## 🚀 Getting Started & Local Setup

### Prerequisites
* **Java Development Kit (JDK)**: Version 21 LTS
* **Servlet Container**: Apache Tomcat 10.1.x (Jakarta Servlet 6.0 compatible)
* **Database**: MySQL Server 8.0+
* **Build Tool**: Apache Maven 3.9+

### 1. Clone the Repository
```bash
git clone https://github.com/Ganeshgoudbandi0810/SmartHire-Campus-Placement-System.git
cd SmartHire-Campus-Placement-System
```

### 2. Configure Database
1. Create a MySQL database and load the schema:
```bash
mysql -u root -p < src/main/resources/schema.sql
```
2. Copy `db.properties.example` to `src/main/resources/db.properties` and set your local credentials:
```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/smarthire_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&characterEncoding=UTF-8
db.username=root
db.password=YOUR_MYSQL_PASSWORD
```

### 3. Build & Package
```bash
mvn clean package
```
This generates `target/smarthire.war`.

### 4. Deploy & Run on Apache Tomcat 10.1.x
1. Copy `target/smarthire.war` into Tomcat's `webapps/` folder.
2. Start Tomcat:
```bash
bin/catalina.bat run   # Windows
# or
bin/catalina.sh run    # Linux / macOS
```
3. Open your browser at: **`http://localhost:8080/smarthire/`**

---

## 🛠️ Technology Stack

| Layer | Technology |
| :--- | :--- |
| **Backend Runtime** | Java 21 LTS |
| **Server & APIs** | Jakarta Servlet 6.0, Apache Tomcat 10.1.x |
| **Presentation / Views** | Jakarta JSP 3.1, JSTL 3.0, Modern CSS Design System |
| **Database & Pooling** | MySQL 8.0+, HikariCP 5.1.0 Connection Pool |
| **Security & Hashing** | jBCrypt 0.4 (Cost Factor 10), HTTP Session RBAC Filters |
| **JSON & Utilities** | Google Gson 2.10.1, SLF4J Logging |

---

## 📄 License
This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

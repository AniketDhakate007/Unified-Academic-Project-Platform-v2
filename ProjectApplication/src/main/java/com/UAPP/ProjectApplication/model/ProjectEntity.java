package com.UAPP.ProjectApplication.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "projects")
public class ProjectEntity {
    @Id
    private String id;
    private String title;
    private String description;
    private List<String> studentEmails;
    private String guideName;
    private LocalDate startDate;
    private LocalDate submissionDate;
    private String githubRepo;
    private String ownerUserId;
    private String pdfFileId;

    public ProjectEntity() {}

    // getters & setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getStudentEmails() { return studentEmails; }
    public void setStudentEmails(List<String> studentEmails) { this.studentEmails = studentEmails; }

    public String getGuideName() { return guideName; }
    public void setGuideName(String guideName) { this.guideName = guideName; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(LocalDate submissionDate) { this.submissionDate = submissionDate; }

    public String getGithubRepo() { return githubRepo; }
    public void setGithubRepo(String githubRepo) { this.githubRepo = githubRepo; }

    public String getOwnerUserId() { return ownerUserId; }
    public void setOwnerUserId(String ownerUserId) { this.ownerUserId = ownerUserId; }

    public String getPdfFileId() { return pdfFileId; }
    public void setPdfFileId(String pdfFileId) { this.pdfFileId = pdfFileId; }
}

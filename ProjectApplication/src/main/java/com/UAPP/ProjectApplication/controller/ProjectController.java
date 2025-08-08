package com.UAPP.ProjectApplication.controller;

import com.UAPP.ProjectApplication.model.ProjectEntity;
import com.UAPP.ProjectApplication.repository.ProjectRepository;
import com.UAPP.ProjectApplication.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectRepository projectRepo;
    private final FileService fileService;

    @Autowired
    public ProjectController(ProjectRepository projectRepo, FileService fileService) {
        this.projectRepo = projectRepo;
        this.fileService = fileService;
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submit(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam List<String> studentEmails,
            @RequestParam String guideName,
            @RequestParam String startDate,
            @RequestParam String submissionDate,
            @RequestParam String githubRepo,
            @RequestPart(required = false) MultipartFile pdf,
            Authentication auth
    ) throws IOException {
        String ownerId = (String) auth.getPrincipal();
        ProjectEntity p = new ProjectEntity();
        p.setTitle(title);
        p.setDescription(description);
        p.setStudentEmails(studentEmails);
        p.setGuideName(guideName);
        p.setStartDate(LocalDate.parse(startDate));
        p.setSubmissionDate(LocalDate.parse(submissionDate));
        p.setGithubRepo(githubRepo);
        p.setOwnerUserId(ownerId);

        if (pdf != null && !pdf.isEmpty()) {
            String fileId = fileService.storeFile(pdf);
            p.setPdfFileId(fileId);
        }
        projectRepo.save(p);
        return ResponseEntity.ok(p);
    }

    @GetMapping("/mine")
    public List<ProjectEntity> myProjects(Authentication auth) {
        String ownerId = (String) auth.getPrincipal();
        return projectRepo.findByOwnerUserId(ownerId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable String id, Authentication auth) {
        Optional<ProjectEntity> opt = projectRepo.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        ProjectEntity p = opt.get();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        String userId = (String) auth.getPrincipal();
        if (!isAdmin && !p.getOwnerUserId().equals(userId)) return ResponseEntity.status(403).body("Forbidden");
        return ResponseEntity.ok(p);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<?> download(@PathVariable String id, Authentication auth) throws IOException {
        Optional<ProjectEntity> opt = projectRepo.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        ProjectEntity p = opt.get();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        String userId = (String) auth.getPrincipal();
        if (!isAdmin && !p.getOwnerUserId().equals(userId)) return ResponseEntity.status(403).body("Forbidden");
        if (p.getPdfFileId() == null) return ResponseEntity.notFound().build();
        GridFsResource res = fileService.getResource(p.getPdfFileId());
        if (res == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + res.getFilename() + "\"")
                .contentLength(res.contentLength())
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(res.getInputStream().readAllBytes());
    }

    @PutMapping("/{id}/edit")
    public ResponseEntity<?> edit(
            @PathVariable String id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestPart(required = false) MultipartFile pdf,
            Authentication auth
    ) throws IOException {
        Optional<ProjectEntity> opt = projectRepo.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        ProjectEntity p = opt.get();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        String userId = (String) auth.getPrincipal();
        if (!isAdmin && !p.getOwnerUserId().equals(userId)) return ResponseEntity.status(403).body("Forbidden");

        if (title != null) p.setTitle(title);
        if (description != null) p.setDescription(description);
        if (pdf != null && !pdf.isEmpty()) {
            if (p.getPdfFileId() != null) fileService.deleteById(p.getPdfFileId());
            p.setPdfFileId(fileService.storeFile(pdf));
        }
        projectRepo.save(p);
        return ResponseEntity.ok(p);
    }

    @GetMapping("/all")
    public ResponseEntity<?> allProjects(Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) return ResponseEntity.status(403).body("Forbidden");
        return ResponseEntity.ok(projectRepo.findAll());
    }
}
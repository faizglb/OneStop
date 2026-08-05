package com.acea.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectRequest {

    @NotBlank(message = "Project title is required")
    @Size(max = 200, message = "Project title cannot exceed 200 characters")
    private String projectTitle;

    @NotBlank(message = "Project description is required")
    private String description;

    @NotEmpty(message = "Tech stack list cannot be empty")
    private List<String> techStack;

    @Size(max = 500, message = "GitHub link cannot exceed 500 characters")
    @URL(message = "Invalid GitHub URL")
    private String githubLink;

    @Size(max = 500, message = "Live link cannot exceed 500 characters")
    @URL(message = "Invalid live link URL")
    private String liveLink;
}

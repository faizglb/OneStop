package com.acea.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterestRequest {

    @NotBlank(message = "Interest name is required")
    @Size(max = 100, message = "Interest name cannot exceed 100 characters")
    private String interestName;
}

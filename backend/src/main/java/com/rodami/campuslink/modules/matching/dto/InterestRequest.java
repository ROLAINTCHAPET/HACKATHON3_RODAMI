package com.rodami.campuslink.modules.matching.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterestRequest {

    @NotBlank(message = "Le tag est obligatoire")
    private String tag;

    private String category;
}

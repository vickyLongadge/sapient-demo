package com.ps.itp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Document(value = "itpAssessmentTemplates")
public class ItpAssessments {


    private String assessmentYear;

    private ITPStatus status;

    private LocalDateTime assessmentDateTime;

    private AssessmentType assessmentType;

    private String itpAssessorOracleId;

    private String itpAssessorName;

    private Double overallScore=0.0;

    private List<Sections> sections = new ArrayList<>();

    private List<Sections> itpBase = new ArrayList<>();

    private List<Remarks> overallRemarks = new ArrayList<>();

    private int weightForEachSection;

    private int weightForOverAllSection;
}

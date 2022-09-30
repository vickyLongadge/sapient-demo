package com.ps.itp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class NewITPAssessment {

    private String employeeOracleId;

    private String assessmentYear;

    private String itpAssessorOracleId;

    private String itpAssessorName;

    private AssessmentType assessmentType;
}

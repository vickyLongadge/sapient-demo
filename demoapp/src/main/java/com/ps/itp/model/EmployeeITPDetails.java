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
@Document
public class EmployeeITPDetails {

    @Id
    private String id;

    private EmployeeDetails employeeDetails;

    private ITPStatus itpStatus;

    private Double itpScore = 0.0;

    private LocalDateTime lastUpdatedDateTime;

    private List<ItpAssessments> itpAssessments = new ArrayList<>();

    private List<String> sharedOracleIds;

 }

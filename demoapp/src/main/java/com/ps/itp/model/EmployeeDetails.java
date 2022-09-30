package com.ps.itp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDetails {

    @Id
    private String id;

    private String name;

    private String oracleId;

    private String currentDesignation;

    private String careerStage;

    private String domain;

    private String reportingManager;

    private String emailId;

    private String mobileNumber;

    private String joiningDate;

    private PidDetails pidDetails;

    private List<String> tags;

}

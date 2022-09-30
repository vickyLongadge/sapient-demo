package com.ps.itp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PidDetails {

    private String pid;

    private LocalDateTime startDate = LocalDateTime.now();

    private LocalDateTime endDate = LocalDateTime.now();

    private String accountName;

    private String projectName;

    private boolean active;
}

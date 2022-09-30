package com.ps.itp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Remarks {

    private String comment;

    private String commentedBy;

    private LocalDateTime createdAt = LocalDateTime.now();
}

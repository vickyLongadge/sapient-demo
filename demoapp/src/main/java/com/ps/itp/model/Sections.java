package com.ps.itp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Sections {

    private String sectionCategory;

    private String details;

    private Double score = 0.0;

    private String level;

    private String imageSrc;

    private List<String> options;

    private List<Remarks> remarks;

}

package com.ps.itp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(value = "employeeDesignation")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeDesignation {
    @Id
    private String id;
    private String name;
    private Integer level;

}

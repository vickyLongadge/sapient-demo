package com.ps.itp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public enum AssessmentType {
    QE("QE"),
    ENGINEERING("ENGINEERING"),
    PRODUCTMANAGER("PRODUCTMANAGER");

    private String value;

}

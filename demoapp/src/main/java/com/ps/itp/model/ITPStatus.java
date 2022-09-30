package com.ps.itp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
public enum ITPStatus {
    COMPLETED("COMPLETED"),
    STARTED("STARTED"),
    PENDING("PENDING"),
    SUBMITTED("SUBMITTED"),
    NOT_STARTED("NOT STARTED");

    private String value;
}

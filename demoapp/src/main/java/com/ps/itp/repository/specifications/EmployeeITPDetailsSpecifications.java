package com.ps.itp.repository.specifications;

import com.ps.itp.model.EmployeeITPDetails;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;


@Component
public class EmployeeITPDetailsSpecifications {

    public static Specification<EmployeeITPDetails> equalsName(String searchByName ) {
        if(StringUtils.isEmpty(searchByName)) {
            return null;
        }

        return  (root, query, cb) -> {
            return cb.in(root.get("employeeDetails").get("name").in(searchByName));
        };
    }

    public static Specification<EmployeeITPDetails> equalsSharedOracleIds(List<String> sharedOracleIds ) {
        if(CollectionUtils.isEmpty(sharedOracleIds)) {
            return null;
        }

        return  (root, query, cb) -> {
            return cb.in(root.get("sharedOracleIds").in(sharedOracleIds));
        };
    }

    public static Specification<EmployeeITPDetails> equalsReportingManager(String managerOracleId ) {
        if(StringUtils.isEmpty(managerOracleId)) {
            return null;
        }

        return  (root, query, cb) -> {
            return cb.equal(root.get("employeeDetails").get("reportingManager"), managerOracleId);
        };
    }

    public static Specification<EmployeeITPDetails> equalsAssessorOracleId(String assessorOracleId ) {
        if(StringUtils.isEmpty(assessorOracleId)) {
            return null;
        }

        return  (root, query, cb) -> {
            return cb.equal(root.get("itpAssessments").get("itpAssessorOracleId"), assessorOracleId);
        };
    }

}

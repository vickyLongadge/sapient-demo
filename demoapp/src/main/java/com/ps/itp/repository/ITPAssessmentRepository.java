package com.ps.itp.repository;

import com.ps.itp.model.EmployeeITPDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ITPAssessmentRepository extends MongoRepository<EmployeeITPDetails, String>  {

    Optional<EmployeeITPDetails> findByEmployeeDetails_oracleId(String employeeOracleId);


    Page<EmployeeITPDetails> findAllByEmployeeDetails_reportingManagerOrItpAssessments_itpAssessorOracleId(String managerOracleId,String assessorOracleId, Pageable pageable);

    Page<EmployeeITPDetails> findAllBySharedOracleIdsOrEmployeeDetails_reportingManagerOrItpAssessments_itpAssessorOracleId(List<String> sharedOracleIds, String managerOracleId,String assessorOracleId, Pageable pageable);

    Page<EmployeeITPDetails> findAllBySharedOracleIdsOrEmployeeDetails_reportingManagerOrItpAssessments_itpAssessorOracleIdAndEmployeeDetails_name(
            List<String> sharedOracleIds, String managerOracleId,String assessorOracleId, String name,  Pageable pageable);

    EmployeeITPDetails findByIdAndItpAssessments_assessmentYear(String id, String assessmentYear);
}

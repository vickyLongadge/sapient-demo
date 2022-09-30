package com.ps.itp.repository;

import com.ps.itp.model.EmployeeDetails;
import com.ps.itp.model.EmployeeITPDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeDetailsRepository extends MongoRepository<EmployeeDetails, String> {

    Optional<EmployeeDetails> findByOracleId(String oracleId);

    List<EmployeeDetails> findAllByNameOrOracleId(String name, String oracleId);

    Optional<EmployeeDetails> findByEmailId(String employeeEmailId);

    List<EmployeeDetails> findAllByCareerStageIn(List<String> careerStage);

}

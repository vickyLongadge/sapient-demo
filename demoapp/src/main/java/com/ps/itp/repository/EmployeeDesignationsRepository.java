package com.ps.itp.repository;

import com.ps.itp.model.EmployeeDesignation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeDesignationsRepository
        extends MongoRepository<EmployeeDesignation, String> {

    List<EmployeeDesignation> findAllByLevelGreaterThanEqual(int level);

    List<EmployeeDesignation> findAllByLevelBetween(Integer managerLevel, Integer employeeLevel);

    Optional<EmployeeDesignation> findByName(String designation);


}

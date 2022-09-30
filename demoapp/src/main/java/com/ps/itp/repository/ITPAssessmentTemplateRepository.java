package com.ps.itp.repository;

import com.ps.itp.model.EmployeeITPDetails;
import com.ps.itp.model.ItpAssessments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ITPAssessmentTemplateRepository extends MongoRepository<ItpAssessments, String> {

    ItpAssessments findByAssessmentType(String assessmentType);
}

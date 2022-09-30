package com.ps.itp.service;

import com.ps.itp.exception.EmployeeNotFoundException;
import com.ps.itp.model.*;
import com.ps.itp.repository.EmployeeDetailsRepository;
import com.ps.itp.repository.ITPAssessmentRepository;
import com.ps.itp.repository.ITPAssessmentTemplateRepository;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class ITPService {
    @Autowired
    private ITPAssessmentRepository itpAssessmentRepository;

    @Autowired
    private EmployeeDetailsRepository employeeDetailsRepository;

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @Autowired
    private ITPAssessmentTemplateRepository assessmentTemplateRepository;

    public EmployeeITPDetails startNewItpAssessment(NewITPAssessment newITPAssessment) {
        return itpAssessmentRepository.save(createNewITPAssessment(newITPAssessment));
    }

    public Page<EmployeeITPDetails> getEmployeeItpDetails(Integer pageNumber, Integer size) {
        SimpleKeycloakAccount auth = (SimpleKeycloakAccount) authenticationFacade.getAuthentication().getDetails();
        String emailId = auth.getKeycloakSecurityContext().getToken().getEmail();
        EmployeeDetails employeeDetails = employeeDetailsRepository.findByEmailId(emailId).orElseThrow(() -> new EmployeeNotFoundException());


        return itpAssessmentRepository.findAllBySharedOracleIdsOrEmployeeDetails_reportingManagerOrItpAssessments_itpAssessorOracleId(
                Arrays.asList(employeeDetails.getOracleId()),
                employeeDetails.getOracleId(),
                employeeDetails.getOracleId(),
                PageRequest.of(pageNumber, size, Sort.by("lastUpdatedDateTime").descending()));
    }


    public Page<EmployeeITPDetails> searchEmployeeItpDetails(String searchText, Integer pageNumber, Integer size) {
        SimpleKeycloakAccount auth = (SimpleKeycloakAccount) authenticationFacade.getAuthentication().getDetails();
        String emailId = auth.getKeycloakSecurityContext().getToken().getEmail();
        EmployeeDetails employeeDetails = employeeDetailsRepository.findByEmailId(emailId).orElseThrow(() -> new EmployeeNotFoundException());

        return itpAssessmentRepository.findAllBySharedOracleIdsOrEmployeeDetails_reportingManagerOrItpAssessments_itpAssessorOracleIdAndEmployeeDetails_name(
                Arrays.asList(employeeDetails.getOracleId()),
                employeeDetails.getOracleId(),
                employeeDetails.getOracleId(),
                searchText,
                PageRequest.of(pageNumber, size, Sort.by("lastUpdatedDateTime").descending()));


    }


    public EmployeeITPDetails updateEmployeeITPDetails(EmployeeITPDetails employeeITPDetails) {
        updateOverallScore(employeeITPDetails);
        return itpAssessmentRepository.save(employeeITPDetails);
    }


    public EmployeeITPDetails getEmployeeItpDetails(String year, String id) {
        SimpleKeycloakAccount auth = (SimpleKeycloakAccount) authenticationFacade.getAuthentication().getDetails();
        String emailId = auth.getKeycloakSecurityContext().getToken().getEmail();
        EmployeeDetails employeeDetails = employeeDetailsRepository.findByEmailId(emailId).orElseThrow(() -> new EmployeeNotFoundException());
        return itpAssessmentRepository.findByIdAndItpAssessments_assessmentYear(id, year);
    }

    private void calculateITPOutcome(ItpAssessments itpAssessment, Double overallScore, Integer levelScore) {
        //Top Performer
        if (levelScore >= 16) {
            itpAssessment.getItpBase().get(8).setScore(2d);

        }
        //Valued Contributor
        if ((levelScore <= 15 && levelScore >= 12) || (overallScore <= 2 && overallScore >= 1)) {
            itpAssessment.getItpBase().get(8).setScore(1d);
        }

        //Manage Performance
        checkForManagePerformance(itpAssessment, levelScore);
    }

    private void checkForManagePerformance(ItpAssessments itpAssessments, Integer levelScore) {
        if (levelScore <= 11 ||
                itpAssessments.getItpBase().get(0).getLevel().contentEquals("0") || // Performance
                itpAssessments.getItpBase().get(0).getLevel().contentEquals("1") || // Performance
                itpAssessments.getItpBase().get(1).getLevel().contentEquals("0") || // Required Skills
                itpAssessments.getItpBase().get(3).getLevel().contentEquals("0")) { //Culture
            itpAssessments.getItpBase().get(8).setScore(0d);

        }
    }

    private Integer collectLevelScore(ItpAssessments itpAssessments) {
        return itpAssessments.getItpBase().stream()
                .filter(withCriticalSectionsPredicate())
                .map(Sections::getLevel).map(level -> {
                    return Integer.parseInt(level) >= 0 ? Integer.parseInt(level)+1 : 0;
                })
                .collect(Collectors.summingInt(Integer::intValue));
    }

    private Double getOverallScore(ItpAssessments itpAssessments) {
        return itpAssessments.getSections().stream()
                .map(Sections::getScore).map(score -> {
                    return score >= 0 ? score : 0;
                })
                .collect(Collectors.summingDouble(Double::doubleValue));
    }

    private Predicate<Sections> withCriticalSectionsPredicate() {
        return sections -> sections.getSectionCategory().equals("Performance") ||
                sections.getSectionCategory().equals("Required Skill Capability") ||
                sections.getSectionCategory().equals("Expected Skill Capability") ||
                sections.getSectionCategory().equals("Culture") ||
                sections.getSectionCategory().equals("Growth");
    }

    private EmployeeITPDetails createNewITPAssessment(NewITPAssessment newITPAssessment) {
        EmployeeITPDetails employeeITPDetails =
                itpAssessmentRepository
                        .findByEmployeeDetails_oracleId(newITPAssessment.getEmployeeOracleId())
                        .orElse(
                                EmployeeITPDetails.builder()
                                        .employeeDetails(
                                                employeeDetailsRepository.findByOracleId(
                                                        newITPAssessment.getEmployeeOracleId()).orElseThrow(() -> new EmployeeNotFoundException()))
                                        .itpAssessments(new ArrayList<>())
                                        .build());
        employeeITPDetails.setItpStatus(ITPStatus.PENDING);
        employeeITPDetails.setLastUpdatedDateTime(LocalDateTime.now());

        employeeITPDetails.getItpAssessments().add(getITPAssessments(newITPAssessment));

        return employeeITPDetails;
    }

    private ItpAssessments getITPAssessments(NewITPAssessment newITPAssessment) {
        ItpAssessments itpAssessment = assessmentTemplateRepository.findByAssessmentType(newITPAssessment.getAssessmentType().name());
        itpAssessment.setAssessmentYear(newITPAssessment.getAssessmentYear());
        itpAssessment.setItpAssessorOracleId(newITPAssessment.getEmployeeOracleId());
        itpAssessment.setItpAssessorOracleId(newITPAssessment.getItpAssessorOracleId());
        itpAssessment.setItpAssessorName(newITPAssessment.getItpAssessorName());
        itpAssessment.setAssessmentDateTime(LocalDateTime.now());
        return itpAssessment;
    }

    private void updateOverallScore(EmployeeITPDetails employeeITPDetails) {
        employeeITPDetails.getItpAssessments().stream()
//            .filter(itpAssessment -> itpAssessment
//            .getStatus().equals(ITPStatus.PENDING) || itpAssessment.getStatus().equals(ITPStatus.STARTED))
                .forEach(
                        itpAssessments -> {
                            Double overallScore = getOverallScore(itpAssessments);
                            Integer levelScore = collectLevelScore(itpAssessments);

                            calculateITPOutcome(itpAssessments, overallScore, levelScore);

                            itpAssessments.setOverallScore(overallScore);
                            employeeITPDetails.setItpScore(overallScore);
                        });
    }

//
//  private ItpAssessments getNewITPAssessments(NewITPAssessment newITPAssessment) {
//    return ItpAssessments.builder()
//        .itpAssessorOracleId(newITPAssessment.getItpAssessor())
//        .assessmentDateTime(LocalDateTime.now())
//        .status(ITPStatus.STARTED)
//        .assessmentYear(newITPAssessment.getAssessmentYear())
//        .overallScore(0.0)
//        .assessmentType(newITPAssessment.getAssessmentType())
//        .sections(getSections(newITPAssessment.getAssessmentType()))
//        .overallRemarks(new ArrayList<>())
//        .build();
//  }
//
//  private List<Sections> getSections(AssessmentType assessmentType) {
//    if (assessmentType.equals(AssessmentType.QE)) {
//      return Arrays.asList(
//          getCloudSection(),
//          getBDDSection(),
//          getCICDSection(),
//          getSecuritySection(),
//          getTDDSection());
//    } else {
//      return Arrays.asList(
//          getCloudSection(),
//          getBDDSection(),
//          getCICDSection(),
//          getSOLIDSection(),
//          getMicroservicesSection(),
//          getSecuritySection(),
//          getTDDSection());
//    }
//  }
//
//  private Sections getCloudSection() {
//    return Sections.builder()
//        .sectionCategory("Cloud")
//        .details("Hands-on experience of cloud infra")
//        .score(0.0)
//        .level("")
//        .remarks(new ArrayList<>())
//        .build();
//  }
//
//  private Sections getSecuritySection() {
//    return Sections.builder()
//        .sectionCategory("Security")
//        .details("Hands-on experience of Security")
//        .score(0.0)
//        .level("")
//        .remarks(new ArrayList<>())
//        .build();
//  }
//
//  private Sections getCICDSection() {
//    return Sections.builder()
//        .sectionCategory("CI / CD")
//        .details("Hands-on experience of CI/CD")
//        .score(0.0)
//        .level("")
//        .remarks(new ArrayList<>())
//        .build();
//  }
//
//  private Sections getTDDSection() {
//    return Sections.builder()
//        .sectionCategory("TDD")
//        .details("Hands-on experience of TDD ")
//        .remarks(new ArrayList<>())
//        .score(0.0)
//        .level("")
//        .build();
//  }
//
//  private Sections getBDDSection() {
//    return Sections.builder()
//        .sectionCategory("BDD")
//        .details("Hands-on experience of BDD ")
//        .score(0.0)
//        .level("")
//        .remarks(new ArrayList<>())
//        .build();
//  }
//
//  private Sections getMicroservicesSection() {
//    return Sections.builder()
//        .sectionCategory("Microservices")
//        .details("Hands-on experience of Microservices ")
//        .score(0.0)
//        .level("")
//        .remarks(new ArrayList<>())
//        .build();
//  }
//
//  private Sections getSOLIDSection() {
//    return Sections.builder()
//        .sectionCategory("Solid Principles & Design Patterns")
//        .details("Hands-on experience of Microservices ")
//        .score(0.0)
//        .level("")
//        .remarks(new ArrayList<>())
//        .build();
//  }
}

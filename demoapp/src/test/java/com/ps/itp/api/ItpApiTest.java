package com.ps.itp.api;

import com.ps.itp.model.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ItpApiTest {

  @Autowired TestRestTemplate testRestTemplate;

  @Test
  public void should_be_able_create_employee_documents() {
    ResponseEntity<EmployeeDetails> responseEntity =
        testRestTemplate.postForEntity(
            "/itp/create/employee", getEmployeeDetailsPojo(), EmployeeDetails.class);
    Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(responseEntity.hasBody()).isEqualTo(true);
 //   Assertions.assertThat(responseEntity.getBody().getName()).isEqualTo("Xyz");
  }

  @Test
  public void should_be_able_get_employee_documents() {
    ResponseEntity<List<EmployeeITPDetails>> responseEntity = getEmployeeItpDetailsRequest();
    Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(responseEntity.hasBody()).isEqualTo(true);
    Assertions.assertThat(
            responseEntity.getBody().stream()
                .filter(
                    employeeITPDetails ->
                        employeeITPDetails.getEmployeeDetails().getName().equals("Xyz"))
                    .findFirst())

        .isNotEmpty();
  }

  @Test
  public void should_be_able_to_create_itp_assessment() {
    ResponseEntity<EmployeeITPDetails> responseEntity =
        testRestTemplate.postForEntity(
            "/itp/start/employee/assessment", getNewITPAssessment(), EmployeeITPDetails.class);
    Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(responseEntity.hasBody()).isEqualTo(true);
    Assertions.assertThat(responseEntity.getBody().getItpAssessments().size()).isGreaterThan(0);
  }

  @Test
  public void should_be_able_update_itp_assessment() {
    ResponseEntity<List<EmployeeITPDetails>> responseEntity = getEmployeeItpDetailsRequest();
    putRequest(updateEmployeeITPDetailsWithRemarks(responseEntity));
    responseEntity = getEmployeeItpDetailsRequest();

    Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(responseEntity.hasBody()).isEqualTo(true);
    Assertions.assertThat(getActualRemarks(responseEntity)).isNotEmpty();
  }

  private Stream<Optional<List<Remarks>>> getActualRemarks(
      ResponseEntity<List<EmployeeITPDetails>> responseEntity) {
    return responseEntity.getBody().stream()
        .filter(itpDetails -> itpDetails.getEmployeeDetails().getOracleId().equals("1234"))
        .map(
            itpDetails -> {
              return itpDetails.getItpAssessments().stream()
                  .map(ItpAssessments::getOverallRemarks)
                  .findAny();
            });
  }

  private void putRequest(EmployeeITPDetails employeeITPDetails) {
    testRestTemplate.put(
        "/itp/update/employee/assessment", employeeITPDetails, EmployeeITPDetails.class);
  }

  private ResponseEntity<List<EmployeeITPDetails>> getEmployeeItpDetailsRequest() {
    return testRestTemplate.exchange(
        "/itp/get/employees",
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<EmployeeITPDetails>>() {});
  }

  private EmployeeITPDetails updateEmployeeITPDetailsWithRemarks(
      ResponseEntity<List<EmployeeITPDetails>> responseEntity) {
    EmployeeITPDetails employeeITPDetails =
        responseEntity.getBody().stream()
            .filter(itpDetails -> itpDetails.getEmployeeDetails().getOracleId().equals("1234"))
            .findFirst()
            .get();
    employeeITPDetails.getItpAssessments().stream()
        .filter(itpAssessment -> itpAssessment.getAssessmentYear().equals("2022"))
        .forEach(
            itpAssessment -> {
              itpAssessment.setOverallRemarks(Arrays.asList(Remarks.builder().comment("Good").build()));
            });
    return employeeITPDetails;
  }

  private NewITPAssessment getNewITPAssessment() {
    Queue qu = new LinkedList();

    return NewITPAssessment.builder()
        .assessmentYear("2022")
        .employeeOracleId(getEmployeeDetailsPojo().getOracleId())
        .assessmentType(AssessmentType.ENGINEERING)
        .itpAssessorOracleId("456")
        .build();
  }

  private EmployeeDetails getEmployeeDetailsPojo() {
    return EmployeeDetails.builder()
        .name("Xyz")
        .oracleId("1234")
        .pidDetails(PidDetails.builder().active(true).pid("543").projectName("SOW").endDate(LocalDateTime.now()).startDate(LocalDateTime.now()).build())
        .currentDesignation("QE")
        .reportingManager("Abc")
        .build();
  }
}

package com.ps.itp.service;

import com.ps.itp.exception.DesignationNotFoundException;
import com.ps.itp.exception.EmployeeNotFoundException;
import com.ps.itp.model.*;
import com.ps.itp.repository.EmployeeDesignationsRepository;
import com.ps.itp.repository.EmployeeDetailsRepository;
import com.ps.itp.repository.ITPAssessmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EmployeeService {

    @Autowired
    private EmployeeDetailsRepository employeeDetailsRepository;

    @Autowired
    ITPService itpService;

    @Autowired
    private EmployeeDesignationsRepository designationsRepository;

    @Autowired
    private ITPAssessmentRepository itpAssessmentRepository;

    @Autowired
    private EmployeeDesignationService employeeDesignationService;

    @Autowired
    private AuthenticationFacade authenticationFacade;

    public List<EmployeeDetails> getAssessorEmployeesList(){
        return employeeDetailsRepository.findAll();
    }

    public EmployeeDesignation saveDesignations(EmployeeDesignation employeeDesignation) {
        return designationsRepository.save(employeeDesignation);
    }

    public EmployeeDetails createNewEmployeeAndStartAssessment(EmployeeDetails employeeDetails){
        Optional<EmployeeDetails> optionalEmployeeDetails = employeeDetailsRepository.findByOracleId(employeeDetails.getOracleId());
        if (optionalEmployeeDetails.isPresent()) {
            employeeDetails.setId(optionalEmployeeDetails.get().getId());
        }
      EmployeeDetails empDetails = employeeDetailsRepository.save(employeeDetails);
    insertAssessmentRecord(empDetails);
      return empDetails;
    }

    public List<EmployeeDesignation> getAllDesignations() {
        return designationsRepository.findAll();
    }


    private void insertAssessmentRecord(EmployeeDetails empDetails) {
        EmployeeITPDetails employeeITPDetails = itpAssessmentRepository.findByEmployeeDetails_oracleId(empDetails.getOracleId()).orElse(EmployeeITPDetails.builder()
                .itpAssessments(Collections.emptyList())
                .itpScore(0.0)
                .itpStatus(ITPStatus.NOT_STARTED)
                .build());
        employeeITPDetails.setEmployeeDetails(empDetails);
        itpAssessmentRepository.save(employeeITPDetails);
    }

    public List<EmployeeDetails> getAssessorEmployeesList(String oracleId) {
        EmployeeDetails employeeDetails = employeeDetailsRepository.findByOracleId(oracleId).orElseThrow(() -> new EmployeeNotFoundException());
        log.info("Current employee details "+employeeDetails);
        return Arrays.asList(employeeDetailsRepository.findByOracleId(employeeDetails.getReportingManager()).orElseThrow(()-> new EmployeeNotFoundException()));
//        return employeeDetailsRepository.findAllByCareerStageIn(getCareerStageList(employeeDetails));
    }


    public void processEmployeeData(@RequestParam("file") MultipartFile employeeSheet) throws IOException {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(employeeSheet.getInputStream());
            XSSFSheet sheet = workbook.getSheetAt(0);
            System.out.println("Total rows ->"+sheet.getPhysicalNumberOfRows());
            for(int i=1; i<sheet.getPhysicalNumberOfRows();i++) {
                XSSFRow row = sheet.getRow(i);
                EmployeeDetails employeeDetails =  buildEmployeeDetails(row);
                createNewEmployeeAndStartAssessment(employeeDetails);
            }

        } catch (IOException e) {
          throw e;
        }
    }

    private EmployeeDetails buildEmployeeDetails(XSSFRow row) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d-MMM-yyyy");
       return  EmployeeDetails.builder().oracleId(row.getCell(0).getRawValue())
                .name(row.getCell(2) +" " +row.getCell(3))
                .currentDesignation(row.getCell(4).toString())
               .careerStage(row.getCell(5).toString())
                .domain(row.getCell(8).toString())
                .tags(Arrays.asList(row.getCell(9).toString()))
                .joiningDate(row.getCell(13).toString())
               .emailId(row.getCell(22).toString())
                .pidDetails(PidDetails.builder()
                        .pid(row.getCell(23).getRawValue())
                        .accountName(row.getCell(25).toString())
                        .projectName(row.getCell(24).toString())
                        .build())
                .reportingManager(row.getCell(20).getRawValue())
                .build();
    }

    private List<String> getCareerStageList(EmployeeDetails employeeDetails) {
        List<EmployeeDesignation> assessorEmployees = designationsRepository
                .findAllByLevelBetween(getEmployeeDesignation(employeeDetails).getLevel(), getManagerDesignation(employeeDetails).getLevel() +1);
        log.info("assessorEmployees "+assessorEmployees);
        return assessorEmployees.stream().map(EmployeeDesignation::getName).collect(Collectors.toList());
    }

    private EmployeeDesignation getManagerDesignation(EmployeeDetails employeeDetails) {
       EmployeeDetails ManagerDetails = employeeDetailsRepository.findByOracleId(employeeDetails.getReportingManager()).orElseThrow(()-> new EmployeeNotFoundException());
        log.info("manager employee details "+ManagerDetails);
        return designationsRepository
                .findByName(ManagerDetails.getCareerStage()).orElseThrow(() -> new DesignationNotFoundException());
    }

    private EmployeeDesignation getEmployeeDesignation(EmployeeDetails employeeDetails){

        return designationsRepository
                .findByName(employeeDetails.getCareerStage()).orElseThrow(() -> new DesignationNotFoundException());

    }

    public List<EmployeeDetails> getSharedEmployeeList(String designation) {
        EmployeeDesignation employeeDesignation = employeeDesignationService.getEmployeeDesignationByName(designation);
        List<EmployeeDesignation> employeeDesignationList =  employeeDesignationService.getListOfEmployeeDesignationLevelGeThanOrEqualTo(employeeDesignation.getLevel());
        List<String> designations = employeeDesignationList.stream().map(empDesgn -> empDesgn.getName()).collect(Collectors.toList());
        return employeeDetailsRepository.findAllByCareerStageIn(designations);
    }

    public EmployeeDetails getLoggedInUser() {
        SimpleKeycloakAccount auth = (SimpleKeycloakAccount) authenticationFacade.getAuthentication().getDetails();
        String emailId = auth.getKeycloakSecurityContext().getToken().getEmail();
        EmployeeDetails employeeDetails = employeeDetailsRepository.findByEmailId(emailId).orElseThrow(() -> new EmployeeNotFoundException());
        return employeeDetails;
    }

    public void processSupervisorData(MultipartFile supervisorSheet) throws IOException {

        try {
            XSSFWorkbook workbook = new XSSFWorkbook(supervisorSheet.getInputStream());
            XSSFSheet sheet = workbook.getSheetAt(1);
            System.out.println("Total rows ->"+sheet.getPhysicalNumberOfRows());
            for(int i=1; i<sheet.getPhysicalNumberOfRows();i++) {
                XSSFRow row = sheet.getRow(i);
                EmployeeDetails employeeDetails =  buildSupervisorDetails(row);
//                try{
//                    CredentialRepresentation passwordCredentials = new CredentialRepresentation();
//                    passwordCredentials.setTemporary(false);
//                    passwordCredentials.setType(CredentialRepresentation.PASSWORD);
//                    passwordCredentials.setValue("password");
//
//                    UserRepresentation user = new UserRepresentation();
//                    user.setUsername(employeeDetails.getName());
//                    user.setFirstName(employeeDetails.getName());
//
//                    user.setEmail(employeeDetails.getEmailId());
//                    user.setCredentials(Collections.singletonList(passwordCredentials));
//                    user.setEnabled(true);
//
//                    keycloak.realm("master").users().create(user);
//                }catch (Exception e){
//                    log.error("Unable to create user in keycloak");
//                }
                createNewEmployeeAndStartAssessment(employeeDetails);
            }

        } catch (IOException e) {
            throw e;
        }
    }


    private EmployeeDetails buildSupervisorDetails(XSSFRow row) {
        System.out.println("row number -> "+row.getRowNum());
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d-MMM-yyyy");

        return  EmployeeDetails.builder().oracleId(row.getCell(0).getRawValue())
                .name(row.getCell(2) +" " +row.getCell(3))
                .currentDesignation(row.getCell(4).toString())
                .careerStage(row.getCell(6).toString())
                .domain(row.getCell(8).toString())
                .tags(Arrays.asList(row.getCell(9).toString()))
                .joiningDate(row.getCell(12).toString())
                .reportingManager(row.getCell(21).getRawValue())
                .emailId(row.getCell(5).toString())
                .pidDetails(PidDetails.builder()
                        .pid(row.getCell(16).getRawValue())
                        .projectName(row.getCell(17).toString())
                        .accountName(row.getCell(19).toString())

                        .build())

                .build();
    }
}

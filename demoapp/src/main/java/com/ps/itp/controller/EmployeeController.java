package com.ps.itp.controller;

import com.ps.itp.model.EmployeeDesignation;
import com.ps.itp.model.EmployeeDetails;
import com.ps.itp.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/emp")
public class EmployeeController {


    @Autowired
    EmployeeService employeeService;



    @PostMapping("/create/employee")
    public EmployeeDetails createEmployee(@RequestBody EmployeeDetails employeeDetails){
        return employeeService.createNewEmployeeAndStartAssessment(employeeDetails);
    }

    @GetMapping("/get/employees")
    public List<EmployeeDetails> getEmployees(){
        return employeeService.getAssessorEmployeesList();
    }


    @GetMapping("/get/designations")
    public List<EmployeeDesignation> getDesignations(){
        return employeeService.getAllDesignations();
    }

    @GetMapping("/loggedIn/user")
    public EmployeeDetails getLoggedInUser(){
        return employeeService.getLoggedInUser();
    }

    @GetMapping("/shareable/details")
    public List<EmployeeDetails> getShareableEmployeeDetails() {
        EmployeeDetails employeeDetails =  employeeService.getLoggedInUser();
     //   List<EmployeeDetails> shareableEmployeeDetails = employeeService.getSharedEmployeeList(employeeDetails.getCareerStage());
     //   return shareableEmployeeDetails;
        return null;
    }

    @GetMapping("/get/assessor/employees")
    public List<EmployeeDetails> getAssessorEmployees(@RequestParam String oracleId){
        return employeeService.getAssessorEmployeesList(oracleId);
    }

    @PostMapping("/save/designations")
    public EmployeeDesignation createNewDesignation(@RequestBody EmployeeDesignation employeeDesignation){
        return employeeService.saveDesignations(employeeDesignation);
    }

    @PostMapping(value = "/create/upload/excel", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String employeeData(@RequestParam("file") MultipartFile employeeSheet) throws IOException {
        employeeService.processEmployeeData(employeeSheet);
        return "Success";
    }

    @PostMapping(value = "/supervisor/upload/excel", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String supervisorData(@RequestParam("file") MultipartFile supervisorSheet) throws IOException {
        employeeService.processSupervisorData(supervisorSheet);
        return "Success";
    }

}

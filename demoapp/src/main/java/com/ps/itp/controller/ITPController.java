package com.ps.itp.controller;

import com.ps.itp.model.EmployeeDetails;
import com.ps.itp.model.EmployeeITPDetails;
import com.ps.itp.model.NewITPAssessment;
import com.ps.itp.repository.EmployeeDetailsRepository;
import com.ps.itp.service.EmployeeService;
import com.ps.itp.service.ITPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/itp")
public class ITPController {

    @Autowired
    ITPService service;

    @GetMapping("/get/employees/assessment")
    public Page<EmployeeITPDetails> getEmployeesAssessment(@RequestParam Integer pageNumber, @RequestParam Integer size){
        return service.getEmployeeItpDetails(pageNumber,size);
    }

    @GetMapping("/search/employees/assessment")
    public Page<EmployeeITPDetails> searchEmployeesAssessment(@RequestParam Integer pageNumber, @RequestParam Integer size, @RequestParam String searchText){
        Page<EmployeeITPDetails>  pageDetails = service.searchEmployeeItpDetails(searchText, pageNumber, size);
        return pageDetails;
    }

    @PostMapping("/start/employee/assessment")
    public EmployeeITPDetails startNewAssessment(@RequestBody NewITPAssessment newITPAssessment){
       return service.startNewItpAssessment(newITPAssessment);
    }

    @PostMapping("/update/employee/assessment")
    public EmployeeITPDetails updateITPAssessment(@RequestBody EmployeeITPDetails employeeITPDetails){
        return service.updateEmployeeITPDetails(employeeITPDetails);
    }

    @GetMapping("/get/employees/assessment/{year}/{id}")
    public EmployeeITPDetails getEmployeeItpDetails(@PathVariable("year") String year, @PathVariable("id") String id){
        EmployeeITPDetails  employeeITPDetails = service.getEmployeeItpDetails(year, id);
        return employeeITPDetails;
    }




}

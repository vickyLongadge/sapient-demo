package com.ps.itp.service;


import com.ps.itp.exception.DesignationNotFoundException;
import com.ps.itp.model.EmployeeDesignation;
import com.ps.itp.model.EmployeeDetails;
import com.ps.itp.repository.EmployeeDesignationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeDesignationService {

    @Autowired
    private EmployeeDesignationsRepository employeeDesignationsRepository;

    public EmployeeDesignation getEmployeeDesignationByName(String designation) {
        EmployeeDesignation employeeDesignation = employeeDesignationsRepository.findByName(designation).orElseThrow(()-> new DesignationNotFoundException());
        return  employeeDesignation;
    }

    public List<EmployeeDesignation> getListOfEmployeeDesignationLevelGeThanOrEqualTo(int level) {
        List<EmployeeDesignation> employeeDesignationList =  employeeDesignationsRepository.findAllByLevelGreaterThanEqual(level);
        return employeeDesignationList;
    }

}

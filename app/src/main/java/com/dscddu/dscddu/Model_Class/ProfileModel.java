package com.dscddu.dscddu.Model_Class;

public class ProfileModel {
    private String phoneNumber,firstName,lastName,collegeId,branch;
    private Double sem;
    public ProfileModel() {
    }

    public ProfileModel(String firstName, String lastName, String collegeId, String branch,
                        Double sem, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.collegeId = collegeId;
        this.branch = branch;
        this.sem = sem;
        this.phoneNumber = phoneNumber;
    }

    public Double getSem() {
        return sem;
    }

    public void setSem(Double sem) {
        this.sem = sem;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(String collegeId) {
        this.collegeId = collegeId;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }
}

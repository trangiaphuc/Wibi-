package com.example.wibi.Models;

public class User {
    private String id, imgURL, backgroundURL, fullname, gender, job_office, hometown, address,
            primary_school, secondary_school, high_school, college, university, marriage, dob, status;

    public User(){ }

    public User(String id, String imgURL, String backgroundURL, String fullname, String gender,
                String job_office,
                String hometown, String address,
                String primary_school, String secondary_school, String high_school,
                String college, String university,
                String marriage, String dob, String status) {
        this.id = id;
        this.imgURL = imgURL;
        this.backgroundURL = backgroundURL;
        this.fullname = fullname;
        this.gender = gender;
        this.job_office = job_office;
        this.hometown = hometown;
        this.address = address;
        this.primary_school = primary_school;
        this.secondary_school = secondary_school;
        this.high_school = high_school;
        this.college = college;
        this.university = university;
        this.marriage = marriage;
        this.dob = dob;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getJob_office() {
        return job_office;
    }

    public void setJob_office(String job_office) {
        this.job_office = job_office;
    }

    public String getHometown() {
        return hometown;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPrimary_school() {
        return primary_school;
    }

    public void setPrimary_school(String primary_school) {
        this.primary_school = primary_school;
    }

    public String getSecondary_school() {
        return secondary_school;
    }

    public void setSecondary_school(String secondary_school) {
        this.secondary_school = secondary_school;
    }

    public String getHigh_school() {
        return high_school;
    }

    public void setHigh_school(String high_school) {
        this.high_school = high_school;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getMarriage() {
        return marriage;
    }

    public void setMarriage(String marriage) {
        this.marriage = marriage;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getBackgroundURL() {
        return backgroundURL;
    }

    public void setBackgroundURL(String backgroundURL) {
        this.backgroundURL = backgroundURL;
    }


    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", imgURL='" + imgURL + '\'' +
                ", backgroundURL='" + backgroundURL + '\'' +
                ", fullname='" + fullname + '\'' +
                ", gender='" + gender + '\'' +
                ", job_office='" + job_office + '\'' +
                ", hometown='" + hometown + '\'' +
                ", address='" + address + '\'' +
                ", primary_school='" + primary_school + '\'' +
                ", secondary_school='" + secondary_school + '\'' +
                ", high_school='" + high_school + '\'' +
                ", college='" + college + '\'' +
                ", university='" + university + '\'' +
                ", marriage='" + marriage + '\'' +
                ", dob='" + dob + '\'' +
                '}';
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

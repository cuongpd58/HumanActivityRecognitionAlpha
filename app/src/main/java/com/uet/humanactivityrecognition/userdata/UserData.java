package com.uet.humanactivityrecognition.userdata;

/**
 * Project name: HumanActivityRecognition
 * Created by Cuong Phan
 * Email: cuongphank58@gmail.com
 * on 08/02/2017.
 * University of Engineering and Technology,
 * Vietnam National University.
 */

public class UserData {

    private String name;

    private String sex;

    private String id;

    private int yearOfBirth;

    private String job;

    public UserData(String name, String sex, int yearOfBirth, String job) {
        this.name = name;
        this.sex = sex;
        this.yearOfBirth = yearOfBirth;
        this.job = job;
        autoCreateID();
    }

    private void autoCreateID() {
        id = name.replaceAll(" ","") + sex + yearOfBirth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getId() {
        return id;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public int getYearOfBirth() {
        return yearOfBirth;
    }

    public void setYearOfBirth(int yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    public boolean equals(UserData other){
        return (other.getName().equals(name) && other.getSex().equals(sex)
                && other.getJob().equals(job)
                && other.getYearOfBirth() == yearOfBirth);
    }
}

package com.mobil.mezun.model;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class User {
    private String id;
    private String firstname;
    private String surname;
    private int entry_year;
    private int graduate_year;
    private String communication_email;
    private String phone_number;
    private String lisans_name;
    private String yuksek_name;
    private String doktora_name;
    private Job job;
    private ArrayList<Post> postList;

    public User() {
    }

    public User(String id, String firstname, String surname, int entry_year, int graduate_year,
                String communication_email, String phone_number, String lisans_name,
                String yuksek_name, String doktora_name, Job job, ArrayList<Post> postList) {
        this.id = id;
        this.firstname = firstname;
        this.surname = surname;
        this.entry_year = entry_year;
        this.graduate_year = graduate_year;
        this.communication_email = communication_email;
        this.phone_number = phone_number;
        this.lisans_name = lisans_name;
        this.yuksek_name = yuksek_name;
        this.doktora_name = doktora_name;
        this.job = job;
        this.postList = postList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getEntry_year() {
        return entry_year;
    }

    public void setEntry_year(int entry_year) {
        this.entry_year = entry_year;
    }

    public int getGraduate_year() {
        return graduate_year;
    }

    public void setGraduate_year(int graduate_year) {
        this.graduate_year = graduate_year;
    }

    public String getCommunication_email() {
        return communication_email;
    }

    public void setCommunication_email(String communication_email) {
        this.communication_email = communication_email;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getLisans_name() {
        return lisans_name;
    }

    public void setLisans_name(String lisans_name) {
        this.lisans_name = lisans_name;
    }

    public String getYuksek_name() {
        return yuksek_name;
    }

    public void setYuksek_name(String yuksek_name) {
        this.yuksek_name = yuksek_name;
    }

    public String getDoktora_name() {
        return doktora_name;
    }

    public void setDoktora_name(String doktora_name) {
        this.doktora_name = doktora_name;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public ArrayList<Post> getPostList() {
        return postList;
    }

    public void setPostList(ArrayList<Post> postList) {
        this.postList = postList;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", firstname='" + firstname + '\'' +
                ", surname='" + surname + '\'' +
                ", entry_year=" + entry_year +
                ", graduate_year=" + graduate_year +
                ", communication_email='" + communication_email + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", lisans_name='" + lisans_name + '\'' +
                ", yuksek_name='" + yuksek_name + '\'' +
                ", doktora_name='" + doktora_name + '\'' +
                ", job=" + job +
                ", postList=" + postList +
                '}';
    }
}

package com.mobil.mezun.model;

public class Job {

    private String country;
    private String city;
    private String company;

    public Job() {
    }

    public Job(String country, String city, String company) {
        this.country = country;
        this.city = city;
        this.company = company;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getCompany() {
        return company;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    @Override
    public String toString() {
        return "Job{" +
                "country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", company='" + company + '\'' +
                '}';
    }
}

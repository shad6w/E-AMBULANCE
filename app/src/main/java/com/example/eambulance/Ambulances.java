package com.example.eambulance;

import java.util.Map;

public class Ambulances {
    private String inputdname,inputdphone,inputdemail,inputdpassword,inputaid,inputadesc;

    public Ambulances() {
    }

    public Ambulances(String inputdname, String inputdphone, String inputdemail, String inputdpassword, String inputaid, String inputadesc) {
        this.inputdname = inputdname;
        this.inputdphone = inputdphone;
        this.inputdemail = inputdemail;
        this.inputdpassword = inputdpassword;
        this.inputaid = inputaid;
        this.inputadesc = inputadesc;
    }

    public String getInputdname() {
        return inputdname;
    }

    public void setInputdname(String inputdname) {
        this.inputdname = inputdname;
    }

    public String getInputdphone() {
        return inputdphone;
    }

    public void setInputdphone(String inputdphone) {
        this.inputdphone = inputdphone;
    }

    public String getInputdemail() {
        return inputdemail;
    }

    public void setInputdemail(String inputdemail) {
        this.inputdemail = inputdemail;
    }

    public String getInputdpassword() {
        return inputdpassword;
    }

    public void setInputdpassword(String inputdpassword) {
        this.inputdpassword = inputdpassword;
    }

    public String getInputaid() {
        return inputaid;
    }

    public void setInputaid(String inputaid) {
        this.inputaid = inputaid;
    }

    public String getInputadesc() {
        return inputadesc;
    }

    public void setInputadesc(String inputadesc) {
        this.inputadesc = inputadesc;
    }
}

package com.example.phamhuan.ModelClass;

public class User {
    private String Name;
    private String Password;
    private String Phone;
    private String Address;
    private String Img_url;
    public User(){

    }

    public User(String name, String password, String address, String img_url) {
        Name = name;
        Password = password;
        Address = address;
        Img_url = img_url;
    }

    public String getImg_url() {
        return Img_url;
    }

    public void setImg_url(String img_url) {
        Img_url = img_url;
    }

    public String getPhone() {
        return Phone;
    }
    public void setPhone(String phone) {
        Phone = phone;
    }
    public String getName() {
        return Name;
    }
    public void setName(String name)
    {
        Name = name;
    }
    public String getPassword() {
        return Password;
    }
    public void setPassword(String password){
        Password = password;
    }
    public String getAddress() {
        return Address;
    };
    public void setAddress(String address){
        Address = address;
    };
}

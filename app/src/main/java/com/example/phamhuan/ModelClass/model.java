package com.example.phamhuan.ModelClass;

import java.io.Serializable;

public class model implements Serializable
{
    private String Name;
    private String Image;
    private String Distance;
    private int Charge;
    private String Status;
    model()
    {}

    public model(String name, String image, String distance, int charge, String status) {
        Name = name;
        Image = image;
        Distance = distance;
        Charge = charge;
        Status = status;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getDistance() {
        return Distance;
    }

    public void setDistance(String distance) {
        Distance = distance;
    }

    public int getCharge() {
        return Charge;
    }

    public void setCharge(int charge) {
        Charge = charge;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}

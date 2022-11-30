package com.example.pesticide_pass.ui.login;

public class ModuleBean{
    private String name;
    private double k;
    private double b;
    private String create_time;

    public double getB() {
        return b;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public void setB(double b) {
        this.b = b;
    }

    public double getK() {
        return k;
    }

    public void setK(double k) {
        this.k = k;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
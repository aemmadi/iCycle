package com.coppellcoders.icycle;

public class Recycle {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDecs() {
        return decs;
    }

    public void setDecs(String decs) {
        this.decs = decs;
    }

    public Recycle(String name, String decs) {
        this.name = name;
        this.decs = decs;
    }

    public String name;
    public String decs;



}

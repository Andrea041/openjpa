package org.apache.openjpa.util.testUtil;
/* This class is used to simulate a non-proxyable object -> "final" modifier */

public final class NonProxyableInstanceFinal {
    private String brand;
    private String model;

    public NonProxyableInstanceFinal(String brand, String model) {
        this.brand = brand;
        this.model = model;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}

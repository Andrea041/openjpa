package org.apache.openjpa.util.testUtil;

public class NonProxyableInstanceNotFinal {
    private String toSay;

    public NonProxyableInstanceNotFinal(String toSay) {
        this.toSay = toSay;
    }

    public String getToSay() {
        return toSay;
    }

    public void setToSay(String toSay) {
        this.toSay = toSay;
    }
}

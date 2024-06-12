package org.apache.openjpa.util.testUtil;
/* This is a proxyable class. Stand to documentation:
* All concrete proxy classes should be public and have public no-args
* constructors so that tools that work via reflection on persistent instances
* can manipulate them. */

public class ProxyableInstance {
    private String dummy;

    public ProxyableInstance() {
        this.dummy = "Hello World";
    }

    public String getDummy() {
        return dummy;
    }

    public void setDummy(String dummy) {
        this.dummy = dummy;
    }
}

package org.apache.openjpa.util.testUtil;

public class InvalidKeyValue {
    @Override
    public int hashCode() {
        // Generate random value -> inconsistent object
        return (int) (Math.random() * 1000);
    }
}

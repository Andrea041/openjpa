package org.apache.openjpa.util.testUtil;

public class InvalidKeyValue {
    @Override
    public int hashCode() {
        // Genera un valore casuale, violando la consistenza
        return (int) (Math.random() * 1000);
    }
}

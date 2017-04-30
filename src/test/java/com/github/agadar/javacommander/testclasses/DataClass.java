package com.github.agadar.javacommander.testclasses;

/**
 * Test data class, used as a parameter type.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public final class DataClass {

    public final String data;

    public DataClass(String data) {
        this.data = data;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.data.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DataClass other = (DataClass) obj;
        return this.data.equals(other.data);
    }
}

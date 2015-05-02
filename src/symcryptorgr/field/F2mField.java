package symcryptorgr.field;

public class F2mField {

    //степень расширения
    private int m;
    //порождающий полином
    private int generator;

    public F2mField(int g) {
        this.generator = g;
        this.m = 31 - Integer.numberOfLeadingZeros(g);
        if (m < 1) {
            throw new IllegalArgumentException("degree of p must be at least 1");
        }
    }

    public int getExtensionDegree() {
        return m;
    }

    public int getGenerator() {
        return generator;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + this.generator;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final F2mField other = (F2mField) obj;
        return this.generator == other.generator;
    }

}

package symcryptorgr;

import symcryptorgr.field.F2mElement;
import symcryptorgr.field.F2mField;

public class PowerFunctionOverFF implements BinaryFunction {

    private F2mField field;
    private int power;
    private int[] truthTable;

    public PowerFunctionOverFF(F2mField field, int power) {
        this.field = field;
        this.power = power;
    }

    @Override
    public int getInputLength() {
        return field.getExtensionDegree();
    }

    @Override
    public int getOutputLength() {
        return field.getExtensionDegree();
    }

    @Override
    public int[] getTruthTable() {
        if (truthTable == null) {
            truthTable = new int[1 << field.getExtensionDegree()];
            for (int i = 0; i < truthTable.length; i++) {
                truthTable[i] = new F2mElement(i, field).pow(power).getBinaryRepresentation();
            }
        }
        return truthTable;
    }

    public String toString() {
        int ext = field.getExtensionDegree();
        return "(" + ext + "," + ext + ")-F: " + "x^" + power;
    }
}

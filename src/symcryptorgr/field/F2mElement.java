package symcryptorgr.field;

import symcryptorgr.Utils;

public class F2mElement {

    private int a;
    private F2mField field;

    public F2mElement(int a, F2mField field) {
        this.field = field;
        this.a = (a & Utils.getLowerBitsMask(field.getExtensionDegree()));
    }

    public F2mElement add(F2mElement elem) {
        checkField(elem);
        return new F2mElement(this.a ^ elem.a, field);
    }

    public F2mElement multiply(F2mElement elem) {
        checkField(elem);
        int product = mult(a, elem.a, field.getGenerator(), field.getExtensionDegree());
        return new F2mElement(product, field);
    }

    public static int mult(int a, int b, int p, int m) {
        //начальные значения
        int aMultXPower = a;
        int product = a * (b & 1);
        for (int i = 1; i < m; i++) {
            //вычисляем a(x) * x^i (mod m(x)) по вычисленному 
            //на пред. шаге a(x) * x^(i-1) (mod m(x))
            //1. Сдвигаем на один бит влево
            aMultXPower <<= 1;
            //2. Если степень результата равна степени модуля - вычитаем его
            //(по модулю 2 равносильно операции xor)
            if ((aMultXPower >>> m) == 1) {
                aMultXPower ^= p;
            }
            //обновляем значение p(x)
            //если i-ый бит b(x) не равен нулю,
            //то прибавляем к текущему значению p(x)
            //значение a(x) * x^i
            if (((b >>> i) & 1) == 1) {
                product ^= aMultXPower;
            }
        }
        return product;
    }

    public F2mElement pow(int pow) {
        if (pow < 0) {
            throw new IllegalArgumentException("power must be positive[" + pow + "]");
        }
        int m = field.getExtensionDegree();
        int p = field.getGenerator();
        int length = 31 - Integer.numberOfLeadingZeros(pow);
        int powerOfA = 1;

        for (int i = length; i >= 0; i--) {
            powerOfA = mult(powerOfA, powerOfA, p, m);
            if (((pow >>> i) & 1) == 1) {
                powerOfA = mult(powerOfA, a, p, m);
            }
        }
        return new F2mElement(powerOfA, field);
    }

    public int getBinaryRepresentation() {
        return a;
    }

    private void checkField(F2mElement elem) {
        if (!field.equals(elem.field)) {
            throw new IllegalArgumentException("elements belongs to different fields");
        }
    }

    @Override
    public String toString() {
        return '(' + String
                .format("%" + field.getExtensionDegree() + "s", Integer.toBinaryString(a))
                .replace(' ', '0') + ')';
    }
}

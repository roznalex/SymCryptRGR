package symcryptorgr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static symcryptorgr.Computions.*;
import symcryptorgr.field.F2mElement;
import symcryptorgr.field.F2mField;

public class SymCryptoRGR {

    public static void explore(BinaryFunction function) {
        System.out.println("Функция: " + function.toString());
        //выделяем координатные функции
        int[][] coordFunctions = splitIntoCoordinateFunctions(function);
        //вычисляем преобразования Уолша-Адамара всех функций
        int[][] walshHadamansforms = new int[coordFunctions.length][];
        for (int i = 0; i < walshHadamansforms.length; i++) {
            walshHadamansforms[i] = fastWalshHadamarTransform(coordFunctions[i]);
        }
        //дисбаланс
        System.out.println("Дисбаланс");
        for (int[] walshHadamansform : walshHadamansforms) {
            System.out.print(Math.abs(walshHadamansform[0]) + "\t");
        }
        System.out.println();
        //находим АНФ всех координатных функций
        int[][] algebraicNormalForms = new int[coordFunctions.length][];
        for (int i = 0; i < algebraicNormalForms.length; i++) {
            algebraicNormalForms[i] = fastMebiusTransform(coordFunctions[i]);
        }
        //находим для каждой функции ее алгебраическую степень
        final int n = function.getInputLength();
        int[] algDegrees = new int[coordFunctions.length];
        System.out.println("Алгебраическая степень");
        for (int i = 0; i < coordFunctions.length; i++) {
            int[] anf = algebraicNormalForms[i];
            algDegrees[i] = algebraicDegree(anf, n);
            System.out.print(algDegrees[i] + "\t");
        }
        System.out.println();
        //нелинейность
        System.out.println("Нелинейность");
        for (int i = 0; i < walshHadamansforms.length; i++) {
            System.out.print(nonLinearity(walshHadamansforms[i], n) + "\t");
        }
        System.out.println();
        //уровень корреляционного имунитета
        System.out.println("Уровень корреляционного имунитета");
        for (int i = 0; i < walshHadamansforms.length; i++) {
            System.out.print(correlationImmunity(walshHadamansforms[i], n) + "\t");
        }
        System.out.println();
        //коэффициенты распростарения ошибок
        int[][] errorCoefs = new int[coordFunctions.length][];
        for (int i = 0; i < coordFunctions.length; i++) {
            errorCoefs[i] = errorCoefficients(coordFunctions[i], n);
        }
        System.out.println("Коэффициенты распространения ошибок");
        for (int i = 0; i < n; i++) {
            System.out.print("i = " + i + ":\t");
            for (int j = 0; j < coordFunctions.length; j++) {
                System.out.print(errorCoefs[j][i] + "\t");
            }
            System.out.println("");
        }
        //отклонения
        System.out.println("Относительное отклонение коэффициентов распростарения ошибок от среднего значения");
        double mean = (1 << (n - 1));
        for (int i = 0; i < n; i++) {
            System.out.print("i = " + i + ":\t");
            for (int j = 0; j < coordFunctions.length; j++) {
                System.out.format("%.5f\t", Math.abs(errorCoefs[j][i] - mean) / mean);
            }
            System.out.println("");
        }
        /**
         * *********Для функции в целом***************
         */
        String funcRepresentation = String.format("(%d, %d)-F", n, function.getOutputLength());
        //алгебраическая степень
        System.out.println("Алгебраическая степень " + funcRepresentation + "\t" + Utils.max(algDegrees));
        //коэффициенты распространения ошибок в среднем
        System.out.println("Коэффициенты распространения ошибок в среднем и отклонения " + funcRepresentation);
        int[] meanErrCoefs = meanErrorCoefficients(function);
        mean = function.getOutputLength() * (1 << (n - 1));
        for (int i = 0; i < meanErrCoefs.length; i++) {
            System.out.format("i = %d:\t%d\t%.5f%n", i, meanErrCoefs[i], Math.abs(meanErrCoefs[i] - mean) / mean);
        }
        //Наличие лавинного эффекта
        System.out.println("Наличие строгого лавинного эффекта:\t" + hasStrictAvalanceEffect(function, errorCoefs));
        System.out.println("Наличие строгого лавинного эффекта в среднем:\t" + hasStrictAvalanceEffectInMean(function, meanErrCoefs));
        //Дифференциальная вероятность
        //System.out.println(maxDifferentialProbability(function));
        System.out.println("Максимум дифференциальной вероятности:\t" + parallelMaxDiffProb(function));
    }

    public static void main(String[] args) {
        F2mField field = new F2mField((1 << 17) + (1 << 3) + 1);
        BinaryFunction f1 = new PowerFunctionOverFF(field, 2);
        explore(f1);
        BinaryFunction f2 = new PowerFunctionOverFF(field, 16260);
        explore(f2);
    }

}

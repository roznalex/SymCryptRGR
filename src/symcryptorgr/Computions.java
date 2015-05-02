package symcryptorgr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Computions {

    public static int[][] splitIntoCoordinateFunctions(BinaryFunction f) {
        int[] truthTable = f.getTruthTable();
        int[][] coordFunctions = new int[f.getOutputLength()][1 << f.getInputLength()];
        for (int x = 0; x < truthTable.length; x++) {
            int value = truthTable[x];
            for (int i = 0; i < coordFunctions.length; i++) {
                coordFunctions[i][x] = (value >> i) & 1;
            }
        }
        return coordFunctions;
    }

    /**
     * 
     * @param bf булева функция (n, 1)
     * @return преобразование Уолша
     */
    public static int[] fastWalshHadamarTransform(int[] bf) {
        int[] wht = new int[bf.length];
        for (int i = 0; i < wht.length; i++) {
            wht[i] = 1 - (bf[i] << 1);
        }
        for (int k = bf.length >> 1; k >= 1; k >>= 1) {
            int inc = k << 1;
            for (int i = 0; i < bf.length; i += inc) {
                int limit = i + k;
                for (int j = i; j < limit; j++) {
                    int twin = j + k;
                    int t = wht[j];
                    wht[j] += wht[twin];
                    wht[twin] = t - wht[twin];
                }
            }
        }
        return wht;
    }

    public static int[] fastMebiusTransform(int[] bf) {
        int[] anf = Arrays.copyOf(bf, bf.length);
        for (int k = bf.length >> 1; k >= 1; k >>= 1) {
            int inc = k << 1;
            for (int i = 0; i < bf.length; i += inc) {
                int limit = i + k;
                for (int j = i; j < limit; j++) {
                    anf[j + k] = anf[j] ^ anf[j + k];
                }
            }
        }
        return anf;
    }

    public static int algebraicDegree(int[] anf, int n) {
        int algebraicDegree = 0;
        //перебираем k от n до нуля
        degreeLookup:
        for (int k = n; k > 0; k--) {
            //создаем генератор k-битных чисел
            KBitNumbersGenerator gen = new KBitNumbersGenerator(n, k);
            //перебираем числа в поисках ненулевого коэффициента АНФ
            while (gen.hasNext()) {
                //Проверяем очередное число. 
                //Если коэф. не равен нулю, то прекращаем поиск
                if (anf[gen.next()] != 0) {
                    algebraicDegree = k;
                    break degreeLookup;
                }
            }
        }
        return algebraicDegree;
    }

    public static int[] errorCoefficients(int[] bf, int n) {
        int[] errCoefs = new int[n];
        for (int i = 0; i < n; i++) {
            int e = 1 << i;
            for (int x = 0; x < bf.length; x++) {
                errCoefs[i] += bf[x] ^ bf[x ^ e];
            }
        }
        return errCoefs;
    }

    public static int[] meanErrorCoefficients(BinaryFunction function) {
        int[] f = function.getTruthTable();
        int n = function.getInputLength();
        int[] errCoefs = new int[n];
        for (int i = 0; i < n; i++) {
            int e = 1 << i;
            for (int x = 0; x < f.length; x++) {
                errCoefs[i] += Integer.bitCount(f[x] ^ f[x ^ e]);
            }
        }
        return errCoefs;
    }

    public static class DiffProbCounter implements Callable<Integer> {

        public int[] f;
        public int minA;
        public int maxA;

        public DiffProbCounter(int[] f, int minA, int maxA) {
            this.f = f;
            this.minA = minA;
            this.maxA = maxA;
        }

        @Override
        public Integer call() throws Exception {
            int maxDiff = 0;
            for (int a = minA; a < maxA; a++) {
                int[] b = new int[f.length];
                for (int x = 0; x < f.length; x++) {
                    b[f[x] ^ f[x ^ a]]++;
                }
                for (int i = 0; i < b.length; i++) {
                    if (b[i] > maxDiff) {
                        maxDiff = b[i];
                    }
                }
            }
            return maxDiff;
        }
    }

    public static int THREAD_NUM = 4;

    public static int parallelMaxDiffProb(BinaryFunction function) {
        int[] f = function.getTruthTable();
        int range = f.length / THREAD_NUM;
        ExecutorService pool = Executors.newFixedThreadPool(THREAD_NUM);
        List<Future<Integer>> results = new ArrayList<>();
        for (int a = 1; a < f.length; a += range) {
            DiffProbCounter counter = new DiffProbCounter(f, a, Math.min(a + range, f.length));
            results.add(pool.submit(counter));
        }
        int max = 0;
        for (Future<Integer> result : results) {
            try {
                int value = result.get();
                if (max < value) {
                    max = value;
                }
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(Computions.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        pool.shutdown();
        return max;
    }

    public static int maxDifferentialProbability(BinaryFunction function) {
        int[] f = function.getTruthTable();
        int maxDiff = 0;

        for (int a = 1; a < f.length; a++) {
            int[] b = new int[f.length];
            for (int x = 0; x < f.length; x++) {
                b[f[x] ^ f[x ^ a]]++;
            }
            for (int i = 0; i < b.length; i++) {
                if (b[i] > maxDiff) {
                    maxDiff = b[i];
                }
            }
        }
        return maxDiff;
    }

    /**
     *
     * @param wht - преобразование Уолша-Адамара исследуемой функции
     * @param n - длина входа функции в битах. Справедливо что log2(wht.length)
     * = n
     * @return
     */
    public static int nonLinearity(int[] wht, int n) {
        int absMax = 0;
        for (int x : wht) {
            int abs = Math.abs(x);
            if (abs > absMax) {
                absMax = abs;
            }
        }
        return (1 << (n - 1)) - (absMax >>> 1);
    }

    /**
     *
     * @param wht - преобразование Уолша-Адамара исследуемой функции
     * @param n - длина входа функции в битах. Справедливо что log2(wht.length)
     * = n
     * @return
     */
    public static int correlationImmunity(int[] wht, int n) {
        for (int k = 1; k <= n; k++) {
            KBitNumbersGenerator gen = new KBitNumbersGenerator(n, k);
            while (gen.hasNext()) {
                if (wht[gen.next()] != 0) {
                    return k - 1;
                }
            }
        }
        return n;
    }

    public static boolean hasStrictAvalanceEffect(BinaryFunction f, int[][] errorCoefs) {
        int target = 1 << (f.getInputLength() - 1);
        for (int[] functionErrorCoefs : errorCoefs) {
            for (int errorCoef : functionErrorCoefs) {
                if (errorCoef != target) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean hasStrictAvalanceEffectInMean(BinaryFunction f, int[] meanErrorCoefs) {
        int target = f.getOutputLength() * (1 << (f.getInputLength() - 1));
        for (int meanErrorCoef : meanErrorCoefs) {
            if (meanErrorCoef != target) {
                return false;
            }
        }
        return true;
    }
}

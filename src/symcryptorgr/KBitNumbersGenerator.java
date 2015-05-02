package symcryptorgr;

public class KBitNumbersGenerator {

    //общее число задействованых бит
    private int n;
    //число единичных бит
    private int k;
    //массив хранящий текущее состояние
    private int[] state;
    //флаг показывающий есть ли след. элемент
    private boolean hasNext;

    public KBitNumbersGenerator(int n, int k) {
        if ((n > 32 || n <= 0) || (k < 0 || k > n)) {
            throw new IllegalArgumentException("n = " + n + ", k = " + k);
        }
        this.n = n;
        this.k = k;
        state = new int[k];
        for (int i = 0; i < state.length; i++) {
            state[i] = i;
        }
        hasNext = true;
    }

    private boolean nextState() {
        int i = k - 1;
        while (i >= 0 && state[i] == n - k + i) {
            i--;
        }
        if (i == -1) {
            return false;
        }
        state[i]++;
        for (i++; i < k; i++) {
            state[i] = state[i - 1] + 1;
        }
        return true;
    }

    private int convertStateIntoNumber() {
        int num = 0;
        for (int i = 0; i < k; i++) {
            num += (1 << state[i]);
        }
        return num;
    }

    public boolean hasNext() {
        return hasNext;
    }

    public int next() {
        if (!hasNext) {
            throw new IllegalStateException("values exceeded");
        }
        int retVal = convertStateIntoNumber();
        hasNext = nextState();
        return retVal;
    }
}

package symcryptorgr;

public class Utils {

    public static int getLowerBitsMask(int nbits) {
        if (nbits < 0 || nbits > 32) {
            throw new IllegalArgumentException();
        }
        return nbits == 0 ? 0 : 0xFFFFFFFF >>> (32 - nbits);
    }

    public static int max(int[] values) {
        int max = values[0];
        for (int i = 1; i < values.length; i++) {
            if (max < values[i]) {
                max = values[i];
            }
        }
        return max;
    }
}

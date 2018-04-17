import java.lang.Math;
import java.math.BigInteger;

public class SSS {
    private int nShares;
    int k = 5;
    private int coef[][];
    double x[];
    private static final int mod = 251;

    SSS(int nShares) {
        int shift; int mult;
        switch (nShares){
            case 4:  shift = 100; mult = 148; break;
            case 5: shift = 225; mult = 23; break;
            default: shift = 1; mult = 249;
        }
        this.nShares = nShares;
        x = new double[5];
        for (int d = 0; d < 5; d++) {
            x[d] = (int) (Math.random() * mult) + shift;
            for (int e = 0; e < d; e++){
                if (x[d] == x[e]) {
                    d--;
                    break;
                }
            }
        }
    }

    /**
     * Makes shares of byte array
     * @param bytes - data to be secured
     * @return byte[][] x - share, y - value
     */
    public byte[][] encryptBytes(byte[] bytes){
        int data[] = new int[bytes.length];
        for (int i = 0; i < bytes.length; i++){
            data[i] = bytes[i]&0xFF;
        }
        byte[][] out = new byte[k][bytes.length];
        int[][] e = encryptData(data);
        for (int i = 0; i < k; i++){
            System.out.print(".");
            for (int j = 0; j < bytes.length; j ++){
                out[i][j] = (byte) e[i][j];
            }
        }
        return out;
    }
    void generateCoefficientMatrix(int dataLength){
        coef = new int[dataLength][nShares-1];
        for (int i = 0; i < dataLength; i++) {
            for (int c = 0; c < nShares - 1; c++) {
                coef[i][c] = (int) (Math.random() * 249) + 1;
            }
        }
    }
    /**
     * Makes shares of integer array
     * @param data - data to be secured
     * @return int[][] x - share, y - value
     */
    private int[][] encryptData(int[] data){
        int[][] out = new int[k][data.length];
        for (int byt = 0; byt < data.length; byt++) {

            for (int share = 0; share < k; share++) {
                if ((data[byt]&0xFF)>250)
                    data[byt] = 250;
                out[share][byt] = encryptInt(data[byt], x[share], byt);
            }
        }
        return out;
    }

    /**
     * Gets the modified value of an int fed into SSS
     * @param t - secret
     * @param x - x value of share
     * @return int - y value of share
     */
    int encryptInt(int t, double x, int index){
        for (int i = 0; i < nShares-1; i++) {
            t += (coef[index][i] * (Math.pow(x,(i+1)))) % mod;
        }
        return (t%mod);
    }

    /**
     * Reconstruct original data from shares
     * @param bytes - x - share, y - value
     * @return byte[] - recovered data
     */
    public byte[] reconstructBytes(byte[][] bytes){
        byte[] out = new byte[bytes[0].length];
        for (int i = 0; i < bytes[0].length; i++){
            int[] ibytes = new int[nShares];
            for (int j = 0; j < nShares; j++){
                ibytes[j] = bytes[j][i]&0xFF;
            }
            out[i] = (byte)reconstructData(ibytes);
        }
        return out;
    }

    /**
     * Get back secret using LaGrange Interpolation
     * @param shares - array of shares for byte/int
     * @return int - secret
     */
    int reconstructData(int[] shares){
        int sum;
        int dens[] = new int[nShares];
        int terms[] = new int[nShares];
        int den = 1;
        for (int i = 0; i < nShares; i++) {
            int term = shares[i];
            for (int j = 0; j < nShares; j++) {
                if (i != j) {
                    term = (int) ((term * (0 - x[j])) % mod);
                    den = (int) (den * ((x[i] - x[j])));
                }
            }
            dens[i] = den;
            terms[i] = term;
            den = 1;
        }
        int lcm = getLcm(dens);

        for (int z = 0; z < terms.length; z++) {
            int fac = lcm / dens[z];
            terms[z] = (terms[z] * fac) % mod;
        }
        int inv = getInverse(lcm);
        sum = sum(terms) % mod;
        sum = (sum * inv) % mod;
        if (sum < 0){
            sum += mod;
        }
        return sum;
    }

    /**
     * Quick method for summing an array of integers
     * @param nums - array of ints to be summed
     * @return int - sum
     */
    private int sum(int[] nums){
        int sum = 0;
        for (int l : nums)
            sum += l;
        return sum;
    }

    /**
     * Get modulare inverse
     * @param val - int
     * @return int - modular inverse
     */
    private int getInverse(int val){
        return (new BigInteger(""+ val).modInverse(new BigInteger(""+mod)).intValue());
    }

    /**
     * finds GCD between two numbers
     * @param x - lesser number
     * @param y - greater number
     * @return long - gcd
     */
    private long gcd(long x, long y){
        while (y > 0){
            long t = y;
            y = x % y;
            x = t;
        }
        return x;
    }

    /**
     * gets the lcm of an array of integers
     * @param nums - int array
     * @return int - lcm
     */
    private int getLcm(int[] nums){
        int lcm = Math.abs(nums[0]);
        for (int num : nums) lcm = (int) lcm(lcm, Math.abs(num));
        return lcm;
    }

    /**
     * get lcm of two integers
     * @param x - int
     * @param y - int
     * @return int - lcm
     */
    private long lcm(long x, long y){
        return x *(y / gcd(x,y));
    }

    /**
     * sorts integer with insertion sort
     * @param array - to be sorted
     */
    private void sort(int[] array){
        for (int i = 0; i < array.length; i++){
            array[i] = Math.abs(array[i]);
        }
        for (int i = 0; i < array.length; i ++){
            for (int j = i; j < array.length; j++){
                if (array[j] < array[i]){
                    int temp = array[j];
                    array[j] = array[i];
                    array[i] = temp;

                }
            }
        }
    }
}

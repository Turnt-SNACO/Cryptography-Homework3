import java.lang.Math;
import java.math.BigInteger;

public class SSS {
    private int nShares;
    private int coef[];
    double x[];
    static final int mod = 251;
    public SSS(int nShares) {
        this.nShares = nShares;
        coef = new int[nShares-1];
        for (int c = 0; c < nShares-1; c++)
            coef[c] = (int)(Math.random()*249)+1;
        x = new double[nShares];
        for (int d = 0; d < nShares; d++)
            x[d] = (int)(Math.random()*249)+1;
    }
    public byte[][] encryptBytes(byte[] bytes){
        int data[] = new int[bytes.length];
        for (int i = 0; i < bytes.length; i++){
            data[i] = bytes[i]&0xFF;
        }
        byte[][] out = new byte[nShares][bytes.length];
        int[][] e = encryptData(data);
        for (int i = 0; i < nShares; i++){
            for (int j = 0; j < bytes.length; j ++){
                out[i][j] = (byte) e[i][j];
            }
        }
        return out;
    }
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
    private int[][] encryptData(int[] data){
        int[][] out = new int[nShares][data.length];
        for (int byt = 0; byt < data.length; byt++) {
            for (int share = 0; share < nShares; share++) {
                if ((data[byt]&0xFF)>250)
                    data[byt] = 250;
                out[share][byt] = encryptByte(data[byt], x[share]);
            }
        }
        return out;
    }

    /**
     * Get back secret using LaGrange Interpolation
     * @param shares - array of shares for byte/int
     * @return int - secret
     */
    int reconstructData(int[] shares){
        long sum = 0;
        long dens[] = new long[nShares];
        long terms[] = new long[nShares];
        long den = 1;
        for (int i = 0; i < nShares; i++) {
            long term = shares[i];
            for (int j = 0; j < nShares; j++) {
                if (i != j) {
                    term *= (0 - x[j]);
                    den *= ((x[i] - x[j]));
                }
            }
            dens[i] = den;
            terms[i] = term;
            den = 1;
        }
        long lcm = getLcm(dens);//0;
        for (int z = 0; z < terms.length; z++) {
            long fac = lcm / dens[z];
            terms[z] *= fac;
            dens[z] *= fac;
        }
        int inv = getInverse((int)lcm);
        sum = sum(terms);
        sum *= inv;
        sum %= mod;
        if (sum < 0){
            sum += mod;
        }
        return (int)sum;
    }
    private long sum(long[] nums){
        long sum = 0;
        for (long l : nums)
            sum += l;
        return sum;
    }
    private int getInverse(int val){
        return (new BigInteger(""+ val).modInverse(new BigInteger(""+mod)).intValue());
    }
    private int[] den(){
        int prod=1;
        int array[] = new int[nShares];
        for (int i = 0; i < nShares; i++){
            for (int j = 0; j < nShares; j++){
                if (j!=i){
                   prod *= x[i] - x[j];
                }
            }
            array[i] = prod;
            prod = 1;
        }
        return array;
    }

    /**
     * Gets the modified value of an int fed into SSS
     * @param b - secret
     * @param x - x value of share
     * @return int - y value of share
     */
    int encryptByte(int b, double x){
        int t = b&0xFF;
        for (int i = 0; i < nShares-1; i++){
            t += (coef[i] * (Math.pow(x,(i+1))));
        }
        return (t%mod);
    }

    /**
     * Finds the gcd of n numbers
     * @param array - int array of numbers
     * @return int - gcd
     */
    private long getGCD(int[] array){
        int[] denoms = new int[array.length];
        System.arraycopy(array, 0, denoms, 0, array.length);
        sort(denoms);
        long result = denoms[0];
        for (int i = 1; i < denoms.length; i ++) {
            result = gcd(result, denoms[i]);
        }
        return result;
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
    private long getLcm(long[] nums){
        long lcm = nums[0];
        for (int l = 0; l < nums.length; l++)
            lcm = lcm(lcm, Math.abs(nums[l]));
        return lcm;
    }
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

import java.lang.Math;
public class SSS {
    int nShares;
    int shares[][];
    int coef[];
    public SSS(int nShares) {
        this.nShares = nShares;
        this.shares = new int[2][nShares];
        coef = new int[5];
        for (int i = 0; i < 5; i++)
            coef[i] = (int) (Math.random()*256);
    }
    public int[] getCoef(){
        return coef;
    }
    public int[] getShare(int shareNum){
        int share[] = new int[2];
        share[0] = shares[0][shareNum];
        share[1] = shares[1][shareNum];
        return share;
    }
    public void encryptByte(byte b){

    }
    public void generateShares(byte b, int x){
        for (int j = 0; j < nShares; j++) {
            int out = (int) b;
            for (int i = 0; i < 3; i++) {
                out += coef[i] * (Math.pow(shares[0][i], i));
            }
            shares[1][j] = out;
        }
    }
    int f(byte b,int x){
        return (int) (b + coef[0]*x + coef[1]*Math.pow(x,2) + coef[2]*Math.pow(x,3)
                        + coef[3]* Math.pow(x,4) + coef[4]*Math.pow(x,5));
    }
}
 
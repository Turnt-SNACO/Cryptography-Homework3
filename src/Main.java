import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Main {
    public static void main(String [] args) {
        Main m = new Main();
        m.man(args);
    }
    public void man(String [] args) {
        int nShares = 3;
        SSS s = new SSS(nShares);
        long t1 = System.currentTimeMillis();
        long t2 = 0;
        URL inputURL = getClass().getResource("/res/small.jpg");
        BufferedImage[] outImage = new BufferedImage[nShares];
        try {
            BufferedImage image = ImageIO.read(inputURL);
            byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
            byte[][] encrypted = s.encryptBytes(pixels);

            for (int i = 0; i < nShares; i ++) {
                outImage[i] = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
                outImage[i].setData(Raster.createRaster(outImage[i].getSampleModel(), new DataBufferByte(encrypted[i], encrypted[i].length), new Point()));
                ImageIO.write(outImage[i], "jpg", new File("/home/james/Pictures/small"+i+".bmp"));
            }
            t2 = System.currentTimeMillis();

            byte[][]ePixels = new byte[nShares][pixels.length];
            for (int i = 0; i < nShares; i++){
                ePixels[i] = ((DataBufferByte) outImage[i].getRaster().getDataBuffer()).getData();
            }
            byte[] decrypted = s.reconstructBytes(ePixels);
            BufferedImage oi = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
            oi.setData(Raster.createRaster(oi.getSampleModel(), new DataBufferByte(decrypted, decrypted.length), new Point()));
            ImageIO.write(oi, "jpg", new File("/home/james/Pictures/smalld.bmp"));

        } catch (IOException e) {
            System.out.println("Error with image path!");
        }
        long t3 = System.currentTimeMillis();
        System.out.println("Shares made in "+(t2-t1)+"ms.");
        System.out.println("Reconstruction completed in "+(t3-t2)+"ms.");
        System.out.println("Task completed in "+(t3-t1)+"ms.");

        int test = 128;
        System.out.println("Secret: "+test);
        System.out.println("-----");
        int [] S = new int[nShares];
        for (int i = 0; i < nShares; i++) {
            S[i] = (int)s.encryptByte(test, s.x[i]);
            System.out.println("Share: (" + s.x[i] + "," + S[i] + ")");
        }
        int r = s.reconstructData(S);
        System.out.println("recon: "+r);


    }
}

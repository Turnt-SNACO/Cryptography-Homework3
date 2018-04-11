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
        SSS s = new SSS(3);
//        URL inputURL = getClass().getResource("/res/small.jpg");
//        BufferedImage[] outImage = new BufferedImage[3];
//        try {
//            BufferedImage image = ImageIO.read(inputURL);
//            byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
//            System.out.println(pixels.length);
//            byte[][] encrypted = s.encryptBytes(pixels);
//
//            for (int i = 0; i < 3; i ++) {
//                outImage[i] = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
//                outImage[i].setData(Raster.createRaster(outImage[i].getSampleModel(), new DataBufferByte(encrypted[i], encrypted[i].length), new Point()));
//                ImageIO.write(outImage[i], "jpg", new File("/home/james/Pictures/small"+i+".bmp"));
//            }
//
//
//            byte[][]ePixels = new byte[3][pixels.length];
//            for (int i = 0; i < 3; i++){
//                ePixels[i] = ((DataBufferByte) outImage[i].getRaster().getDataBuffer()).getData();
//            }
//            byte[] decrypted = s.reconstructBytes(ePixels);
//            BufferedImage oi = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
//            oi.setData(Raster.createRaster(oi.getSampleModel(), new DataBufferByte(decrypted, decrypted.length), new Point()));
//            ImageIO.write(oi, "jpg", new File("/home/james/Pictures/smalld.bmp"));
//
//        } catch (IOException e) {
//            System.out.println("Error with image path!");
//        }


        int test = 13;
        System.out.println("Secret: "+test);
        System.out.println("-----");
        int [] S = new int[3];
        S[0] = s.encryptByte(test, s.x[0]);
        S[1] = s.encryptByte(test, s.x[1]);
        S[2] = s.encryptByte(test, s.x[2]);
        System.out.println("Share: ("+1+","+S[0]+")");
        System.out.println("Share: ("+4+","+S[1]+")");
        System.out.println("Share: ("+10+","+S[2]+")");
        int r = s.reconstructData(S);
        System.out.println("recon: "+r);


    }
}

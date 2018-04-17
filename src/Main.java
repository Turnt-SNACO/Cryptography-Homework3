import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Main {
    private SSS s;
    public static void main(String [] args) {
        Main m = new Main();
        long t1 = System.currentTimeMillis();
        m.man("small.jpg", "smalli.jpg");
        long t2 = System.currentTimeMillis();
        System.out.println("Time: "+(t2-t1)+" ms.");

    }

    private void man(String im, String subImage) {
        int nShares = 2;
        s = new SSS(nShares);
        URL inputURL = getClass().getResource("/res/"+im);
        BufferedImage[] outImage = new BufferedImage[5];
        try {
            System.out.println("Making shares of image...");
            /* MAKE SHARES */
            BufferedImage image = ImageIO.read(inputURL);
            byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
            s.generateCoefficientMatrix(pixels.length);
            byte[][] encrypted = s.encryptBytes(pixels);

            for (int i = 0; i < 5; i ++) {
                outImage[i] = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
                outImage[i].setData(Raster.createRaster(outImage[i].getSampleModel(), new DataBufferByte(encrypted[i], encrypted[i].length), new Point()));
                //ImageIO.write(outImage[i], "jpg", new File("/home/james/Pictures/share"+i+".bmp"));
                ImageIO.write(outImage[i], "jpg", new File("out/share"+i+".jpg"));
            }
            System.out.println("\nReconstructing shares...");
            /* RECONSTRUCT */
            byte[][]ePixels = new byte[5][pixels.length];
            for (int i = 0; i < 5; i++){
                ePixels[i] = ((DataBufferByte) outImage[i].getRaster().getDataBuffer()).getData();
            }
            byte[] decrypted = s.reconstructBytes(ePixels);
            BufferedImage oi = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
            oi.setData(Raster.createRaster(oi.getSampleModel(), new DataBufferByte(decrypted, decrypted.length), new Point()));
            ImageIO.write(oi, "jpg", new File("out/reconstructed.jpg"));

            /* Making shares for subtract image */
            BufferedImage subIm = ImageIO.read(getClass().getResource("/res/"+subImage));
            byte[] subPix = (((DataBufferByte) subIm.getRaster().getDataBuffer()).getData());
            byte[][] subEPixels = s.encryptBytes(subPix);
            for (int x = 0; x < 5; x ++) {
                outImage[x] = new BufferedImage(image.getWidth(), subIm.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
                outImage[x].setData(Raster.createRaster(outImage[x].getSampleModel(), new DataBufferByte(subEPixels[x], subEPixels[x].length), new Point()));
                ImageIO.write(outImage[x], "jpg", new File("out/sshare"+x+".jpg"));
            }

            System.out.println("Performing image subtraction on shares..");
            /* IMAGE SUBTRACTION */
            BufferedImage picture = ImageIO.read(getClass().getResource("/res/"+subImage));

            byte[] pic = ((DataBufferByte) picture.getRaster().getDataBuffer()).getData();
            byte[][] sub = new byte[5][pic.length];
            byte[] oSub = new byte[pic.length];
            System.out.println("Pixels[o] = "+(pixels[0]&0xFF));
            for ( int k = 0; k < pixels.length; k ++){
                if ((pixels[k]&0xFF) > 250) pixels[k] = (byte)250;
                if ((pic[k]&0xFF) > 250) pic[k] = (byte)250;
            }
            for (int i = 0; i < 5; i++){
                for (int j = 0; j < pic.length; j ++){
                    sub[i][j] = (byte) (((subEPixels[i][j]&0xFF)- (ePixels[i][j])&0xFF)%251);
                }
            }
            for (int i = 0; i < pixels.length; i++){
                oSub[i] = (byte) ((pic[i]&0xFF) - (pixels[i]&0xFF));
                // oSub[i] = (byte) (pic[i] - pixels[i]);
            }
            System.out.println("Reconstructing subtracted shares... ");
            byte[] recon = s.reconstructBytes(sub);
            BufferedImage subo = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
            subo.setData(Raster.createRaster(subo.getSampleModel(), new DataBufferByte(recon, recon.length), new Point()));
            ImageIO.write(subo, "jpg", new File("out/psReconstructed.jpg"));
            subo.setData(Raster.createRaster(subo.getSampleModel(), new DataBufferByte(oSub, oSub.length), new Point()));
            ImageIO.write(subo, "jpg", new File("out/subtracted.jpg"));

        } catch (IOException e) {
            System.out.println("Error with image path!");
            e.printStackTrace();
        }
    }
}

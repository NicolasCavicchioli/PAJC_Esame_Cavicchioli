package tetris;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import function.SupplierExc;

/**
 * Class containing all the data to be loaded from the Resources folder,
 * that will be used for other classes. 
 */
public class Assets {
	
	public static final Image[] images;
	public static final Image icon = loadImage("icon.png");
	public static final int XBlockImageIndex = 1;
	
	static {
		String source = "spritesheet.png";
		BufferedImage image = loadImage(source);
		int n = 10;
		images = new Image[n];
		for (int i=0; i<n; i++) {
			images[i] = image.getSubimage(i*32, 0, 32, 32);
		}
		
	}
	
	private static BufferedImage loadImage(String file) {
		String path = System.getProperty("user.dir")+"/Resources/"+file;
		return SupplierExc.of(() -> ImageIO.read(new File(path))).get();
	}
	
}

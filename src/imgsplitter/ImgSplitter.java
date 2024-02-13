import java.io.File;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;

public class ImgSplitter{

    public static boolean isBlank(BufferedImage img, int bkg){

        final int WHITE = 0;
        final int TRANSPARENT = 1;
        final int MAGENTA = 2;

        int width = img.getWidth();
        int height = img.getHeight();

        //if transparent
        switch(bkg){
            case TRANSPARENT:

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    // Check the alpha channel of each pixel
                    if ((img.getRGB(x, y) & 0xFF000000) != 0) {
                        return false; // Image contains non-transparent pixel
                    }
                }
            }

            return true;

            //if bkg is white
            case WHITE:

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int rgb = img.getRGB(x, y);
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;
    
                    // Check if the pixel is not white
                    if (red != 255 || green != 255 || blue != 255) {
                        return false; // Image contains non-white pixel
                    }
                }
            }

            return true;

            case MAGENTA:

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int rgb = img.getRGB(x, y);
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;
    
                    // Check if the pixel is not magenta
                    if (red != 255 || green != 0 || blue != 255) {
                        return false; // Image contains non-white pixel
                    }
                }
            }

            return true;

            default:
            return true;
        }

            

    }

    public static void main(String args[]){

        BufferedImage src = null;
        BufferedImage[][] output;
        int srcWidth, srcHeight;
        int srcProcWidth, srcProcHeight;
        final int UNIT_SIZE = 34;
        final int IMG_SIZE  = 32;
        String prefix = "";
        String suffix = "";
        Scanner in = new Scanner(System.in);
        String file;
        String fileExt;
        //0 = white, 1 = transparent, 2 = magenta 0xFF00FF
        int bkg = 0x7fffffff;
        boolean isCharacterSheet = false;
        boolean validInput = false;

        //1. Get the image from HDD into a bufferedimage

        System.out.print("Enter source image name (ex. \"characterSprites.jpg\"): ");
        file = in.nextLine();

        try{

            src = ImageIO.read(new File(file));

        }catch(IOException e){
            System.err.println("Could not open file: " + file);
            e.printStackTrace();
            System.exit(1);
        }
    
        while(!validInput){
            try{
                System.out.print("Is the background color: \n(1) White?\n(2) Transparent?\n(3) Magenta (R:255, G:0, B:255)?\nEnter a number: ");
                bkg = Integer.parseInt(in.nextLine());

                if(bkg < 1 || bkg > 3)
                    throw new InputMismatchException();

                bkg--;

                validInput = true;
            }catch(InputMismatchException | NumberFormatException e){
                
            }
        }
        

        fileExt = file.substring(file.lastIndexOf('.'));

        System.out.print("Is this a character sheet? This question is used for naming tiles. (y/N): ");
        String confirm = in.nextLine();
        if(confirm.length() == 1){
            switch(confirm.charAt(0)){
                case 'y':
                case 'Y':
                isCharacterSheet = true;
                break;
                default:
                isCharacterSheet = false;
            }
        }

        srcWidth = src.getWidth();
        srcHeight = src.getHeight();

        if(srcWidth < UNIT_SIZE || srcHeight < UNIT_SIZE){
            System.err.println("Image too small! Image must be at least 34x34 pixels to process.");
            System.exit(1);
        }

        srcProcWidth = (srcWidth / UNIT_SIZE) + 1;
        srcProcHeight = (srcHeight / UNIT_SIZE) + 1;

        output = new BufferedImage[srcProcHeight][srcProcWidth];


        for(int x = 0; x < srcProcWidth; x++){
            if(isCharacterSheet && x <= 11){
                switch(x){
                    case 0:
                    suffix = "_down_0";
                    break;
                    case 1:
                    suffix = "_left_0";
                    break;
                    case 2:
                    suffix = "_up_0";
                    break;
                    case 3:
                    suffix = "_right_0";
                    break;
                    case 4:
                    suffix = "_down_1";
                    break;
                    case 5:
                    suffix = "_left_1";
                    break;
                    case 6:
                    suffix = "_up_1";
                    break;
                    case 7:
                    suffix = "_right_1";
                    break;
                    case 8:
                    suffix = "_down_2";
                    break;
                    case 9:
                    suffix = "_left_2";
                    break;
                    case 10:
                    suffix = "_up_2";
                    break;
                    case 11:
                    suffix = "_right_2";
                    break;
                }
            }else{
                suffix = Integer.toString(x) + "_";
            }

            for(int y = 0; y < srcProcHeight; y++){

                int xCoord = (x+1) + (IMG_SIZE * x);
                int yCoord = (y+1) + (IMG_SIZE * y);

                try{
                    output[y][x] = src.getSubimage(xCoord, yCoord, IMG_SIZE, IMG_SIZE);
                }catch(RasterFormatException e){
                    continue;
                }
                

                if(isBlank(output[y][x], bkg)){
                    System.gc();
                    continue;
                }

                if(isCharacterSheet && y <= 7){
                    switch(y){
                        case 0:
                        prefix = "alex";
                        break;
                        case 1:
                        prefix = "mike";
                        break;
                        case 2:
                        prefix = "mittens";
                        break;
                        case 3:
                        prefix = "vex";
                        break;
                        case 4:
                        prefix = "shibe";
                        break;
                        case 5:
                        prefix = "athena";
                        break;
                        case 6:
                        prefix = "faelyn";
                        break;
                        case 7:
                        prefix = "glen_4";
                        break;
                    }
                }else{
                    prefix = Integer.toString(y);
                }


                

                try{
                    if(isCharacterSheet && x <= 11 && y <= 7){
                        ImageIO.write(output[y][x], fileExt.substring(1), new File( prefix + suffix + fileExt));
                        System.out.println("Created \"" + prefix + suffix + fileExt + "\"");
                    }else{
                        ImageIO.write(output[y][x], fileExt.substring(1), new File( suffix + prefix + fileExt));
                        System.out.println("Created \"" + suffix + prefix + fileExt + "\"");
                    }
                    
                }catch(IOException e){
                    if(isCharacterSheet && x <= 11 && y <= 7){
                        System.err.println("Could not output file: \"" + prefix + suffix + fileExt + "\"");
                    }else{
                        System.err.println("Could not output file: \"" + suffix + prefix + fileExt + "\"");
                    }
                    e.printStackTrace();
                }

                System.gc();

            }
        }

        System.out.println("Image split completed!");
        in.close();
        
    }

}
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
//import java.util.Arrays;
import java.util.Scanner;

import javax.imageio.ImageIO;//  Java 2D supports loading these external image formats into its BufferedImage format using its Image I/O API which is in the javax.imageio package.
// Image I/O has built-in support for GIF, PNG, JPEG, BMP, and WBMP.

public class LSB_encode {
		
	static final String MESSAGEFILE = "C:\\Users\\500068287\\Downloads\\Project Ideas\\Hs.txt"; // text that would be hided
	
	static final String COVERIMAGEFILE = "C:\\Users\\500068287\\Downloads\\Project Ideas\\Sunset.JPG";	// image file which will store the hided data 
	
	static final String STEGIMAGEFILE = "C:\\Users\\500068287\\Downloads\\Project Ideas\\steg1.png";
	
	public static void main(String[] args) throws Exception {
		
		String contentOfMessageFile = (readMessageFile());// It will return content in the message file
		
        int[] bits = bit_Msg(contentOfMessageFile); // Calling bit_Msg for converting the message into bits with size len*8

        System.out.println("msg in file "+contentOfMessageFile);

        for(int i=0;i<bits.length;i++)
           System.out.print(bits[i]);

          System.out.println();
  
        BufferedImage theImage=readImageFile(COVERIMAGEFILE);// This is the image where we would hide our data.

        hideTheMessage(bits, theImage);// Calling actual hideTheMessage() function by passing the array bits and image

}
	
  public static String readMessageFile () throws FileNotFoundException{
	
	String contentOfMessageFile = "";
	
	File a = new File (MESSAGEFILE);
	
	Scanner scan = new Scanner (a);
	
	while (scan.hasNextLine())
	{
		
	  String next = scan.nextLine();
	  
	  contentOfMessageFile += next;
	  
	   if (scan.hasNextLine())
	   {
	    contentOfMessageFile += "\n";
	   }
	   
	}
	
	scan.close();
	
	return contentOfMessageFile;
	}
	
  public static int[] bit_Msg(String msg)
  {
	  
	  int j=0;
	  int[] b_msg=new int[msg.length()*8];// Each character has a size of 8 bits
	
	    for(int i=0;i<msg.length();i++)
		{
		 int x = msg.charAt(i);
		 
		 String x_s = Integer.toBinaryString(x);
		 
		 while(x_s.length()!=8)
		 {
			x_s='0'+x_s;
		 }
		// new System.out.println("dec value for "+msg.charAt(i)+" "+x +" is "+x_s);

		 for(int i1=0;i1<8;i1++) 
		 {
		    b_msg[j] = Integer.parseInt(String.valueOf(x_s.charAt(i1)));
		    j++;
		 }
	   }
	
	 return b_msg;
   }
 
  public static BufferedImage readImageFile(String COVERIMAGEFILE)
  {
   
   BufferedImage theImage = null;
   
   File p = new File (COVERIMAGEFILE);
   
   try
   {
    theImage = ImageIO.read(p);
   }
   catch (IOException e)
   {
    e.printStackTrace();
    System.exit(1);
   }
   
   return theImage;
  }

    static boolean isValid(BufferedImage theImage, int msg_len)
	 {
		 int img_size = theImage.getHeight()* theImage.getWidth();
		 
		 System.out.println("Image size is : "+theImage.getHeight()+"  "+theImage.getWidth());
		 
		 int column = theImage.getHeight();
		 
		 String x_s = Integer.toBinaryString(msg_len);
		 
		 int size_of_msg_bit = x_s.length();
		 if(column < size_of_msg_bit )
		 {
			  
			 return false;
		 }
		 
		 img_size -= column;
		 
		 if((8*msg_len) > img_size)
		 {
			 return false;
		 }
		 
		 return true;	 
	 }
    public static void hideTheMessage (int[] bits, BufferedImage theImage) throws Exception
	{
		
	 
   	  File f = new File (STEGIMAGEFILE);
	  
	  BufferedImage sten_img = null;
	
	 int bit_l = bits.length/8; // Numbers of character in message
	  
	 if(!isValid(theImage, bit_l))
	  {
		  System.out.println("Cannot be stored");
		  return ;
	  }
	 int[] bl_msg = new int[8]; // Stores the length in the bits form(used as delimiter)
	
	 System.out.println("bit lent "+bit_l);
	
	 String bl_s = Integer.toBinaryString(bit_l);
	
	 while(bl_s.length()!=8)
	 {
		bl_s = '0' + bl_s;// for making 8 bit pattern
	 } 
	
	System.out.println("b1_s "+bl_s);
	
	for(int i1 = 0;i1 < 8;i1++) 
	{
		bl_msg[i1] = Integer.parseInt(String.valueOf(bl_s.charAt(i1)));
	}
	  
    int j = 0;// Message bit array index
	
    int b = 0;// Message length bit array index

    int currentBitEntry = 0;

    int count2 = 0;// Count for bits toggled
	
	int count1 = 0;// Count for total bits involved

  llf : for (int x = 0; x < theImage.getWidth(); x++)
  {
	for ( int y = 0; y < theImage.getHeight(); y++)
	{
	    if(x == 0 && y < 8){
		
		int currentPixel = theImage.getRGB(x, y);	

		int ori=currentPixel;
		
		int red = currentPixel>>16;
		red = red & 255;
		int green = currentPixel>>8;
		green = green & 255;
		int blue = currentPixel;
		blue = blue & 255;
		
		String x_s = Integer.toBinaryString(blue);
		
		//System.out.println("x_s : "+x_s);
		String sten_s = x_s.substring(0, x_s.length()-1);
		//System.out.println(sten_s);
		
		sten_s = sten_s+Integer.toString(bl_msg[b]);
        
		if((bl_msg[b] ^ Integer.parseInt(x_s.substring(x_s.length()-1))) == 1)
		{
			count2++;
		}
		count1++;
		int s_pixel = Integer.parseInt(sten_s, 2);
		
		int a=255;
		int rgb = (a<<24) | (red<<16) | (green<<8) | s_pixel;
		theImage.setRGB(x, y, rgb);
		//System.out.println("original "+ori+" after "+theImage.getRGB(x, y));
		ImageIO.write(theImage, "png", f);
        b++;

	}
	else if (currentBitEntry < bits.length ){

	int currentPixel = theImage.getRGB(x, y);	
	int ori=currentPixel;
	int red = currentPixel>>16;
	red = red & 255;
	//red =0;
	int green = currentPixel>>8;
	green = green & 255;
	int blue = currentPixel;
	blue = blue & 255;
	String x_s=Integer.toBinaryString(blue);
	String sten_s=x_s.substring(0, x_s.length()-1);
	
	sten_s=sten_s+Integer.toString(bits[j]);
	
	if((bits[j] ^ Integer.parseInt(x_s.substring(x_s.length()-1))) == 1)
		{
			count2++;
		}
		count1++;
	j++;
	
	
	int temp=Integer.parseInt(sten_s,2);
	int s_pixel=Integer.parseInt(sten_s, 2);
	
	int  a=255;
	int rgb = (a<<24) | (red<<16) | (green<<8) | s_pixel;
	theImage.setRGB(x, y, rgb);
	//System.out.println("original "+ori+" after "+theImage.getRGB(x, y));
	ImageIO.write(theImage, "png", f);

	currentBitEntry++;	
	
	}
	
	if(currentBitEntry == bits.length)
	{
		System.out.println("BREAK");
		break llf;
	}
	
   }
   
 }
 float  tot = theImage.getWidth()*theImage.getHeight();
 float res = ( count2)/tot;
 System.out.println("Percentage change in quality of picture "+res*100);
}

}
	
// Size of message allowed
  
// Image quality distorted	
	
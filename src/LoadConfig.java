import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class LoadConfig {
    private ArrayList<String> configArray = new ArrayList<String>();
    
	public LoadConfig(String fileName) throws FileNotFoundException 
	{
		
		//Declare config variables
		String readHeader = "";

		// Reads specified file and skips header lines
		Scanner config = new Scanner(new File(fileName));
				
		while(!readHeader.equals("*Header End*"))
		{
			readHeader = config.nextLine();			
		}
				
		//Reads values after header and stores in arraylist, skips empty lines and stops on last line
		while(config.hasNextLine())
		{
			readHeader = config.nextLine();
				if (!readHeader.isEmpty())
				{
					configArray.add(readHeader);
				}
		}
		config.close();


	}
	//returns array of config lines
	public ArrayList<String> getConfig()
	{
		return configArray;
	}

}

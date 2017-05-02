import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;

public class SQLCopy {
	private static final String VERSION = "2.0";
	private static final String CONFIG_DICTIONARY = "configDictionary.txt";
	private static LoadConfig loadConfig;
    private static ProcessConfig processConfig;
	
/*	
	public SQLCopy() {
	}
*/
	//This method reads config dictionary of config files
	public static ArrayList<String> LoadConfigDictionary() throws FileNotFoundException {
		
		//Declare config variables
		ArrayList<String> configDictionary = new ArrayList<>();
		String readHeader = "";
		
		// Reads specified file and skips header lines
		Scanner configScan = new Scanner(new File(CONFIG_DICTIONARY));
		
		while(!readHeader.equals("*Header End*")){
			readHeader = configScan.nextLine();			
		}
		
		//config.nextLine();
		//Reads values after header and stores in arraylist, skips empty lines and stops on last line
		while(configScan.hasNextLine()){
			readHeader = configScan.nextLine();
			if (!readHeader.isEmpty()){
				configDictionary.add(readHeader);
			}
		}
		configScan.close();
		return configDictionary;
	}

	public static void main(String[] args) throws FileNotFoundException {
		System.out.println("SQLCopy v"+VERSION);
		
		//initialize input and main program variables
		Scanner input = new Scanner(System.in);
		String fileNameInput = null;
		ArrayList<String> configFileDictionary = new ArrayList<String>();
		int configQueueSize = 0;
		configFileDictionary = LoadConfigDictionary();
		
		if(args.length >= 1){ //checks for CLI argument
			fileNameInput = args[0];
			loadConfig = new LoadConfig(fileNameInput);
			processConfig = new ProcessConfig(loadConfig.getConfig());
		}
		else if(!configFileDictionary.isEmpty()) //processes non empty config dictionary
		{
			configQueueSize = configFileDictionary.size();
			
			for(int i = 0; i < configQueueSize; i++){
				loadConfig = new LoadConfig(configFileDictionary.get(i));			
				processConfig = new ProcessConfig(loadConfig.getConfig());
			}
		}
	}
}

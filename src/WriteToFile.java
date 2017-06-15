import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
//import java.util.Scanner;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WriteToFile {
	private static final String VERSION = "2.1";
	private String fileName = "";
	private ArrayList<String> logArray = new ArrayList<String>();
	private boolean verbose = false;
	
	public WriteToFile(boolean verboseOption) { //constructor method
		logArray.add("SQLCopy v"+VERSION);
		
		if(verboseOption == true){
			verbose = true;
			logArray.add("File writer initiated");
			System.out.println("File writer inititated");
		}
	}
	
	public void loadFile(String inputFileName) throws IOException {
		fileName = inputFileName;
		
		File file = new File(fileName);
		
		if(!file.exists() && !file.isDirectory()) {
			file.createNewFile();
			System.out.println("file created");
		}
	}
	
	public void addEvent(String stringLine) throws FileNotFoundException {
		StringBuilder eventString = new StringBuilder("");
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		
		eventString = eventString.append("("+dtf.format(now)+") ");
		eventString = eventString.append(stringLine);
		
		logArray.add(eventString.toString());
		
		if(verbose == true){
			System.out.println(eventString.toString());
		}
		
	}
	
	public void writeLog() throws FileNotFoundException {
		PrintWriter file = new PrintWriter(new File(fileName));
		
		for(int i = 0; i < logArray.size(); i++) {
			file.println(logArray.get(i));
		}
		file.close();
	}

}

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class ProcessConfig {
	private static LoadConfig loadConfig;

	private static WriteToFile writeToFile;
	
	public ProcessConfig(String configFileName) throws FileNotFoundException { //constructor method
		String logFilePath = "log_"+configFileName;
		
		loadConfig = new LoadConfig(configFileName);
			System.out.println("Config Loaded: "+configFileName);
		run(loadConfig.getConfig(), logFilePath);
	}
	
	public void run(ArrayList<String> config, String logFileName) throws FileNotFoundException {
		//config variables
			ArrayList<String> configOptions = new ArrayList<String>();
			
			configOptions = config;
			
			String conFrom = configOptions.get(0);
			String fromView = configOptions.get(1);
			String conTo = configOptions.get(2);
			String toTable = configOptions.get(3);
			String deleteExisting = "n";
			Boolean verbose = false;
			
			if(configOptions.get(4).equalsIgnoreCase("y")){
				deleteExisting = "y";
			}
			
			if(configOptions.get(5).equalsIgnoreCase("verbose")){
				verbose = true;
				System.out.println("Verbose mode enabled");
			} 
			
		//declare and initialize file writer
		writeToFile = new WriteToFile(verbose);
		
			try {
				writeToFile.loadFile(logFileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		writeToFile.addEvent("Config Loaded");
		 System.out.println("Processing config...");
		 
		//print config details
		if(verbose == true){
			for (int i = 0; i < configOptions.size(); i++){
				writeToFile.addEvent("Line"+i+": "+configOptions.get(i));
			}
		}

		//Attempt connections and transfer data
		try{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			//from connection and result set 
			Connection fromConnection = DriverManager.getConnection(conFrom);
				writeToFile.addEvent("Connected to source");			
			Statement fromStatement = fromConnection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet fromResultSet = fromStatement.executeQuery("select * from "+ fromView);
			ResultSetMetaData fromRSMD = fromResultSet.getMetaData();
			ArrayList<String> fromRSArray = new ArrayList<String>();
			
			//count columns
			int fromColumnCount = fromRSMD.getColumnCount();	
			
			//count rows selected and copy results to ArrayList
			int rowCount = 0;
			
			while(fromResultSet.next()){
				rowCount++;
				//Build insert row and copy to array
				StringBuilder fromRowString = new StringBuilder("");		
				
				fromRowString = fromRowString.append("'"+fromResultSet.getString(1)+"'");
				
				for (int i = 2; i <= fromColumnCount; i++){
					if(fromResultSet.getString(i) == null){ //checks for nulls
						fromRowString = fromRowString.append(",''");
					}else 
						fromRowString = fromRowString.append(",'"+fromResultSet.getString(i)+"'");											
				}
				fromRSArray.add(fromRowString.toString());
				
			} 
			fromResultSet.beforeFirst();		
			
			writeToFile.addEvent(rowCount+" rows selected, with "+fromColumnCount+" columns");
			
			if(verbose == true){
				for(int x = 0; x < fromRSArray.size(); x++)
				{
					System.out.println(fromRSArray.get(x));
				}
			}
			
			
			
			//needs to be split here so source and destination can be split into methods
			
			
			
			
			
			
			
			
			
			//to
			Connection toConnection = DriverManager.getConnection(conTo);
				writeToFile.addEvent("Connected to target");
			Statement toStatement = toConnection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet toResultSet = toStatement.executeQuery("select * from "+toTable);
			ResultSetMetaData toRSMD = toResultSet.getMetaData();
			
			//Build insert string
			StringBuilder toInsertString = new StringBuilder("");		

			toInsertString = toInsertString.append(toRSMD.getColumnName(1));
			for (int i = 2; i <= fromColumnCount; i++){
				toInsertString = toInsertString.append(","+toRSMD.getColumnName(i));
			}
			
			if(verbose == true){
				System.out.println(toInsertString);
			}
			
			//delete existing data
			if(deleteExisting.equals("y") == true){
				toStatement.execute("delete from "+toTable);
				writeToFile.addEvent("Existing data deleted");
			}
			
			//insert rows
			rowCount = 0;
			
			writeToFile.addEvent("Inserting rows...");
			
			while(fromResultSet.next()) {
				toStatement.executeUpdate("insert into "+toTable+" ("+toInsertString+") "
				+ "values ("+fromRSArray.get(rowCount)+")");
				rowCount++;
				
					if(verbose == true){
						writeToFile.addEvent("Row "+rowCount+" inserted: "+fromResultSet.getString(1)+"  "+fromResultSet.getString(2)+"..."+fromResultSet.getString(fromColumnCount-1)+"  "+fromResultSet.getString(fromColumnCount));
					}
			}
			
			writeToFile.addEvent(rowCount+" rows inserted.");
			
			
			
			if (fromResultSet != null) try { fromResultSet.close(); } catch(Exception e) {}  
		    if (fromStatement != null) try { fromStatement.close(); } catch(Exception e) {}  
		    if (fromConnection != null) try { fromConnection.close(); } catch(Exception e) {}	
		    if (toResultSet != null) try { toResultSet.close(); } catch(Exception e) {}  
		    if (toStatement != null) try { toStatement.close(); } catch(Exception e) {}  
		    if (toConnection != null) try { toConnection.close(); } catch(Exception e) {}	
		    
		    writeToFile.addEvent("Config Processed");
		    
		}catch(Exception e){writeToFile.writeLog(); System.out.println(e);}
		
		
		writeToFile.writeLog();
		
	}

}

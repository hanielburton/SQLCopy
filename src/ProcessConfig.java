import java.sql.*;
import java.util.ArrayList;

public class ProcessConfig {

	public ProcessConfig(ArrayList<String> config) {
		ArrayList<String> configOptions = new ArrayList<String>();
		
		configOptions = config;
		
		String conFrom = configOptions.get(0);
		String fromView = configOptions.get(1);
		String conTo = configOptions.get(2);
		String toTable = configOptions.get(3);
		String deleteExisting = "n";
		
		Boolean verbose = true;
		
		if(configOptions.get(4).equals("y")){
			deleteExisting = "y";
		}
		
		//print config details
		if(verbose == true){
			for (int i = 0; i < configOptions.size(); i++){
				System.out.println("Line"+i+": "+configOptions.get(i));
			}
		}

		//Attempt connections and transfer data
		try{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			//from connection and result set 
			Connection fromConnection = DriverManager.getConnection(conFrom);
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
			
			System.out.println(rowCount+" rows selected, with "+fromColumnCount+" columns");
			if(verbose == true){
				for(int x = 0; x < fromRSArray.size(); x++)
				{
					System.out.println(fromRSArray.get(x));
				}
			}
			
			//to
			Connection toConnection = DriverManager.getConnection(conTo);
			Statement toStatement = toConnection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet toResultSet = toStatement.executeQuery("select * from "+toTable);
			ResultSetMetaData toRSMD = toResultSet.getMetaData();
			
			//Build insert string
			StringBuilder toInsertString = new StringBuilder("");		
			toInsertString = toInsertString.append(toRSMD.getColumnName(1));
			for (int i = 2; i <= fromColumnCount; i++){
				toInsertString = toInsertString.append(","+toRSMD.getColumnName(i));
			}
			
			System.out.println(toInsertString);
			
			//delete existing data
			if(deleteExisting.equals("y") == true){
				toStatement.execute("delete from "+toTable);
				System.out.println("Existing data deleted");
			}
			
			//insert rows
			rowCount = 0;
			System.out.println("Inserting rows..."); 
			while(fromResultSet.next()) {
				toStatement.executeUpdate("insert into "+toTable+" ("+toInsertString+") "
				+ "values ("+fromRSArray.get(rowCount)+")");
				rowCount++;
				if(verbose == true){
					System.out.println("Row "+rowCount+" inserted: "+fromResultSet.getString(1)+"  "+fromResultSet.getString(2)+"..."+fromResultSet.getString(fromColumnCount-1)+"  "+fromResultSet.getString(fromColumnCount));	
				}
			} 
			System.out.println(rowCount+" rows inserted.");
			
		if (fromResultSet != null) try { fromResultSet.close(); } catch(Exception e) {}  
	    if (fromStatement != null) try { fromStatement.close(); } catch(Exception e) {}  
	    if (fromConnection != null) try { fromConnection.close(); } catch(Exception e) {}	
	    if (toResultSet != null) try { toResultSet.close(); } catch(Exception e) {}  
	    if (toStatement != null) try { toStatement.close(); } catch(Exception e) {}  
	    if (toConnection != null) try { toConnection.close(); } catch(Exception e) {}	
			
		}catch(Exception e){System.out.println(e);}
		
	}

}

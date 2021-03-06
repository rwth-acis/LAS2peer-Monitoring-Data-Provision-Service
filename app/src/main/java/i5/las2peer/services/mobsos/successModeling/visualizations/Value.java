package i5.las2peer.services.mobsos.successModeling.visualizations;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import i5.las2peer.services.mobsos.successModeling.database.SQLDatabase;

/**
 *
 * Returns a simple value as visualization result.
 *
 * @author Peter de Lange
 *
 */
public class Value implements Visualization {
	
	
	public String visualize(Map<String, String> queries, SQLDatabase database) throws Exception{
		String queryResult = "";
		if(queries.size() == 1){
			List<String> list = new ArrayList<String>(queries.values());
			ResultSet resultSet;
			ResultSetMetaData resultSetMetaData;
			try {
				resultSet = database.query(list.get(0));
				resultSetMetaData = resultSet.getMetaData();
			} catch (SQLException e) {
				throw new Exception("(Value Visualization) The query has lead to an error: " + e);
			}
			
			if(resultSetMetaData.getColumnCount() != 1){
				throw new Exception("Value queries have to return a single value! " + queries.get(list.get(0)));
			}
			if(!resultSet.next()){
				throw new Exception("Value result is empty! " +  queries.get(list.get(0)));
			}
			queryResult = resultSet.getString(1);
			if(resultSet.next()){
				throw new Exception("Value queries have to return a single value! " + queries.get(list.get(0)));
			}
			
			return queryResult;
		}
		throw new Exception("More than one query defined for value visualization!");
	}
	
	
}

package detector;


import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import beat.HeartBeat;
/**
 * This class is used during election to determine the winner
 * 
 * @author Njoroge-John
 *
 */
public class MapSorter {
	/**
	 * Used during election to determine the winner. 
	 * The map is sorted and the first entry in java.util.SortedMap is the new leader
	 * @param values
	 * @return
	 */
	public static SortedMap<String,HeartBeat> sortMap(Map<String,HeartBeat> values){
		SortedMap<String, HeartBeat> sorted=new TreeMap<String, HeartBeat>();
		sorted.putAll(values);
		return sorted;
	}
}

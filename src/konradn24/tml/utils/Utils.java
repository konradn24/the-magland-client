package konradn24.tml.utils;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import konradn24.tml.entities.Entity;

public class Utils {
	
	public static String loadFileAsString(String path){
		StringBuilder builder = new StringBuilder();
		
		try{
			BufferedReader br = new BufferedReader(new FileReader(path));
			String line;
			while((line = br.readLine()) != null)
				builder.append(line + "\n");
			
			br.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		return builder.toString();
	}
	
	public static int parseInt(String number){
		try{
			return Integer.parseInt(number);
		}catch(NumberFormatException e){
			e.printStackTrace();
			return 0;
		}
	}
	
	public static List<String> breakString(String str, int charsPerLine) {
		List<String> parts = new ArrayList<>();
		
		int lastCut = 0;
		int counter = 0;
		for(int i = 0; i < str.length(); i++) {
			counter++;
			
			if(i == str.length() - 1) {
				parts.add(0, str.substring(lastCut, i + 1));
				break;
			}
			
			if(str.charAt(i) == '\n' || counter >= charsPerLine) {
				if(counter > 1)
					parts.add(0, str.substring(lastCut, i));
				
				lastCut = i;
				counter = 0;
			}
		}
		
		return parts;
	}
	
	public static List<String> breakString(String str, int charsPerLine, boolean firstOnTop) {
		List<String> parts = breakString(str, charsPerLine);
		
		if(firstOnTop) Collections.reverse(parts);
		
		return parts;
	}
	
	public static String getLongest(List<String> strings) {
		if(strings.isEmpty())
			return "";
		
		String longest = strings.get(0);
		
		for(String string : strings) {
			if(string.length() > longest.length())
				longest = string;
		}
		
		return longest;
	}
	
	public static <T> Consumer<T> forEachWithCounter(BiConsumer<Integer, T> consumer) {
	    AtomicInteger counter = new AtomicInteger(0);
	    return item -> consumer.accept(counter.getAndIncrement(), item);
	}
	
	public static boolean isPrintableChar(char c) {
	    Character.UnicodeBlock block = Character.UnicodeBlock.of( c );
	    return (!Character.isISOControl(c)) &&
	            c != KeyEvent.CHAR_UNDEFINED &&
	            block != null &&
	            block != Character.UnicodeBlock.SPECIALS;
	}
	
	public static String directionString(byte direction) {
		if(direction == Entity.DOWN) return "south";
		if(direction == Entity.LEFT) return "west";
		if(direction == Entity.RIGHT) return "east";
		
		return "north";
	}
}

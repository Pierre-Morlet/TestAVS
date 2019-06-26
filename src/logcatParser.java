import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class logcatParser {

	
	public static void main(String[] args) {
				
		String file = null;
			
		final String helpContent = "Usage (Java): java -jar logcat-parser.jar logcat_file.txt -i word1,word2,word3\r\n" +
				"-h prints out the help containing info about all available switches.\r\n" + 
				"-s prints out the time difference between lines containing \"TEST STARTED\" and \"TEST FINISHED\".\r\n" + 
				"-i <args> prints out lines containing all arguments.\r\n" + 
				"-e <args> prints out all lines which don't contain any of provided arguments.";
		
		
		// Loading first argument
		if (args.length >0 ) {
			
			// we accept -h as first argument
			if (args[0].contentEquals("-h")) {
				   System.out.println(helpContent);
				   System.exit(0);
			}
			
			//check if the file exists
			file = args[0];
			File tempFile = new File(file);
			if (!tempFile.exists())	{
				//System.out.println("Working Directory = " + System.getProperty("user.dir"));
				System.out.println("Provide a valid file to be parsed as first argument. Use -h to see options");
				System.exit(0);
			}
		}
		else {
			System.out.println("Provide a valid file to be parsed as first argument. Use -h to see options");
			System.exit(0);
		}
		
	   // Loading the following arguments
		int i = 1;
		if (args[i].charAt(0) == '-' && args[i].length() == 2) {
		   	switch (args[i].charAt(1)) {
		   		case 'i':
		   		case 'e':
		   			if (args.length-1 == i)
	                    throw new IllegalArgumentException("Expected arg after: "+args[i]);
		   			searchKeywords(args[i], args[i+1], file);
	                break;
		   		case 'h':
		   			System.out.println(helpContent);
		            break;
		   		case 's':
		   			displayTimeDiff(file);
		   			break;
		   		default:
		   			System.out.println("unknown argument: " + args[i]);
		   	}
		}
		
    	else {
    		throw new IllegalArgumentException("Not a valid argument: "+args[i]);
    	}
	   
 	   
	}
	
	//Read the input text file and display the lines that match the keywords criteria
	private static void searchKeywords(String flag, String keywords, String file)  {

	    boolean foundSomething = false;
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;

		    while ((line = br.readLine()) != null) {
		       switch (flag.charAt(1)) {
		       case 'i': 
		    	   if (lineContainsAll(line, keywords)) {
		    		   System.out.println(line);
		    		   foundSomething = true;
		    	   }
		    	   break;
		       case 'e': 
		    	   if (lineContainsNone(line, keywords)) {
		    		   System.out.println(line);
		    		   foundSomething = true;
		    	   }
		    	   break;	   
			    }
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block - should be fine since we checked earlier
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block - should be fine since we checked earlier
			e.printStackTrace();
		}
		
		if (!foundSomething)
			System.out.println("the keywords: " + keywords +" did not return any match for the " + flag + " switch");
			
	}
	
	//Extract the time stamp from the first two lines containing the specific string and call timeDiff() method.
	private static void displayTimeDiff(String file) {
			
		final String timeDiffStart 	= "TEST STARTED";
		final String timeDiffEnd 	= "TEST FINISHED";
		String timeStart = null;
		String timeEnd = null;
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		       
		    	   if (line.contains(timeDiffStart)) {
		    		   timeStart=line.substring(0, 18);
		    		   System.out.println(line);
		    		   continue;
		    	   }
		      
		    	   if (line.contains(timeDiffEnd)) {
		    		   timeEnd=line.substring(0, 18);
		    		   System.out.println(line);
		    		   break;
		    	   }
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block - should be fine since we checked earlier
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block - should be fine since we checked earlier
			e.printStackTrace();
		}
		
		System.out.println("Test duration : " + timeDiff(timeStart, timeEnd));
		
	}
	
	//Return true if the line contains all (comma separated) keywords
	private static boolean lineContainsAll(String line, String keywords) {

		var array = keywords.split(",");
		for (int i = 0; i < array.length ; i++) {
			if (!line.contains(array[i]))
				return false;
		}
		return true;
	}
	
	//Return true if the line contains no keyword
	private static boolean lineContainsNone(String line, String keywords) {

		var array = keywords.split(",");
		for (int i = 0; i < array.length ; i++) {
			if (line.contains(array[i]))
				return false;
		}
		return true;
	}
	
	//Convert string time to date format - Calculates the difference in milliseconds - Format in a human readable output
	private static String timeDiff(String timeStart, String timeEnd) {

		   Date dateStart = null;
		   Date dateEnd = null;
		   SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
		   
		    try {
		    	dateStart  = sdf.parse(timeStart);
		    	dateEnd  = sdf.parse(timeEnd);
		        
		    } catch (ParseException e) {
		        e.printStackTrace();
		    }
		    
		    long diffInMillies = Math.abs(dateEnd.getTime() - dateStart.getTime());
		    String timeDiff =    String.format("%d min, %d sec", 
		    	    TimeUnit.MILLISECONDS.toMinutes(diffInMillies),
		    	    TimeUnit.MILLISECONDS.toSeconds(diffInMillies) - 
		    	    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(diffInMillies))
		    	);
		    
		   return(timeDiff);
	}

}

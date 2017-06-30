import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import java.text.*;
import java.util.regex.*;


public class Invest_tweet {
    public static void main(String[] args) throws Exception {
		// read graph from input file
		FileInputStream fis = new FileInputStream(args[0]);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String line = null;
		String line_output = "";
		String line_before = "";
		int nou = 0;
		String time = "";

		PrintWriter pw = new PrintWriter(new FileWriter(args[1]));
		PrintWriter pw1 = new PrintWriter(new FileWriter(args[2]));

		List<List<String>> original = new ArrayList<>();
		ConcurrentHashMap<String, List<String>> calculated = new ConcurrentHashMap<>();

		while ((line = br.readLine()) != null) {

			if(line.contains("created_at")){ //some tweets are not by users i guess.. so ignore them
				line_output = "";
				time ="";

				//extract pure text...
				line_output+=line.split("text\":\"")[1].split("\",")[0];
				line_before = line_output;

				// extract hashtags from the line if there's one
				Pattern MY_PATTERN = Pattern.compile("#(\\S+)");
				Matcher mat = MY_PATTERN.matcher(line_output);
				List<String> hashtags = new ArrayList<String>();
				while (mat.find()) {
				  hashtags.add(mat.group(1));
				}
				

				line_output = line_output.replaceAll("\\\\u(\\p{XDigit}{4})", "");
				if(line_output!=line_before) // get the number of unicode...
					nou++;

				//add timestamp...	
				line_output+=" (timestamp : ";
				line_output+=line.split("created_at")[1].split("\",")[0];


				//get timestamp in second...
				time =line.split("created_at\":\"")[1].split("\",")[0];
				Date timestamp = new Date(time);
				long tSec = timestamp.getTime()/1000;

				//add timestamp to hashtag, so we can calculate degrees later...
				hashtags.add(Long.toString(tSec));

				//2nd feature... calculate degrees of index	
				avgDegree(original, calculated, hashtags, pw1);
				line_output+= ")";
				line_output = line_output.replaceAll("\\\\", "");			
				pw.println(line_output);
			}
		}

		br.close();

		pw.println(" ");
		pw.println(nou+ " tweets contained unicode.");
		pw.close();
		pw1.close();
    }

	static public void avgDegree(List<List<String>> original, ConcurrentHashMap<String, List<String>> calculated, List<String> hashtags, PrintWriter pw1){

		original.add(hashtags);		
		int time = Integer.parseInt(hashtags.get(hashtags.size()-1));
		List<Integer> removes = new ArrayList<>();

		int counter = 0;

		// getting index of old items...
		for(List<String> a : original){
			int rel_time = Integer.parseInt(a.get(a.size()-1));
			if(time > rel_time+60){
				removes.add(counter);
			}
			counter++;
		}

		Collections.reverse(removes);
		// actually remove old items from original....
		for(int a: removes){
			original.remove(a);
		}


		//calculate degree now.. 1.create hash
		for(List<String> a : original){
			if(a.size()>2){ // if size 0 or 1, not necessary to calculate.
				for(int j=0; j<a.size()-1;j++){

					String key = a.get(j);
					List<String> val = new ArrayList<>();

					//generate key-value map. key is index, and value is connected index list
					if(calculated.containsKey(key)){
						val = calculated.get(key);
					} else{
						calculated.putIfAbsent(key, new ArrayList<>());
					}
					//generate key-value map. key is index, and value is connected index list
					for(int k=0; k< a.size()-1; k++){
						if(key == a.get(k)){
							;
						} else{
							if(k== a.size()-2){
								//insert val right now
								calculated.replace(key, val);
							}else{

								if(!val.contains(a.get(k)))
									val.add(a.get(k));
							}
						}
					}
				}
			}
		}

		double vertices = 0.0;
		double sum = 0.0;
		//print out calculated degrees...
		for(String b : calculated.keySet()){
			vertices++;
			sum += calculated.get(b).size();
		}

		if(vertices == 0){
			pw1.println(0);
		}
		else{
			DecimalFormat df = new DecimalFormat("#.##");
			pw1.println(df.format(sum/vertices));
		}
    }
}
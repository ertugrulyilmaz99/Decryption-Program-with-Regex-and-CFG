//libraries
import java.io.File;  // Import the File class
import java.io.IOException;  // Import the IOException class to handle errors
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner; // Import the Scanner class to read text files
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class main {

	public static void main(String[] args) {

		try  //reading input_file
		{  
			File input_file=new File("src/original.txt");    //opens a new file which is input file  
			FileReader fr=new FileReader(input_file);   //reads the file  
			BufferedReader br=new BufferedReader(fr);  //creates a buffering character input stream  
			StringBuffer sb=new StringBuffer();    //constructs a string buffer with no characters  
			String line;  

			File intermediate_file = new File("src/revert.txt"); //creates file which is intermediate file
			FileWriter writer = new FileWriter(intermediate_file); //open writing on the file

			//regex
			while((line=br.readLine())!=null)  // read every line
			{

				line=date(line); // Calls date regex function
				line=time(line); // Calls time regex function
				line=extension(line); // extension  regex function
				line=phoneNumber(line); // Calls phone number regex function
				line=verb(line); // Calls verb regex function
				line=passives_to_plural(line); // Calls passives_to_pural regex function

				writer.write(line+"\n");//open writing on the file

			}

			writer.close(); 

			File output_file = new File("src/decrypted.txt");//creates a new file which is output file 
			fr=new FileReader(intermediate_file);   //reads the file  
			br=new BufferedReader(fr);  //creates a buffering character input stream  
			sb=new StringBuffer();    //constructs a string buffer with no characters  
			FileWriter writer1 = new FileWriter(output_file);


			while((line=br.readLine())!=null) //CFG 
			{

				String[] str=line.split(" "); // Splitting a line according to space characters
				ArrayList<String> str2=new ArrayList<String>(); // Storing every line in arrayList
				for(int i=0;i<str.length;i++) {
					str2.add(str[i]);
				}

				String return_number=S(str2); //CFG starting by calling S function

				if(return_number.equals("1")) { //just write the lines which are valid
					writer1.write(line+"\n");
				}
				//else {writer1.write(line+" THE LINE IS NOT VALID"+"\n");} 

			}
			writer1.close();

			sb.append(line);      //appends line to string buffer  
			sb.append("\n");     //line feed   

			fr.close();    //closes the stream and release the resources  

		}  
		catch(IOException e)  
		{  
			e.printStackTrace();  
		} 

	}

	public static String date( String line) {// 17.01.2021 -> 10.12.2017

		line=line.replaceAll("([0-9])([0-9])(.)([0-9])([0-9])(.)([0-9])([0-9])([0-9])([0-9])", "$1$4$3$5$9$6$7$8$10$2");
		//String text2= text1.replaceAll("([0-9])([0-9])(/)([0-9])([0-9])(/)([0-9])([0-9])([0-9])([0-9])", "$1$4$3$5$9$6$7$8$10$2");

		return line;
	}

	public static String extension(String line) { // we are converting animal names in extensions

		line=line.replaceAll("(([0-9]*[a-z]*)*)(.cat)", "$1.txt"); //.cat ->.txt
		line=line.replaceAll("(([0-9]*[a-z]*)*)(.mouse)", "$1.docx");//.mouse ->.docx
		line=line.replaceAll("(([0-9]*[a-z]*)*)(.dog)", "$1.pdf");//.dog ->.pdf

		return line;
	}

	public static String phoneNumber(String line) {//convert IBAN to phone number 

		line=line.replaceAll("(TR[0-9][0-9])(\\s)([0-9][0-9][0-9])([0-9])(\\s)([0-9][0-9][0-9])([0-9])(\\s)([0-9][0-9][0-9])([0-9])(\\s)([0-9])([0-9][0-9][0-9])(\\s)([0-9][0-9][0-9][0-9])(\\s)([0-9][0-9])","$6-$3-$12$9"); //TR00 357 5310 6640 8000 0000 00 ->  531-357-8664
		line=line.replaceAll("(IBAN)(\\s)*:","Phone number :"); // IBAN -> Phone number :


		return line;
	}

	public static String verb( String line) {//MODAL VERB CONVERTING AND PAST TO FUTURE CONVERTING 

		line=line.replaceAll("(She|It|He)(\\s)(?!is)(([a-z])*)(ed)","$1 will be able to $3");//for example :He/She/It wanted-> He/She/It will be able to want (we eliminated passive in regex)
		line=line.replaceAll("(They|We|You)(\\s)(?!are)(([a-z])*)(ed)","$1 will be able to $3"); //for example : They/We/You wanted -> They/We/You will be able to want (we eliminated passive in regex)
		line=line.replaceAll("(I)(\\s)(?!am)(([a-z])*)(ed)","$1 will be able to $3"); //for example : I looked -> I will be able to look (we eliminated passive in regex)


		line=line.replaceAll("will","won't"); //MODAL VERB REPLACINGS AS NEGATIVE OR POSITIVE
		line=line.replaceAll("couldn't","could");
		line=line.replaceAll("might not","might");
		line=line.replaceAll("may not","may");

		return line;
	}

	public static String time( String line) { //TIME CONVERTING

		line=line.replaceAll("([0-9])([0-9])(:)([0-9])([0-9])","$1$5$3$4$2"); //REPLACING THE TIME AS 16:09 -> 19:06
		line=line.replaceAll("([1])([0-9])(:)([0-9])([0-9])","0$5$3$4$2"); // to make change about hours without exceeding the time (afternoon and evening will be shown as morning)
		line=line.replaceAll("([0])([1-9])(:)([0-9])([0-9])","1$5$3$4$2"); //to make change about hours without exceeding the time(morning and after night will be shown as afternoon or evening)

		return line;
	}

	public static String passives_to_plural( String line) { //passive to plural without passive


		line=line.replaceAll("(([a-zA-Z])*)(\\s)(is)(\\s)(([a-z])*)(ed)(\\s)(by)(\\s)(([a-z])*)(.)","$1 and $12 are $6ing.");//Dog is looked by human. -> Dog and human are looking.
		line=line.replaceAll("(([a-zA-Z])*)(\\s)(are)(\\s)(([a-z])*)(ed)(\\s)(by)(\\s)(([a-z])*)(.)","$1 and $12 are $6ing.");//Dog and Cat are looked by human. -> Dog and Cat and human are looking.

		return line;
	}




	//CFG FUNCTIONS STARTING HERE
	//IN HERE BASICLY I TAKE THE INPUT LINE BY LINE AS ARRAYLIST AND I USED TO STR.GET(0) TO TAKE FIRST WORD OF THE LINE, THEN I ADDED .EQUALS("string").So it is str.get(0).equals("string") because I try to
	//find them in a sequence because after controlling that String every time I will remove by using str.remove(0) like a queue. (FIRST IN FIRST OUT). 
	//return 0 = INVALID, return 1= VALID
	public static String S(ArrayList<String> str) { //CFG STARTER VARIABLE

		if(str.get(0).equals("Cat") || str.get(0).equals("He")|| str.get(0).equals("She")|| str.get(0).equals("It") ) { //I used "||" because it can be one of them, and if it is then remove that where is first place of arraylist. 
			str.remove(0);
			return O(str);//O 
		}else if (str.get(0).equals("I")) {
			str.remove(0);
			return J(str); //J
		}else if (str.get(0).equals("You") || str.get(0).equals("They")|| str.get(0).equals("We") ) {
			str.remove(0);
			return P(str); //P
		}else {
			return "0"; //INVALID
		}
	}
	public static String A(ArrayList<String> str) { // A VARIABLE

		if( str.get(0).equals("won't") && str.get(1).equals("be") && str.get(2).equals("able") && str.get(3).equals("to")){
			str.remove(0);
			str.remove(0);
			str.remove(0);
			str.remove(0); //to remove "won't be able to" like a queue
			return B(str); //B
		}else if(str.get(0).equals("could")|| str.get(0).equals("won't")|| str.get(0).equals("might")|| str.get(0).equals("may"))  {
			str.remove(0);
			return B(str); //B

		}else if(str.get(0).equals("and")) {
			str.remove(0);
			return L(str); //L
		}else {
			return "0"; //INVALID
		}
	}

	public static String J(ArrayList<String> str) {//J VARIABLE

		if( str.get(0).equals("am")){
			str.remove(0);
			return G(str); //G
		}else {
			return A(str); //A
		}
	}

	public static String O(ArrayList<String> str) {//0 VARIABLE

		if( str.get(0).equals("is")){
			str.remove(0);
			return G(str); //G
		}else {
			return A(str); //A
		}
	}
	public static String P(ArrayList<String> str) {//P VARIABLE

		if( str.get(0).equals("are")){
			str.remove(0);
			return G(str); //G
		}else {
			return A(str); //A
		}
	}

	public static String L(ArrayList<String> str) {//L VARIABLE

		if( str.get(0).equals("dog") && str.get(1).equals("are") ){
			str.remove(0);
			str.remove(0);
			return K(str); //K
		}else if(str.get(0).equals("human")  && str.get(1).equals("are"))  {
			str.remove(0);
			str.remove(0);
			return K(str); //K
		}else {
			return "0";
		}
	}

	public static String K(ArrayList<String> str) {//K VARIABLE

		if( str.get(0).equals("looking.") ){
			str.remove(0); 
			return D(str); //D
		}else {
			return "0";
		}
	}

	public static String G(ArrayList<String> str) {//G VARIABLE

		if( str.get(0).equals("wanted") || str.get(0).equals("looked") || str.get(0).equals("forwarded")|| str.get(0).equals("waited")){ //it can be in a sentence
			str.remove(0); 
			return C(str); //C
		}else if(str.get(0).equals("wanted.") || str.get(0).equals("looked.") || str.get(0).equals("forwarded.")|| str.get(0).equals("waited.")){ //we can end the sentence with them
			str.remove(0); 
			return D(str); //C
		}else {
			return "0";
		}
	}

	public static String B(ArrayList<String> str) {//B VARIABLE

		if(str.get(0).equals("want") || str.get(0).equals("look") || str.get(0).equals("forward")|| str.get(0).equals("wait")){
			str.remove(0); 
			return C(str); //C
		}else if(str.get(0).equals("want.") || str.get(0).equals("look.") || str.get(0).equals("forward.")|| str.get(0).equals("wait.")){
			str.remove(0); 
			return D(str); //C
		}else {
			return "0";
		}
	}

	public static String C(ArrayList<String> str) {//C VARIABLE
		if( str.get(0).equals("to") && str.get(1).equals("Phone") && str.get(2).equals("number") && str.get(3).equals(":")&& str.get(4).equals("123-456-0789.")){
			str.remove(0);
			str.remove(0);
			str.remove(0);
			str.remove(0);
			str.remove(0);//removing like in FIFO logic
			return D(str); //D
		}else if((str.get(0).equals("at") && str.get(1).equals("10-12-2017.")) || (str.get(0).equals("at") && str.get(1).equals("12:12.")) )  {
			str.remove(0);
			str.remove(0);
			return D(str); //D

		}else if (str.get(0).equals("abc.txt.") || str.get(0).equals("abc.pdf.") || str.get(0).equals("abc.docx.")) {
			str.remove(0);
			return D(str); //D
		}else if(str.get(0).equals("abcd.txt.") || str.get(0).equals("abcd.pdf.") || str.get(0).equals("abcd.docx.")) {
			str.remove(0);
			return D(str); //D
		}else if(str.get(0).equals(".")) {
			str.remove(0);
			return D(str); //D
		}else if(str.get(0).equals("at") && str.get(1).equals("19:06.")) { //at 19:06.
			str.remove(0);
			str.remove(0);
			return D(str); //D
		}else {
			return "0";
		}
	}

	public static String D(ArrayList<String> str) {//D VARIABLE

		if (!str.isEmpty()) {
			return S(str);//More than one sentence can be in a line
		}else {
			return "1";//VALID
		}

	}

}
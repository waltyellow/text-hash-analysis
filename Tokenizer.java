import java.util.*; // auto-import
import java.nio.file.*;
import java.io.*;

public class Tokenizer{
  
//Fields
  private ArrayList<String> wordList;
  private int count;
  
  
  
  
  //constructor with String[] input
  public Tokenizer(String[] input){
    //create an arrayrist, estimating that 2x of input size
  
    this.wordList = new ArrayList<String>(2*input.length);
    //look at each elements and split them by white space
    System.out.println(input.length);
    for(int i = 0; i < input.length; i++){
      
      //Split each string by white spaces
      String[] adding = 
        input[i].split("\\s");
      //look at each of these splited "words"
      for (int j = 0; j < adding.length;j++)
        //Delete all non letters, transfer all to lower cases, and delete all leading-trailing spaces
        wordList.add(adding[j].replaceAll("[^a-zA-Z]","").toLowerCase().trim());
    }
    //Trim the list

    this.normalizer();
  }
  /*
  public Tokenizer(String[] regulated, boolean id){
    //create an arrayrist, estimating that 3x of input size
    this.wordList = new ArrayList<String>(regulated.length);
    //look at each elements and split them by white space
    for(int i = 0; i < regulated.length; i++){
      wordList.add(regulated[i]);
    }
  }*/
  
  
  //constructor with string file name input
  public Tokenizer(String file) throws java.io.IOException{
    BufferedReader inputStream = new BufferedReader(new FileReader(file));
    this.wordList = new ArrayList<String>();
    String nextLine = inputStream.readLine();
    while (nextLine != null){
      String[] adding = nextLine.split("\\s");
      for (int i = 0; i<adding.length;i++)
        wordList.add(adding[i].replaceAll("[^a-zA-Z]","").toLowerCase().trim());
      nextLine = inputStream.readLine();
    }
    //Now all are read, we normalize.
    this.normalizer();
  }
  
  
  //Return the created wordlist
  public ArrayList<String> wordList(){
    return this.wordList;
  }
  
  
  //Private method: normalize words
  private void normalizer(){
    //iterate through the list, and delete empty items
    while (wordList.remove(""))
    this.wordList.trimToSize();
  }
}
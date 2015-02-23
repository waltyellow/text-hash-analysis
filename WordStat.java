//@author Zhenglin Huang
import java.util.*; // auto-import
import java.nio.file.*;
import java.io.*;

public class WordStat{
  
  ArrayList<String> wordList;
  ArrayList<String> wordPairList;
  HashTable wordHash;
  HashTable wordPairHash;
  HashEntry[] rankedWord;
  HashEntry[] rankedWordPair;
  
  public WordStat(String fileName) throws java.io.IOException{
    Tokenizer t = new Tokenizer(fileName);
    wordList = t.wordList();
    if (wordList.size() == 0)
    {
      rankedWord = new HashEntry[0];
    }else{ 
      if (wordList.size() == 1){
        //one case
        
        executeWordCount();
        executeRank();
        
      }else{
        //two cases
        executeWordCount();
        executePairCount();
        executeRank();
        executePairRank();
      }
    }
  }
  
  public WordStat(String[] input) {
    Tokenizer t = new Tokenizer(input);
    wordList = t.wordList();
    if (input.length == 0){
      //input nothing, and we have nothing to give you
      rankedWord = new HashEntry[0];
    }else{
      //one or more
      if (input.length == 1)
      {
        //and it's ranked
        //just put one in the table, there would be no pair
        executeWordCount();
        executeRank();
      }else{
        //we have two or more words
        executeWordCount();
        executePairCount();
        executeRank();
        executePairRank();
      }
    }
  }
  
  public int wordCount(String word){
    //handles zero case when there is nothing in the table
    if (rankedWord.length == 0)
      return 0;
    String input = word.trim().replaceAll("[^a-zA-Z]","").toLowerCase();
    int count = wordHash.get(input);
    if (count == -1)
      return 0;
    else
      return count;
  }
  
  public int wordPairCount(String w1, String w2){
    //handles 0 and 1 case when there is no word pairs at all
    if (rankedWord.length == 0)
      return 0;
    if (rankedWord.length == 1)
      return 0;
    w1 = w1.trim().replaceAll("[^a-zA-Z]","").toLowerCase();
    w2 = w2.trim().replaceAll("[^a-zA-Z]","").toLowerCase();
    String input = w1+" "+w2;
    int count = wordPairHash.get(input);
    if (count == -1)
      return 0;
    else
      return count;
  }
  
  public int wordRank(String word){
    word = word.trim().replaceAll("[^a-zA-Z]","").toLowerCase();
    if (rankedWord.length == 0)
      return 0;
    return wordHash.getRank(word);
  }
  
  public int wordPairRank(String w1, String w2){
    w1 = w1.trim().replaceAll("[^a-zA-Z]","").toLowerCase();
    w2 = w2.trim().replaceAll("[^a-zA-Z]","").toLowerCase();
    String input = w1+" "+w2;
    return wordPairHash.getRank(input);
  }
  
  public String[] mostCommonWords(int k){
    //0, 1 and many case checked, as rankedWord could be zero
    if (k > rankedWord.length)
      k = rankedWord.length;
    String[] output = new String[k];
    for(int i = 0; i < k; i++){
      output[i] = rankedWord[i].getKey();
    }
    return output;
  }
  
  public String[] leastCommonWords(int k){
    if (k > rankedWord.length)
      k = rankedWord.length;
    String[] output = new String[k];
    //0, 1 and many case checked, as rankedWord could be zero
    //reverse the alphabetical order
    incComparatorAlphabetReversed comparator = new incComparatorAlphabetReversed();
    java.util.Arrays.sort(rankedWord,comparator);
    
    for(int i = 0; i < k; i++){
      output[i] = rankedWord[rankedWord.length-1-i].getKey();
    }
    return output;
  }
  
  public String[] mostCommonWordPairs(int k){
    if (rankedWord.length == 1)
      return null;
    else{
      if (k > rankedWordPair.length)
        k = rankedWordPair.length;
      String[] output = new String[k];
      for(int i = 0; i < k; i++){
        output[i] = rankedWordPair[i].getKey();
      }
      return output;
    }
  }
  
  public String[] mostCommonCollocs(int k, String baseWord, int i){
    baseWord = baseWord.trim().replaceAll("[^a-zA-Z]","").toLowerCase();
    String[] neighbors = neighborSearch(baseWord, i);
    WordStatLite counter = new WordStatLite(neighbors);
    return counter.mostCommonWords(k);
  }
  
  public String[] mostCommonCollocsExc(int k, String baseWord, int i, String [] exclusions){
    baseWord = baseWord.trim().replaceAll("[^a-zA-Z]","").toLowerCase();
    String[] neighbors = neighborSearchExc(baseWord, i, exclusions);
    WordStatLite counter = new WordStatLite(neighbors);
    return counter.mostCommonWords(k);
  }
  
  public String generateWordString(int k, String startword){
    startword = startword.trim().replaceAll("[^a-zA-Z]","").toLowerCase();
    StringBuilder so = new StringBuilder();
    String nextWord = startword;
    //append k-1 times, and now we stay at the kth word
    for (int i = 1; i < k; i++){
      so.append(nextWord);
      so.append(" ");
      String[] next = mostCommonCollocs(1, nextWord, 1);
      if (next.length > 0)
        nextWord = next[0];
      else
        nextWord = "-ErrorErrorError-";
    }
    so.append(nextWord);
    return so.toString();
  }
  
  public String generateRandomWordString(int k, String startword){
    startword = startword.trim().replaceAll("[^a-zA-Z]","").toLowerCase();
    StringBuilder so = new StringBuilder();
    String nextWord = startword;
    //append k-1 times, and now we stay at the kth word
    for (int i = 1; i < k; i++){
      so.append(nextWord);
      so.append(" ");
      String[] next = neighborSearch(nextWord,1);
      if (next.length > 0)
        nextWord = next[new java.util.Random().nextInt(next.length)];
      else
        nextWord = "the";
    }
    so.append(nextWord);
    return so.toString();
  }
  
  public String generateWordStringWithoutRepeats(int k, String startword){
    startword = startword.trim().replaceAll("[^a-zA-Z]","").toLowerCase();
    StringBuilder so = new StringBuilder();
    String nextWord = startword;
    ArrayList<String> exclusion = new ArrayList<String>(k-1);
    //append k-1 times, and now we stay at the kth word
    for (int i = 1; i < k; i++){
      so.append(nextWord);
      exclusion.add(nextWord);
      so.append(" ");
      String[] next = mostCommonCollocs(10000, nextWord, 1);
      if (next.length > 0){
        int j = 0;
        boolean notFound = true;
        while ((notFound) && (j < next.length) ){
          if (!exclusion.contains(next[j])){
            nextWord = next[j];
            notFound = false;
          }
          j++;
        }
        if (notFound)
          nextWord = " the";
      }else{
        nextWord = " the";
      }
    }
    so.append(nextWord);
    return so.toString();
  }
  
  
  public String generateWordStringWithoutRepeatedPairs(int k, String startword){
    startword = startword.trim().replaceAll("[^a-zA-Z]","").toLowerCase();
    StringBuilder so = new StringBuilder();
    String nextWord = startword;
    String previousWord = startword;
    ArrayList<String> exclusion = new ArrayList<String>(k);
    
    //first case
    so.append(nextWord);
    so.append(" ");
    String[] next = mostCommonCollocs(1, nextWord, 1);
    if (next.length > 0){
      nextWord = next[0];
    }else{
      nextWord = "the";
    }
    
    //each time a new word it's found, it becomes the previous word
    //append k-2 times, and now we stay at the kth word
    for (int i = 2; i < k; i++){
      so.append(nextWord);
      so.append(" ");
      exclusion.add(previousWord+" "+nextWord);
      next = mostCommonCollocs(10000, nextWord, 1);
      if (next.length > 0){
        int j = 0;
        boolean notFound = true;
        while ((notFound) && (j < next.length) ){
          if (!exclusion.contains(nextWord+" "+next[j])){
            previousWord = nextWord;
            nextWord = next[j];
            notFound = false;
          }
          j++;
        }
        if (notFound)
          nextWord = "the";
      }else{
        nextWord = ". The";
      }
    }
    so.append(nextWord);
    return so.toString();
  }
  
  /*
   * 
   * 
   Welcome to my massive array of private helper methods
   
   Each of them handles a certain funtion
   
   
   
   */
  
  //Pull out all the collocations, given a word and parameters, output in a string array.
  private String[] neighborSearch(String base, int position){
    ArrayList<String> neighbors = new ArrayList<String>(750);
    
    //Feeding the words
    //i = -1 case, preceeding
    if (position == -1){
      for (int i = 1; i < wordList.size(); i++){
        if (wordList.get(i).equals(base))
          neighbors.add(wordList.get(i-1));
      }
    }
    
    //i = 1 case, succeeding
    if (position == 1){
      for (int i = 0; i < (wordList.size() - 1); i++){
        if (wordList.get(i).equals(base))
          neighbors.add(wordList.get(i+1));
      }
    }
    
    //output these neighbors
    String[] output = new String[neighbors.size()];
    
    for(int i = 0; i < neighbors.size(); i++)
      output[i] = neighbors.get(i);
    return output;
    
    //return all collocations for base word in respect for position
    //output could be 0 string, 1 string or many strings.
  }
  
  //same as the previous one, except has a filter to exclude unwanted words
  private String[] neighborSearchExc(String base, int position, String[] exc){
    ArrayList<String> neighbors = new ArrayList<String>(750);
    ArrayList<String> exclusions = new ArrayList<String>(exc.length);
    //Convert the exclusions to an array
    for (int i = 0; i < exc.length; i++){
      exclusions.add(exc[i]);
    }
    
    //i = -1 case, preceeding, and not excluded
    if (position == -1)
      for (int i = 1; i < wordList.size(); i++){
      if ((wordList.get(i).equals(base)) && (!exclusions.contains(wordList.get(i-1))))
        neighbors.add(wordList.get(i-1));
    }
    
    //i = 1 case, succeeding
    if (position == 1)
      for (int i = 0; i < (wordList.size() - 1); i++){
      //if the +1 neighbor we are looking at is not exclueded
      if ((wordList.get(i).equals(base)) && (!exclusions.contains(wordList.get(i+1))))
        neighbors.add(wordList.get(i+1));
    }
    
    //Analyse these neighbors
    String[] output = new String[neighbors.size()];
    for(int i = 0; i < neighbors.size(); i++)
      output[i] = neighbors.get(i);
    return output;
    
    //return all collocations for base word in respect for position
    //output could be, 0, 1, or many strings array
  }
  
  //Generate wordpairs by connecting a word and its succeeding word
  private void wordPairGenerator(){
    if (wordList.size() > 1){
      wordPairList = new ArrayList<String>(wordList.size()-1);
      for (int i = 0; i < wordList.size() -1; i++){
        wordPairList.add(wordList.get(i)+" "+wordList.get(i+1));
      }
    }
  }
  
  //Create a hash table instance and count all words. Overprovisioning ensured that rehashing is minimal
  private void executeWordCount(){
    if (wordList.size() < 25000)
      this.wordHash = new HashTable(wordList.size());
    else
      this.wordHash = new HashTable(25000);
    //handled the "1" case automatically
    for (int i = 0; i < wordList.size(); i++){
      //key
      int currentCount = wordHash.get(wordList.get(i));
      if (currentCount == -1)
        this.wordHash.update(wordList.get(i),1);
      else
        this.wordHash.update(wordList.get(i),currentCount+1);     
    }
  }
  
  //Do the same function as the previous one, except working on pairs
  private void executePairCount(){
    wordPairGenerator();
    if (wordList.size() < 25000)
      this.wordPairHash = new HashTable(wordList.size());
    else
      this.wordPairHash = new HashTable(25000);
    
    for (int i = 0; i < wordPairList.size(); i++){
      
      int currentCount = wordPairHash.get(wordPairList.get(i));
      if (currentCount == -1)
        this.wordPairHash.update(wordPairList.get(i),1);
      else
        this.wordPairHash.update(wordPairList.get(i),currentCount+1);
    }
  }
  
  //Sort the items by left(largest occurance, like "the", "a", "to")->right(least occurance, 1)
  //When there is an equal value, left to right is sorted by a->z.
  private void executeRank(){
    LinkedList<HashEntry> puller = wordHash.toLinkedList();
    rankedWord = new HashEntry[puller.size()];
    for(int i = 0; i < rankedWord.length; i++)
      rankedWord[i] = puller.poll();
    
    incComparator comparator = new incComparator();
    java.util.Arrays.sort(rankedWord,comparator);
    
    //make sure the first rank has 1, and sets an example for the rest
    int previousCount = rankedWord[0].getValue();
    int previousRank = 1;
    
    for(int i = 0; i < rankedWord.length; i++){
      if (rankedWord[i].getValue() == previousCount)
        //match up with the previous item's rank
        rankedWord[i].rank = previousRank;
      
      else{
        //make this one the first of its row, fixate its new rank
        rankedWord[i].rank = i + 1;
        previousRank = i + 1;
        previousCount = rankedWord[i].getValue();
      }
      
    }
  }
  
  //Same function for the word pair
  private void executePairRank(){
    LinkedList<HashEntry> puller = wordPairHash.toLinkedList();
    rankedWordPair = new HashEntry[puller.size()];
    for(int i = 0; i < rankedWordPair.length; i++)
      rankedWordPair[i] = puller.poll();
    
    incComparator comparator = new incComparator();
    java.util.Arrays.sort(rankedWordPair,comparator);
    
    //make sure the first rank has 1, and sets an example for the rest
    int previousCount = rankedWordPair[0].getValue();
    int previousRank = 1;
    
    for(int i = 0; i < rankedWordPair.length; i++){
      if (rankedWordPair[i].getValue() == previousCount)
        //match up with the previous item's rank
        rankedWordPair[i].rank = previousRank;
      else{
        
        //make this one the first of its row, fixate its new rank
        rankedWordPair[i].rank = i + 1;
        previousRank = i + 1;
        previousCount = rankedWordPair[i].getValue();
      }
    }
  }
  
  //The comparator
  //Sort the items by left(largest occurance, like "the", "a", "to")->right(least occurance, 1)
  //When there is an equal value, left to right is sorted by a->z.
  private class incComparator implements Comparator<HashEntry>{
    public int compare(HashEntry e1, HashEntry e2){
      if (e1.getValue() < e2.getValue())
        return 1;
      if (e1.getValue() > e2.getValue())
        return -1;
      if ((e1.getKey().compareTo(e2.getKey())) < 0)
        return -1;
      if ((e1.getKey().compareTo(e2.getKey())) > 0)
        return 1;
      return 0;
    }
  }
  
  //Here's the difference:
  //When there is an equal value, left to right is sorted by z->a instand of a->z
  private class incComparatorAlphabetReversed implements Comparator<HashEntry>{
    public int compare(HashEntry e1, HashEntry e2){
      if (e1.getValue() < e2.getValue())
        return 1;
      if (e1.getValue() > e2.getValue())
        return -1;
      if ((e1.getKey().compareTo(e2.getKey())) < 0)
        return 1;
      if ((e1.getKey().compareTo(e2.getKey())) > 0)
        return -1;
      return 0;
    }
    
    public boolean equals(HashEntry e1){
      return false;
    }
  }
  
  
  
  //A wordstat class without pair functions
  private class WordStatLite{
    ArrayList<String> wordList;
    HashTable wordHash;
    HashEntry[] rankedWord;
    //int initialCapacity;
    
    public WordStatLite(String[] input) {
      wordList = new ArrayList<String>(input.length);
      //rationale: 3000 common English words plus half of the words pretty unique      
      for(int i = 0; i < input.length; i++){
        wordList.add(input[i]);
      }
      if (input.length == 0){
        rankedWord = new HashEntry[0];
      }else{
        if (input.length == 1)
        {
          //just put one in
          rankedWord = new HashEntry[1];
          rankedWord[0] = new HashEntry(input[0],1);
        }else{
          //case where there are many items
          executeWordCount();
          executeRank();
        }
      }
    }
    
    public String[] mostCommonWords(int k){
      if (rankedWord == null)
        return new String[0];
      if (k > rankedWord.length)
        k = rankedWord.length;
      String[] output = new String[k];
      for(int i = 0; i < k; i++){
        output[i] = rankedWord[i].getKey();
      }
      return output;
    }
    
    
    private void executeWordCount(){
      //In a 300k word text, the most frequent word, "the" appeared 17693 times, which is about 6%
      //We design to handle 160k word documents before performance start to slightly decreases
      //Rehashing is still considered inefficient compared to this
      if (wordList.size() < 5000){
        this.wordHash = new HashTable(wordList.size());
      }else{
        this.wordHash = new HashTable(10000);
      }
      
      for (int i = 0; i < wordList.size(); i++){
        //get the count
        int currentCount = wordHash.get(wordList.get(i));
        if (currentCount == -1)
          this.wordHash.update(wordList.get(i),1);
        else
          this.wordHash.update(wordList.get(i),currentCount+1);     
      }
    }
    
    //order them
    private void executeRank(){
      LinkedList<HashEntry> puller = wordHash.toLinkedList();
      if (puller.size() > 1){
        rankedWord = new HashEntry[puller.size()];
        for(int i = 0; i < rankedWord.length; i++)
          rankedWord[i] = puller.poll();
        
        incComparator comparator = new incComparator();
        java.util.Arrays.sort(rankedWord,comparator);
        
      }
    }
    
    private class incComparator implements Comparator<HashEntry>{
      public int compare(HashEntry e1, HashEntry e2){
        if (e1.getValue() < e2.getValue())
          return 1;
        if (e1.getValue() > e2.getValue())
          return -1;
        if ((e1.getKey().compareTo(e2.getKey())) < 0)
          return -1;
        if ((e1.getKey().compareTo(e2.getKey())) > 0)
          return 1;
        return 0;
      }
    }
    
  }
}
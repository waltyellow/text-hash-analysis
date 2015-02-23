import java.util.*; // auto-import

public class HashEntry {
  
//Fields
  private String key;
  private int value;
  //extra
  //external rank
  public int rank;
  private HashEntry next;
  
  
  //constructor with key and value
  public HashEntry(String key, int value){
    this.key = key;
    setValue(value);
    this.next = null;
  }
  
  public HashEntry getNext(){
    return next;
  }
  
  public void setNext(HashEntry next){
    this.next = next;
  }
  
  //get, set for value, and key
  
  public int getValue(){
    return this.value;
  }
  
  public String getKey(){
    return this.key;
  }
  
  public void setValue(int value){
    this.value = value;
  }
  
}
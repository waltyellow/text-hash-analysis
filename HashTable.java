import java.util.*; // auto-import

public class HashTable{
  //fields
  private HashEntry[] table;
  private int tableCapacity;
  private int count;
  
  //constructor with no argument
  public HashTable(){
    //default size would 10007, a prime number larger than 10000(normal medium literature work has ~10000 words)
    //call the other constructor to implement this size
    this(10007);
  }
  
  //constructor with table size specified
  public HashTable(int initSize){
    //set the capacity to this size
    this.tableCapacity = initSize;
    //then, create this table, with all null elements
    table= new HashEntry[tableCapacity];
    count = 0;
  }
  
  //rehash the table when we change the size
  public void rehash(int newCap){
    //set the new capacity
    this.tableCapacity = newCap;
    //set the cooresponding new table
    HashEntry[] oldTable= table;
    table= new HashEntry[tableCapacity];
    
    //pull all index items from the old table and rehash them
    for(int i = 0; i < oldTable.length; i++){
      HashEntry location = oldTable[i];
      //iterate through the chain on each index
      while(location != null){
        //rehash each item in the chain
        put(location.getKey(),location.getValue());
        location = location.getNext();
      }
      //At this point, the chain at index i has been handled, we move on to the next index
    }
  }
  
  //Rehash when necesary
  public void rehashCheck(){
    if (count > tableCapacity)
      rehash(tableCapacity*2+1);
  }
  
  //call the other function with the java hashCode
  public void put(String key, int value){
    put(key,value,key.hashCode());  
  }
  
  
  public void put(String key, int value, int hashCode){
    //Find an index to hash in
    int index = (Math.abs(hashCode) % (this.tableCapacity));
    //iterate through the chain
    HashEntry location = table[index];
    if (location == null){
      table[index] = new HashEntry(key,value);
      count++;
      return;
    }
    //Find the end of the chain
    while (location.getNext() != null){
      location = location.getNext();
    }
    //Next is the end "null" of the chain,location is the last notnull item
    location.setNext(new HashEntry(key,value));
    count++;
  }
  
  //Update an value, if not existent, add it
  public void update(String key, int value){
    //hash an index
    int index = (Math.abs(key.hashCode()) % (this.tableCapacity));
    HashEntry location = table[index];
    if (location == null){
      table[index] = new HashEntry(key,value);
      return;
    }
    HashEntry tail = location;
    //Find the end of the chain
    while (location != null){
      if (location.getKey().equals(key)) {
        //found it, add value
        location.setValue(value);
        return;
      }
      tail = location;
      location = location.getNext();
    }
    //now this index has no corresponding item updated, tail is the last item, location is the end "null"
    //we, happily, add a new thing to the end
    tail.setNext(new HashEntry(key,value));
    count++;
  }
  
  //retrieve a key automatically
  public int get(String key){
    return get(key,key.hashCode());  
  }
  
  //retrieve a key with specific hashcode
  public int get(String key, int hashCode){
    int index = (Math.abs(hashCode) % (this.tableCapacity));
    HashEntry location = table[index];
    //Find the end of the chain
    while (location != null){
      if (location.getKey().equals(key))
        return location.getValue();
      location = location.getNext();
    }
    return -1;
  }
  
  //retrieve a stored numerical rank("1 1 3 3 3 6...") for a key-value pair
  public int getRank(String key){
    return getRank(key,key.hashCode());  
  }
  
  private int getRank(String key, int hashCode){
    int index = (Math.abs(hashCode) % (this.tableCapacity));
    HashEntry location = table[index];
    //Find the end of the chain
    while (location != null){
      if (location.getKey().equals(key))
        return location.rank;
      location = location.getNext();
    }
    return 0;
  }
  
  //Output all hashentrys into a linkedlist
  public LinkedList<HashEntry> toLinkedList(){
    LinkedList<HashEntry> puller = new LinkedList<HashEntry>();
    for(int i = 0; i < table.length; i++){
      HashEntry location = table[i];
      //iterate through the chain on each index
      while(location != null){
        //put them in the linked list
        puller.add(location);
        location = location.
          getNext();
      }
    }
    return puller;
  }
}
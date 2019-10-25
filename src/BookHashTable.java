import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

// TODO: comment and complete your HashTableADT implementation
//
// TODO: implement all required methods
// DO ADD REQUIRED PUBLIC METHODS TO IMPLEMENT interfaces
//
// DO NOT ADD ADDITIONAL PUBLIC MEMBERS TO YOUR CLASS 
// (no public or package methods that are not in implemented interfaces)
//
// TODO: describe the collision resolution scheme you have chosen
// identify your scheme as open addressing or bucket
//
// if open addressing: describe probe sequence 
// if buckets: describe data structure for each bucket
//
// TODO: explain your hashing algorithm here 
// NOTE: you are not required to design your own algorithm for hashing,
//       since you do not know the type for K,
//       you must use the hashCode provided by the <K key> object

/** HashTable implementation that uses:
 * @param <K> unique comparable identifier for each <K,V> pair, may not be null
 * @param <V> associated value with a key, value may be null
 */
public class BookHashTable implements HashTableADT<String, Book> {

    /** The initial capacity that is used if none is specifed user */
    static final int DEFAULT_CAPACITY = 101;
    
    /** The load factor that is used if none is specified by user */
    static final double DEFAULT_LOAD_FACTOR_THRESHOLD = 0.75;
    private double loadFactorThreshold;
    private ArrayList<LinkedList<BookNode>> hashTable;
    private int capacity;
    private int numKeys;

    /**
     * REQUIRED default no-arg constructor
     * Uses default capacity and sets load factor threshold 
     * for the newly created hash table.
     */
    public BookHashTable() {
        this(DEFAULT_CAPACITY,DEFAULT_LOAD_FACTOR_THRESHOLD);
    }
    
    /**
     * Creates an empty hash table with the specified capacity 
     * and load factor.
     * @param initialCapacity number of elements table should hold at start.
     * @param loadFactorThreshold the ratio of items/capacity that causes table to resize and rehash
     */
    public BookHashTable(int initialCapacity, double loadFactorThreshold) {
        // TODO: comment and complete a constructor that accepts initial capacity 
        // and load factor threshold and initializes all fields
        capacity = initialCapacity;
        this.loadFactorThreshold = loadFactorThreshold;
        hashTable = new ArrayList<>(capacity);
        for(int i = 0; i < capacity; i++)
            hashTable.add(new LinkedList<>());
    }

    private class BookNode{
        private Book book;
        private String key;

        private BookNode(String key, Book book){
            this.book = book;
            this.key = key;
        }
    }

    // Returns the load factor for this hash table
    // that determines when to increase the capacity
    // of this hash table
    @Override
    public double getLoadFactorThreshold() {
        return loadFactorThreshold;
    }

    // Capacity is the size of the hash table array
    // This method returns the current capacity.
    //
    // The initial capacity must be a positive integer, 1 or greater
    // and is specified in the constructor.
    //
    // REQUIRED: When the load factor is reached,
    // the capacity must increase to: 2 * capacity + 1
    //
    // Once increased, the capacity never decreases
    @Override
    public int getCapacity() {
        return hashTable.size();
    }

    // Returns the collision resolution scheme used for this hash table.
    // Implement this ADT with one of the following collision resolution strategies
    // and implement this method to return an integer to indicate which strategy.
    //
    // 1 OPEN ADDRESSING: linear probe
    // 2 OPEN ADDRESSING: quadratic probe
    // 3 OPEN ADDRESSING: double hashing
    // 4 CHAINED BUCKET: array list of array lists
    // 5 CHAINED BUCKET: array list of linked lists
    // 6 CHAINED BUCKET: array list of binary search trees
    // 7 CHAINED BUCKET: linked list of array lists
    // 8 CHAINED BUCKET: linked list of linked lists
    // 9 CHAINED BUCKET: linked list of of binary search trees
    @Override
    public int getCollisionResolutionScheme() {
        return 5;
    }

    // Add the key,value pair to the data structure and increase the number of keys.
    // If key is null, throw IllegalNullKeyException;
    // If key is already in data structure, throw DuplicateKeyException();
    @Override
    public void insert(String key, Book value) throws IllegalNullKeyException, DuplicateKeyException {
        if(key == null)
            throw new IllegalNullKeyException();
        if(search(key, hashTable.get(Math.abs(key.hashCode() % capacity))) != 0)
            throw new DuplicateKeyException();
        else {
            hashTable.get(Math.abs(key.hashCode() % capacity)).add(new BookNode(key, value));
            numKeys++;
            if(numKeys / capacity > loadFactorThreshold)
                resize();
        }
    }

    private void resize() throws IllegalNullKeyException, DuplicateKeyException {
        ArrayList<LinkedList<BookNode>> bookList = new ArrayList<>();
        for(int i = 0; i < capacity; i++) {
            if(hashTable.get(i).size() != 0) {
                bookList.add(hashTable.get(i));
                hashTable.remove(i);
                hashTable.add(new LinkedList<>());
            }
        }
        int newCapacity = 2 * capacity + 1;
        for (int i = 0; i < newCapacity - capacity; i++) {
            hashTable.add(new LinkedList<>());
        }
        capacity = newCapacity;
        for(int i = 0; i < bookList.size(); i++) {
            for(int j = 0; j < bookList.get(i).size(); j++) {
                BookNode bookNode = bookList.get(i).get(j);
                insert(bookNode.key, bookNode.book);
                numKeys--;
            }
        }
    }

    // If key is found,
    //    remove the key,value pair from the data structure
    //    decrease number of keys.
    //    return true
    // If key is null, throw IllegalNullKeyException
    // If key is not found, return false
    @Override
    public boolean remove(String key) throws IllegalNullKeyException {
        if(key == null)
            throw new IllegalNullKeyException();
        else{
            LinkedList<BookNode> bucket = hashTable.get(Math.abs(key.hashCode() % capacity));
            int bookIndex = search(key, bucket);
            if(bookIndex == 0)
                return false;
            else {
                bucket.remove(bookIndex - 1);
                numKeys--;
                return true;
            }
        }
    }

    // Returns the value associated with the specified key
    // Does not remove key or decrease number of keys
    //
    // If key is null, throw IllegalNullKeyException
    // If key is not found, throw KeyNotFoundException().
    @Override
    public Book get(String key) throws IllegalNullKeyException, KeyNotFoundException {
        if(key == null)
            throw new IllegalNullKeyException();
        else {
            int bookIndex = search(key, hashTable.get(Math.abs(key.hashCode() % capacity)));
            if (bookIndex == 0)
                throw new KeyNotFoundException();
            else
                return hashTable.get(Math.abs(key.hashCode() % capacity)).get(bookIndex - 1).book;
        }
    }

    private int search(String key, LinkedList<BookNode> list) {
        int counter = 1;
        for(BookNode node : list) {
            if (node.key.equals(key))
                return counter;
            else
                counter++;
        }
        return 0;
    }

    // Returns the number of key,value pairs in the data structure
    @Override
    public int numKeys() {
        return numKeys;
    }
}
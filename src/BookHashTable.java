import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
/**
 * This HashTable implementation uses chained buckets of an array list that holds linked lists
 * The hashing algorithm used for the HashTable uses the Java hashCode() method, key.hashCode() % TableSize
 * HashTable implementation that uses:
 *
 * @param <K> unique comparable identifier for each <K,V> pair, may not be null
 * @param <V> associated value with a key, value may be null
 * @author Brandon Erickson
 */
public class BookHashTable implements HashTableADT<String, Book> {

    /**
     * The initial capacity that is used if none is specifed user
     */
    static final int DEFAULT_CAPACITY = 101;

    /**
     * The load factor that is used if none is specified by user
     */
    static final double DEFAULT_LOAD_FACTOR_THRESHOLD = 0.75;
    private double loadFactorThreshold; // Load factor threshold used throughout implementation
    private ArrayList<LinkedList<BookNode>> hashTable; // Data structure that stores the hash table
    private int capacity; // Size of the hash table
    private int numKeys; // Number of keys within the hash table

    /**
     * REQUIRED default no-arg constructor
     * Uses default capacity and sets load factor threshold
     * for the newly created hash table.
     */
    public BookHashTable() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR_THRESHOLD);
    }

    /**
     * Creates an empty hash table with the specified capacity
     * and load factor.
     *
     * @param initialCapacity     number of elements table should hold at start.
     * @param loadFactorThreshold the ratio of items/capacity that causes table to resize and rehash
     */
    public BookHashTable(int initialCapacity, double loadFactorThreshold) {
        capacity = initialCapacity;
        this.loadFactorThreshold = loadFactorThreshold;
        hashTable = new ArrayList<>(capacity); // Creates the HashTable
        // Adds a LinkedList bucket to every index of the HashTable
        for (int i = 0; i < capacity; i++)
            hashTable.add(new LinkedList<>());
    }

    /**
     * Nodes that store a key/value pair to be stored in HashTable
     *
     */
    private class BookNode {
        private Book book; // Value
        private String key; // Key

        /**
         * Initializes book and key variables
         *
         * @param key - String key that is used to identify node
         * @param book - Book stored within the node
         */
        private BookNode(String key, Book book) {
            this.book = book;
            this.key = key;
        }
    }

    /**
     * Returns the loadFactorThreshold of the HashTable
     *
     * @return - LoadFactorThreshold of the HashTable
     */
    @Override
    public double getLoadFactorThreshold() {
        return loadFactorThreshold;
    }

    /**
     * Returns the capacity of the HashTable
     *
     * @return - Capacity of the HashTable
     */
    @Override
    public int getCapacity() {
        return hashTable.size();
    }

    /**
     * Returns the collision resolution scheme used for this HashTable
     *
     * @return - int corresponding to a collision resolution scheme
     */
    @Override
    public int getCollisionResolutionScheme() {
        return 5;
    }

    /**
     * Adds a given key, value pair to the HashTable
     *
     * @param key - Key of node to be inserted
     * @param value - Value of node to be inserted
     * @throws IllegalNullKeyException - If given key is null
     * @throws DuplicateKeyException - If given key is already in the data structure
     */
    @Override
    public void insert(String key, Book value) throws IllegalNullKeyException, DuplicateKeyException {
        // If given key is null
        if (key == null)
            throw new IllegalNullKeyException();
        // If given key is already in the HashTable
        if (search(key, hashTable.get(Math.abs(key.hashCode() % capacity))) != 0)
            throw new DuplicateKeyException();
        else {
            // Adds node to correct bucket using hash function
            hashTable.get(Math.abs(key.hashCode() % capacity)).add(new BookNode(key, value));
            numKeys++;
            // Checks if HashTable needs to be resized after insert
            if (numKeys / capacity > loadFactorThreshold)
                resize();
        }
    }

    /**
     * Once loadFactorThreshold has been reached, increases the size of the HashTable and rehashes nodes back into the HashTable
     *
     * @throws IllegalNullKeyException - If key to be inserted is null
     * @throws DuplicateKeyException - If node has already been inserted
     */
    private void resize() throws IllegalNullKeyException, DuplicateKeyException {
        // Temporary ArrayList used to store nodes already within HashTable
        ArrayList<BookNode> bookList = new ArrayList<>();
        // Iterates through HashTable collecting buckets
        for (int i = 0; i < capacity; i++) {
            // If bucket isn't empty
            if (hashTable.get(i).size() != 0) {
                // Iterates through bucket adding nodes to temporary ArrayList
                for (int j = 0; j < hashTable.get(i).size(); j++) {
                    bookList.add(hashTable.get(i).get(j));
                }
                hashTable.get(i).clear(); // Clears bucket once all nodes are added to the temporary ArrayList
            }
        }

        int newCapacity = 2 * capacity + 1; // Calculates the new capacity
        // Adds new buckets to the HashTable
        for (int i = 0; i < newCapacity - capacity; i++) {
            hashTable.add(new LinkedList<>());
        }

        capacity = newCapacity;
        // Rehashes all nodes back into the HashTable
        for (int i = 0; i < bookList.size(); i++) {
            BookNode bookNode = bookList.get(i);
            insert(bookNode.key, bookNode.book);
            numKeys--;
        }
    }

    /**
     * Removes a key, value pair from the data structure
     *
     * @param key - Key of node to be removed
     * @return - True if node was removed, false otherwise
     * @throws IllegalNullKeyException - If given key is null
     */
    @Override
    public boolean remove(String key) throws IllegalNullKeyException {
        // If key is null throws an exception
        if (key == null)
            throw new IllegalNullKeyException();
        else {
            // Bucket where key to remove is located
            LinkedList<BookNode> bucket = hashTable.get(Math.abs(key.hashCode() % capacity));
            int bookIndex = search(key, bucket); // Index of node to remove within bucket
            // If key was not found returns false
            if (bookIndex == 0)
                return false;
            // If key was found removes node from LinkedList
            else {
                bucket.remove(bookIndex - 1);
                numKeys--;
                return true;
            }
        }
    }

    /**
     * Returns the value associated with the specified key
     * Does not remove key or decrease number of keys
     *
     * @param key - Key of node to get
     * @return - Value of node to get
     * @throws IllegalNullKeyException - If key is null
     * @throws KeyNotFoundException - If key is not found
     */
    @Override
    public Book get(String key) throws IllegalNullKeyException, KeyNotFoundException {
        // If key is null throws exception
        if (key == null)
            throw new IllegalNullKeyException();
        else {
            // Location within bucket
            int bookIndex = search(key, hashTable.get(Math.abs(key.hashCode() % capacity)));
            // If key was not found throws exception
            if (bookIndex == 0)
                throw new KeyNotFoundException();
            // If key was found returns book
            else
                return hashTable.get(Math.abs(key.hashCode() % capacity)).get(bookIndex - 1).book;
        }
    }

    /**
     * Finds a node's index within a bucket
     *
     * @param key - Key of node to find
     * @param list - Bucket to search within
     * @return - Index of node within bucket
     */
    private int search(String key, LinkedList<BookNode> list) {
        int counter = 1; // Counts amount of nodes iterated through
        // Iterates through LinkedList until key is found or end of LinkedList is reached
        for (BookNode node : list) {
            // If key is found
            if (node.key.equals(key))
                return counter;
            else
                counter++;
        }
        return 0; // If key is not found
    }

    /**
     * Returns the number of key, value pairs in the data structure
     *
     * @return - Number of key, value pairs
     */
    @Override
    public int numKeys() {
        return numKeys;
    }
}
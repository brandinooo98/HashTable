/**
 * Filename:   TestHashTableDeb.java
 * Project:    p3
 * Authors:    Debra Deppeler (deppeler@cs.wisc.edu)
 * 
 * Semester:   Fall 2018
 * Course:     CS400
 * 
 * Due Date:   before 10pm on 10/29
 * Version:    1.0
 * 
 * Credits:    None so far
 * 
 * Bugs:       TODO: add any known bugs, or unsolved problems here
 */

import org.junit.After;
import java.io.FileNotFoundException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/** 
 * Test HashTable class implementation to ensure that required 
 * functionality works for all cases.
 */
public class BookHashTableTest {

    // Default name of books data file
    public static final String BOOKS = "books.csv";

    // Empty hash tables that can be used by tests
    static BookHashTable bookObject;
    static ArrayList<Book> bookTable;

    static final int INIT_CAPACITY = 2;
    static final double LOAD_FACTOR_THRESHOLD = 0.49;
       
    static Random RNG = new Random(0);  // seeded to make results repeatable (deterministic)

    /** Create a large array of keys and matching values for use in any test */
    @BeforeAll
    public static void beforeClass() throws Exception{
        bookTable = BookParser.parse(BOOKS);
    }
    
    /** Initialize empty hash table to be used in each test */
    @BeforeEach
    public void setUp() throws Exception {
        // TODO: change HashTable for final solution
         bookObject = new BookHashTable(INIT_CAPACITY,LOAD_FACTOR_THRESHOLD);
    }

    /** Not much to do, just make sure that variables are reset     */
    @AfterEach
    public void tearDown() throws Exception {
        bookObject = null;
    }

    private void insertMany(ArrayList<Book> bookTable) 
        throws IllegalNullKeyException, DuplicateKeyException {
        for (int i=0; i < bookTable.size(); i++ ) {
            bookObject.insert(bookTable.get(i).getKey(), bookTable.get(i));
        }
    }

    /** IMPLEMENTED AS EXAMPLE FOR YOU
     * Tests that a HashTable is empty upon initialization
     */
    @Test
    public void test000_collision_scheme() {
        if (bookObject == null)
        	fail("Gg");
    	int scheme = bookObject.getCollisionResolutionScheme();
        if (scheme < 1 || scheme > 9) 
            fail("collision resolution must be indicated with 1-9");
    }


    /** IMPLEMENTED AS EXAMPLE FOR YOU
     * Tests that a HashTable is empty upon initialization
     */
    @Test
    public void test000_IsEmpty() {
        //"size with 0 entries:"
        assertEquals(0, bookObject.numKeys());
    }

    /** IMPLEMENTED AS EXAMPLE FOR YOU
     * Tests that a HashTable is not empty after adding one (key,book) pair
     * @throws DuplicateKeyException 
     * @throws IllegalNullKeyException 
     */
    @Test
    public void test001_IsNotEmpty() throws IllegalNullKeyException, DuplicateKeyException {
    	bookObject.insert(bookTable.get(0).getKey(),bookTable.get(0));
        String expected = ""+1;
        //"size with one entry:"
        assertEquals(expected, ""+bookObject.numKeys());
    }
    
    /** IMPLEMENTED AS EXAMPLE FOR YOU 
    * Test if the hash table  will be resized after adding two (key,book) pairs
    * given the load factor is 0.49 and initial capacity to be 2.
    */
    @Test 
    public void test002_Resize() throws IllegalNullKeyException, DuplicateKeyException {
    	bookObject.insert(bookTable.get(0).getKey(),bookTable.get(0));
    	int cap1 = bookObject.getCapacity(); 
    	bookObject.insert(bookTable.get(1).getKey(),bookTable.get(1));
    	int cap2 = bookObject.getCapacity();
    	
        //"size with one entry:"
        assertTrue(cap2 > cap1 & cap1 ==2);
    }

    /**
     * Tests to see if a KeyNotFoundException() is thrown when you try to get a key that is not within the HashTable
     *
     * @throws IllegalNullKeyException
     * @throws DuplicateKeyException
     */
    @Test
    public void test003_Key_Not_Found_Exception() throws IllegalNullKeyException, DuplicateKeyException {
        for(int i = 0; i < 100; i++)
            bookObject.insert(bookTable.get(i).getKey(), bookTable.get(i));
        try{
            bookObject.get("This is not an actual key");
            fail("KeyNotFoundException was not thrown");
        } catch (KeyNotFoundException e){}
    }

    /**
     * Tests to see if numKeys() returns correct values
     *
     * @throws IllegalNullKeyException
     * @throws DuplicateKeyException
     */
    @Test
    public void test004_num_keys() throws IllegalNullKeyException, DuplicateKeyException {
        for(int i = 0; i < 100; i++)
            bookObject.insert(bookTable.get(i).getKey(), bookTable.get(i));
        if(bookObject.numKeys() != 100)
            fail("Instead of numKeys() returning 100, it returned " + bookObject.numKeys());
        bookObject.remove(bookTable.get(73).getKey());
        if(bookObject.numKeys() != 99)
            fail("Instead of numKeys() returning 99, it returned " + bookObject.numKeys());
    }

    /**
     * Tests to see if DuplicateKeyException() is thrown when you insert two keys of the same value
     *
     * @throws IllegalNullKeyException
     * @throws DuplicateKeyException
     */
    @Test
    public void test005_Duplicate_Key_Exception() throws IllegalNullKeyException, DuplicateKeyException {
        bookObject.insert(bookTable.get(0).getKey(), bookTable.get(0));
        try {
            bookObject.insert(bookTable.get(0).getKey(), bookTable.get(0));
            fail("DuplicateKeyException was not thrown when it should have");
        } catch (DuplicateKeyException e) {}
    }

    /**
     * Checks to see if get() returns the correct vvalue
     *
     * @throws IllegalNullKeyException
     * @throws DuplicateKeyException
     * @throws KeyNotFoundException
     */
    @Test
    public void test006_get() throws IllegalNullKeyException, DuplicateKeyException, KeyNotFoundException {
        for(int i = 0; i < 100; i++)
            bookObject.insert(bookTable.get(i).getKey(), bookTable.get(i));
        if(!bookObject.get(bookTable.get(46).getKey()).equals(bookTable.get(46)))
            fail("Get did not return " + bookTable.get(46) + ", it returned " + bookObject.get(bookTable.get(46).getKey()));
    }

    /**
     * Tests to see if remove returns correct values and removes BookNode from the HashTable
     *
     * @throws IllegalNullKeyException
     * @throws DuplicateKeyException
     */
    @Test
    public void test007_remove() throws IllegalNullKeyException, DuplicateKeyException {
        for(int i = 0; i < 100; i++)
            bookObject.insert(bookTable.get(i).getKey(), bookTable.get(i));
        for(int i = 0; i < 100; i++) {
            if (!bookObject.remove(bookTable.get(i).getKey()))
                fail("Remove returned false when it shouldn't have");
        }
        try{
            bookObject.get(bookTable.get(25).getKey());
            fail("Remove did not remove the key from the HashTable");
        } catch (KeyNotFoundException e) {
        }
    }
}

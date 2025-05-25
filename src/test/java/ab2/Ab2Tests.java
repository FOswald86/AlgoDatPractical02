package ab2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ab2.impl.KassikLaferOswald.Ab2Impl;


public class Ab2Tests {

	
	
    @Test
    public void testInsertAndGetSimple() {
    	AbstractCustomProbingHashTable hashTable = new Ab2Impl(49);
        hashTable.insert(5, "Alice");
        assertEquals("Alice", hashTable.get(5));
    }

    @Test
    public void testOverwriteValue() {
    	AbstractCustomProbingHashTable hashTable = new Ab2Impl(49);
        hashTable.insert(5, "Alice");
        hashTable.insert(5, "Bob");
        assertEquals("Bob", hashTable.get(5));
    }

    @Test
    public void testInsertCollisionHandling() {
    	AbstractCustomProbingHashTable hashTable = new Ab2Impl(49);
        int p = 7;
        int n = p * p; // 49
        int key1 = 10;
        int key2 = key1 + n; // Force same hash index (since 59 % 49 = 10)

        hashTable.insert(key1, "First");
        hashTable.insert(key2, "Second");

        assertEquals("First", hashTable.get(key1));
        assertEquals("Second", hashTable.get(key2));
    }

    @Test
    public void testRemoveThenInsertSameKey() {
    	AbstractCustomProbingHashTable hashTable = new Ab2Impl(49);
        hashTable.insert(22, "Temp");
        hashTable.remove(22);
        assertNull(hashTable.get(22));

        hashTable.insert(22, "NewVal");
        assertEquals("NewVal", hashTable.get(22));
    }

    @Test
    public void testInsertDeletedIsRejected() {
    	AbstractCustomProbingHashTable hashTable = new Ab2Impl(49);
        assertThrows(IllegalArgumentException.class, () -> {
            hashTable.insert(3, "deleted");
        });
    }

    @Test
    public void testIsEmptyAndIsFull() {
    	AbstractCustomProbingHashTable hashTable = new Ab2Impl(49);
        assertTrue(hashTable.isEmpty());

        for (int i = 0; i < hashTable.getMaxSize(); i++) {
            hashTable.insert(i, "Val" + i);
        }

        assertFalse(hashTable.isEmpty());
    }

    @Test
    public void testDeletedSizeAndCurrentSizeTracking() {
    	AbstractCustomProbingHashTable hashTable = new Ab2Impl(49);
        hashTable.insert(1, "One");
        hashTable.insert(2, "Two");
        hashTable.remove(1);

        assertEquals(1, hashTable.getCurrentSize());
        assertEquals(1, hashTable.getDeletedSize());
    }
    
    @Test
    public void testDeletedSlotsAndReuse() {
    	AbstractCustomProbingHashTable hashTable = new Ab2Impl(9);
        // Insert 3 elements
        hashTable.insert(1, "A");
        hashTable.insert(2, "B");
        hashTable.insert(3, "C");

        assertEquals("A", hashTable.get(1));
        assertEquals("B", hashTable.get(2));
        assertEquals("C", hashTable.get(3));
        assertEquals(3, hashTable.getCurrentSize());
        assertEquals(0, hashTable.getDeletedSize());

        // Remove 2
        hashTable.remove(1);
        hashTable.remove(3);

        assertNull(hashTable.get(1));
        assertNull(hashTable.get(3));
        assertEquals(1, hashTable.getCurrentSize());
        assertEquals(2, hashTable.getDeletedSize());

        // Insert new values into the deleted slots
        hashTable.insert(4, "D");
        hashTable.insert(5, "E");

        assertEquals("D", hashTable.get(4));
        assertEquals("E", hashTable.get(5));
        assertEquals(3, hashTable.getCurrentSize()); // Should reuse deleted slots
        assertEquals(2, hashTable.getDeletedSize());
    }

    @Test
    public void testFullTableCondition() {
    	AbstractCustomProbingHashTable hashTable = new Ab2Impl(9);
        for (int i = 0; i < 9; i++) {
            hashTable.insert(i, "Val" + i);
        }
        assertFalse(hashTable.isEmpty());
        assertEquals(9, hashTable.getCurrentSize());

        // Remove one element and check that table is no longer considered full (9)
        hashTable.remove(4);
        assertEquals(8, hashTable.getCurrentSize());
        assertEquals(1, hashTable.getDeletedSize());
        System.out.println("CurrentSize before: " + hashTable.getCurrentSize());
        System.out.println("DeletedSize before: " + hashTable.getDeletedSize());

        // Insert another element, filling the deleted slot 
        hashTable.insert(4, "X");
        System.out.println("CurrentSize after: " + hashTable.getCurrentSize());
        System.out.println("DeletedSize after: " + hashTable.getDeletedSize());
        System.out.println("Wert für key 4: " + hashTable.get(4));

        assertEquals(9, hashTable.getCurrentSize());
        assertEquals(0, hashTable.getDeletedSize());
        // System.out.println("currentSize = " + hashTable.getCurrentSize());
        // 7System.out.println("deletedSize = " + hashTable.getDeletedSize());


        // Insert another element, filling the deleted slot 
        assertThrows(IllegalStateException.class, () -> hashTable.insert(10, "X"));
        
    }

    @Test
    public void testInsertRemoveInsertSameKey() {
    	AbstractCustomProbingHashTable hashTable = new Ab2Impl(9);
        hashTable.insert(7, "First");
        hashTable.remove(7);
        assertNull(hashTable.get(7));
        hashTable.insert(7, "Second");
        assertEquals("Second", hashTable.get(7));
        assertEquals(1, hashTable.getCurrentSize());
        assertEquals(0, hashTable.getDeletedSize());
    }
    
	@Test
	public void testStairSearchMatrixA() {
		Ab2Impl ab2Impl = new Ab2Impl(13*13);
	    // Matrix A
	    int[][] matrixA = {
	        {1, 4, 7, 11},
	        {2, 5, 8, 12},
	        {3, 6, 9, 16},
	        {10, 13, 14, 17}
	     };
	    
	    //stair search found
	    int targetA = 10;
	    int[] resultA = ab2Impl.stairSearch(matrixA, targetA);
	    int[] expectedA = {3, 0};
        assertEquals(expectedA[0], resultA[0]);
        assertEquals(expectedA[1], resultA[1]);
      //stair search not found
        
        int targetA2 = 15;
	    int[] resultA2 = ab2Impl.stairSearch(matrixA, targetA2);
	    int[] expectedA2 = {-1, -1};
        assertEquals(expectedA2[0], resultA2[0]);
        assertEquals(expectedA2[1], resultA2[1]);
        
        int[][] matrixA3 = {
    	        {1, 4, 7, 11},
    	        {2, 5, 8, 22},
    	        {3, 6, 9, 26},
    	        {10, 13, 14, 27}
    	     };
        
        int targetA3 = 11;
	    int[] resultA3 = ab2Impl.stairSearch(matrixA3, targetA3);
	    int[] expectedA3 = {0,3};
        assertEquals(expectedA3[0], resultA3[0]);
        assertEquals(expectedA3[1], resultA3[1]);
        
        int targetA3a = 18;
	    int[] resultA3a = ab2Impl.stairSearch(matrixA3, targetA3a);
	    int[] expectedA3a = {-1,-1};
        assertEquals(expectedA3a[0], resultA3a[0]);
        assertEquals(expectedA3a[1], resultA3a[1]);
	}

	@Test
	public void testBinaryMatrixB() {
		//binary search found
		Ab2Impl ab2Impl = new Ab2Impl(13*13);
		
		int[][] matrixB = {
	            {1, 2, 3, 4},
	            {5, 6, 7, 8},
	            {9, 10, 11, 12}
	        };
	    int target = 7;
	    int[] result = ab2Impl.binarySearch2D(matrixB, target);
	    int[] expected = {1, 2};
	    assertEquals(expected[0], result[0]);
	    assertEquals(expected[1], result[1]);

        //Binary search not found
	        
	    int target2 = 13;
	    int[] result2 = ab2Impl.binarySearch2D(matrixB, target2);
	    int[] expected2 = {-1, -1};
	    assertEquals(expected2[0], result2[0]);
	    
	  //reference vs student
	    int[][] matrixB3 = {
	            {1, 2, 3, 4},
	            {5, 6, 7, 8},
	            {9, 10, 11, 12},
	            {15, 19, 20, 22}
	        };
        
        int targetB3 = 21;
	    int[] resultB3 = ab2Impl.stairSearch(matrixB3, targetB3);
	    int[] expectedB3 = {-1,-1};
        assertEquals(expectedB3[0], resultB3[0]);
        assertEquals(expectedB3[1], resultB3[1]);
        
        int targetB3a = 1;
	    int[] resultB3a = ab2Impl.stairSearch(matrixB3, targetB3a);
	    int[] expectedB3a = {0,0};
        assertEquals(expectedB3a[0], resultB3a[0]);
        assertEquals(expectedB3a[1], resultB3a[1]);
	}
}

package ab2;

/**
 * 
 * The abstract class AbstractCustomProbingHashTable class provides a basic
 * implementation of a hash table with a custom probing function (dt.:
 * Sondierungsverfahren). The class contains methods for common hash table
 * operations such as inserting, retrieving, and removing key-value pairs. This
 * class is abstract, meaning it cannot be instantiated directly and is intended
 * to be subclassed by other classes. See package ab2.impl.Nachnamen @Override
 * and implement the methods at the auto-generated method stubs (where TODO is
 * written)
 * 
 * @author J. Wachter
 */
public abstract class AbstractCustomProbingHashTable {

	/**
	 * The maximum size of the data structure. Equals p*p
	 */
	private int maxSize;

	/**
	 * The current size of the data structure. 
	 * Do not count deleted values!
	 */
	private int currentSize;
	
	/**
	 * Counts the number of deleted keys. 
	 */
	private int deletedSize;
	/**
	 * An array of keys.
	 */
	protected Integer[] keys;

	/**
	 * An array of values corresponding to the keys.
	 */
	protected String[] vals;

	/**
	 * Method to get key array (key present in the current hash table).
	 * 
	 * @return keys; 
	 */
	public Integer[] getKeys() {
		return keys;
	}
	
	/**
	 * Method to get key array (key present in the current hash table).
	 * 
	 * @return keys; 
	 */
	public String[] getVals() {
		return vals;
	}

	/**
	 * 
	 * @param initial capacity of the has table - must be prime number squared by
	 * definition to ensure optimal performance. The constructor constructs a new
	 * AbstractCustomProbingHashTable object with the specified capacity. 
	 * It initializes the private key and values (vals) arrays of adequate size.
	 */
	public AbstractCustomProbingHashTable(int capacity) {
		this.currentSize = 0;
		this.deletedSize = 0;
		this.maxSize = capacity;
		this.keys = new Integer[maxSize];
		this.vals = new String[maxSize];
	}

	/**
	 * 
	 * This method empties the hash table. It re-initializes the key and value
	 * arrays and sets the size of the hash table (number of elements inserted -
	 * currentSize) to zero.
	 */
	public void makeEmpty() {
		this.currentSize = 0;
		this.deletedSize = 0;
		this.keys = new Integer[maxSize];
		this.vals = new String[maxSize];
	}

	
	/**
	 * Method to get size of the hash table (number of elements present in the current hash table).
	 * Deleted elements in the hash table are NOT included.
	 * 
	 * @return this.currentSize
	 */
	public int getCurrentSize() {
		return this.currentSize;
	}
	
	/**
	 * Method to get number of deleted elements in the hash table .
	 * 
	 * @return this.deletedSize
	 */
	public int getDeletedSize() {
		return this.deletedSize;
	}
	/**
	 * Method to set the size of the hash table.
	 * If the {@link newSize} is not valid the method throws an exception.
	 * 
	 * @param newSize
	 * @return this.currentSize
	 * @throws IllegalStateException if size is negative or exceeds maximum size
	 */
	public int setCurrentSize(int size) {
	    System.out.println("Breakpoint: Setting size to " + size);
	    if (size < 0) {
	        //System.out.println("Breakpoint: Size cannot be negative");
	        throw new IllegalStateException("Breakpoint: Size cannot be negative");
	    }
	    if (size + this.getDeletedSize() > this.getMaxSize()) {
	        //System.out.println("Breakpoint: Size exceeds maximum size");
	        throw new IllegalStateException("Breakpoint: Size exceeds maximum size");
	    }
	    this.currentSize = size;
	    //System.out.println("Breakpoint: Size set successfully. New size: " + this.currentSize);
	    return this.currentSize;
	}
	
	public int setDeletedSize(int size) {
	    System.out.println("Breakpoint: Setting size to " + size);
	    if (size < 0) {
	        //System.out.println("Breakpoint: Size cannot be negative");
	        throw new IllegalStateException("Breakpoint: Size cannot be negative");
	    }
	    if (size + this.getCurrentSize() > this.getMaxSize()) {
	        //System.out.println("Breakpoint: Size exceeds maximum size");
	        throw new IllegalStateException("Breakpoint: Size exceeds maximum size");
	    }
	    this.deletedSize = size;
	    //System.out.println("Breakpoint: Size set successfully. New size: " + this.currentSize);
	    return this.deletedSize;
	}

	/**
	 * Method to check if the given hash table object is empty
	 * 
	 * @return true if hash table is empty. Else is false
	 */
	public boolean isEmpty() {
		return this.getCurrentSize()+this.getDeletedSize() == 0;
	}

	/**
	 * Method to check if the given hash table contains a given key (an integer
	 * value).
	 * 
	 * @return true if hash table contains the key. Else it returns false
	 */
	public boolean contains(int key) {
		return this.get(key) != null;
	}

	/**
	 * Method to compute the hash of a key modulo the size of the hash table n. This
	 * method maps the key to valid positions in the hash table: h(key) = key \mod n
	 * 
	 * @param key
	 * @return h(key) = key \mod n
	 */
	protected int hash(int key) {
		return key % this.getMaxSize();

	}
	/**
	 * Method to decrement (-1) the number of deleted elements of the hash table (e.g. 
	 * overwriting a removed key-value pair). 
	 */

	public void decrementDeletedSize() {
	    try {
	        int result = this.setDeletedSize(this.getDeletedSize() - 1);
	        System.out.println("Breakpoint: Size decremented successfully. New size: " + result);
	    } catch (IllegalStateException e) {
	        System.out.println("Breakpoint: Error decrementing size. " + e.getMessage());
	        return;
	    }
	}

	/**
	 * Method to increment (+1) the number of deleted elements of the hash table (e.g. 
	 * after removing a key-value pair). 
	 */
	public void incrementDeletedSize() {
	    try {
	        int result = this.setDeletedSize(this.getDeletedSize() + 1);
	        System.out.println("Breakpoint: Size incremented successfully. New size: " + result);
	    } catch (IllegalStateException e) {
	        System.out.println("Breakpoint: Error incrementing size. " + e.getMessage());
	    }
	}
	/**
	 * Method to decrement (-1) the size of the hash table (e.g. after removing a
	 * key-value pair). 
	 */
	public void decrementCurrentSize() {
	    try {
	        int result = this.setCurrentSize(this.getCurrentSize() - 1);
	        System.out.println("Breakpoint: Size decremented successfully. New size: " + result);
	    } catch (IllegalStateException e) {
	        System.out.println("Breakpoint: Error decrementing size. " + e.getMessage());
	        return;
	    }
	}
	
	/**
	 * Method to increment (+1) the size of the hash table (e.g. after adding a
	 * key-value pair). 
	 */
	protected void incrementCurrentSize() {
		// Prüft, ob über die maximale Anzahl aktiver Elemente hinausgegangen wird
		if (this.currentSize >= this.maxSize) {
			throw new IllegalStateException("Current size already at maximum capacity.");
		}
		this.currentSize++;
	}

	

	/**
	 * Method to get the maximum size of the hash table (=capacity)
	 * 
	 * @return maxSize
	 */
	public int getMaxSize() {
		return maxSize;
	}

	/**
	 * Method to insert a (key, value)-pair in the hash table at a specific key This
	 * method is abstract and must be implemented by subclasses.
	 * 
	 * @param key
	 * @param val
	 */
	public abstract void insert(int key, String val);

	/**
	 * Retrieves the value associated with the specified key from the hash table.
	 * If the value to be inserted equals "deleted" throw an IllegalArgumentException("Cannot insert a value of 'deleted' as it is a reserved key")
	 * This method is abstract and must be implemented by subclasses.
	 * 
	 * @param key
	 * @return value of (key, value)- pair in the hash table for the given key. null
	 *         if non-existent
	 * @throws IllegalArgumentException if the value to be inserted equals "deleted", as it is a reserved key
	 */
	public abstract String get(int key);

	/**
	 * Searches the key-value pair associated with the specified key from the hash
	 * table and removes the key-value pair by setting the value to "deleted". This method is abstract and must be
	 * implemented by subclasses.
	 * Insert and get methods must handle the "deleted" values.
	 * 
	 * @param key
	 */
	public abstract void remove(int key);

}

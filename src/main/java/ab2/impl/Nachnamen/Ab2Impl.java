package ab2.impl.Nachnamen;

import ab2.AbstractCustomProbingHashTable;
import ab2.IMatrixSearch;

public class Ab2Impl extends AbstractCustomProbingHashTable implements IMatrixSearch {

	public Ab2Impl(int capacity) {
		super(capacity);
	}
	
	//PART 1
	//TEIL 1
	// Implement methods from AbstractCustomProbingHashTable
	// Hint: use a helper function to compute probing sequence
	
	@Override
	public void insert(int key, String val) {
		// TODO Auto-generated method stub
	}

	@Override
	public String get(int key) {
		// TODO Auto-generated method stub
	    return null; // 
	}

	@Override
	public void remove(int key) {
		// TODO Auto-generated method stub
	}

	//PART 2
	//TEIL 2
	// Implement methods from IMatrixSearch
	
	@Override
	public int[] binarySearch2D(int[][] matrix, int target) {
		// TODO Auto-generated method stub
        return null; 
    }

	@Override
    public int[] stairSearch(int[][] matrix, int target) {
		// TODO Auto-generated method stub
        return null;
    }


}
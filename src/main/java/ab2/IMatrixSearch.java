package ab2;

/**
 * Interface for searching in 2D matrices.
 * 
 * This interface provides two methods for searching in different types of matrices:
 * <ul>
 * <li>Matrix A: rows and columns are both sorted</li>
 * <li>Matrix B: the matrix is like an array turned into a matrix by filling its values in rows sequentially from left to right (flat sorted)</li>
 * </ul>
 * 
 * @author J WACHTER
 */

public interface IMatrixSearch {
    // Matrix interface 
	//Offers two matrix types
	
	/*
	 *  In Matrix A rows and columns both sorted e.g.
     *[ [ 1,  4,  7, 11],
     *[ 2,  5,  8, 12],
     *[ 3,  6,  9, 16],
   	 *[10, 13, 14, 17] ]
	 */
    
    /*
	 *The matrix is like an array turned into a matrix by filling its values in rows sequentially from left to right (flat sorted)
     *[ [ 1,  2,  3,  4],
     *[ 5,  6,  7,  8],
     *[ 9, 10, 11, 12] ]
     */

    // Implement binarySearch and stairSearch here
    // stair search for MatrixA
    // binary search for MatrixB

	/**
	 * 
	 * @param matrix - an 2d-integer array int[][] containing a matrix of type A
	 * @param target value
	 * @return an integer array int[] {row, col} of the indices 
	 * of the target value in the matrix;
	 * return -1,-1 if the value was not found
	 */
	public int[] stairSearch(int[][] matrix, int target);
	
	/**
	 * 
	 * @param matrix - an 2d-integer array int[][] containing a matrix of type B
	 * @param target value
	 * @return an integer array int[] consisting of {row, col} of the indices 
	 * of the target value in the input matrix; 
	 * return -1,-1 if the value was not found
	 */
	public int[] binarySearch2D(int[][] matrix, int target);

}


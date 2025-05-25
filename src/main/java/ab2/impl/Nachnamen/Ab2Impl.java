package ab2.impl.Nachnamen;

import ab2.AbstractCustomProbingHashTable;
import ab2.IMatrixSearch;

/**
 * Implementierung von Hashing mit benutzerdefinierter Sondierungsstrategie
 * sowie Suchalgorithmen auf 2D-Matrizen.
 *
 * @author FOswald86, CLafer2001, Florian14189
 */
public class Ab2Impl extends AbstractCustomProbingHashTable implements IMatrixSearch {

	public Ab2Impl(int capacity) {
		super(capacity);
		int p = (int) Math.sqrt(capacity);
		if (p * p != capacity) {
			throw new IllegalArgumentException("Capacity must be a perfect square (p*p).");
		}
	}

	// In Ab2Impl.java
	@Override
	public void insert(int key, String val) {
		if ("deleted".equals(val)) {
			throw new IllegalArgumentException("Cannot insert a value of 'deleted' as it is a reserved value for marking deleted slots.");
		}

		// Die Prüfung am Anfang muss angepasst werden, um die "deleted" Slots zu berücksichtigen
		// Wenn die aktuelle Größe + die Anzahl der gelöschten Slots bereits die Maximalkapazität erreicht haben
		// UND wir keinen gelöschten Slot wiederverwenden können, dann ist die Tabelle wirklich voll für einen neuen Eintrag.
		// Aber wenn wir einen deleted slot wiederverwenden, bleibt die Summe gleich!
		// Daher ist diese Initialprüfung schwierig, wenn die Basisklasse übermäßig restriktiv ist.

		int p = (int) Math.sqrt(getMaxSize());
		int n = getMaxSize();
		int initialHash = hash(key);

		int firstDeletedIdx = -1;

		for (int i = 0; i < n; i++) {
			int idx = probe(initialHash, i, p, n);

			// Fall 1: Schlüssel bereits vorhanden
			if (keys[idx] != null && keys[idx].equals(key)) {
				if ("deleted".equals(vals[idx])) {
					// Schlüssel existierte, war aber gelöscht. Wiederbeleben.
					vals[idx] = val;
					// Hier ist der kritische Punkt:
					// Du erhöhst currentSize, dekrementierst deletedSize. Die Summe bleibt gleich.
					// Wenn die Basisklasse dies als "Tabelle voll" interpretiert, ist das Problem dort.
					try {
						incrementCurrentSize();
						decrementDeletedSize();
					} catch (IllegalStateException e) {
						// Dies sollte nicht passieren, wenn die Basisklasse korrekt ist.
						// Wenn es doch passiert, bedeutet es, dass die Basisklasse die Logik nicht richtig unterstützt.
						// Möglicherweise müssen wir den Wert zurücksetzen, um einen inkonsistenten Zustand zu vermeiden.
						vals[idx] = "deleted"; // Rollback
						throw new IllegalStateException("Failed to re-activate deleted entry due to base class restrictions.", e);
					}
					return;
				} else {
					// Schlüssel existiert und ist aktiv – Wert überschreiben
					vals[idx] = val;
					return;
				}
			}

			// Fall 2: Leerer Slot (null) gefunden
			if (keys[idx] == null) {
				if (firstDeletedIdx != -1) {
					// Wir haben einen "deleted" Slot früher gefunden, verwenden wir diesen, um die Lücke zu füllen.
					keys[firstDeletedIdx] = key;
					vals[firstDeletedIdx] = val;
					try {
						incrementCurrentSize();
						decrementDeletedSize();
					} catch (IllegalStateException e) {
						keys[firstDeletedIdx] = null; // Rollback
						vals[firstDeletedIdx] = "deleted"; // Muss wieder als deleted markiert werden
						throw new IllegalStateException("Failed to use deleted slot for new entry due to base class restrictions.", e);
					}
					return;
				} else {
					// Direkteinfügung in einen wirklich leeren Slot
					if (getCurrentSize() + getDeletedSize() >= getMaxSize()) {
						// Dies sollte nur dann auftreten, wenn der einzige freie Platz ein "deleted" Slot wäre,
						// aber keiner gefunden wurde, oder die Tabelle logisch voll ist.
						// Wenn du hier bist, und kein null-Slot gefunden wird, und kein firstDeletedIdx,
						// dann ist die Tabelle wirklich voll.
						throw new IllegalStateException("Hash table is full and no deleted slot found for reuse.");
					}
					keys[idx] = key;
					vals[idx] = val;
					incrementCurrentSize(); // Diese Inkrementierung sollte nur fehlschlagen, wenn currentSize bereits maxSize ist.
					return;
				}
			}

			// Fall 3: Slot ist belegt, aber der Wert ist "deleted"
			if ("deleted".equals(vals[idx])) {
				if (firstDeletedIdx == -1) {
					firstDeletedIdx = idx; // Merken wir uns den ersten "deleted" Slot
				}
			}
		}

		// Wenn der Loop beendet ist und wir einen "deleted" Slot gefunden haben (für einen NEUEN Schlüssel)
		if (firstDeletedIdx != -1) {
			keys[firstDeletedIdx] = key;
			vals[firstDeletedIdx] = val;
			try {
				incrementCurrentSize();
				decrementDeletedSize();
			} catch (IllegalStateException e) {
				keys[firstDeletedIdx] = null; // Rollback
				vals[firstDeletedIdx] = "deleted"; // Muss wieder als deleted markiert werden
				throw new IllegalStateException("Failed to use final deleted slot for new entry due to base class restrictions.", e);
			}
			return;
		}

		// Wenn wir hier ankommen, ist die Tabelle wirklich voll
		throw new IllegalStateException("Hash table is full");
	}


	@Override
	public String get(int key) {
		int p = (int) Math.sqrt(getMaxSize());
		int n = getMaxSize();
		int initialHash = hash(key);

		for (int i = 0; i < n; i++) {
			int idx = probe(initialHash, i, p, n);

			if (keys[idx] == null) {
				return null;
			}

			if (keys[idx].equals(key) && !"deleted".equals(vals[idx])) {
				return vals[idx];
			}
		}
		return null;
	}

	@Override
	public void remove(int key) {
		int p = (int) Math.sqrt(getMaxSize());
		int n = getMaxSize();
		int initialHash = hash(key);

		for (int i = 0; i < n; i++) {
			int idx = probe(initialHash, i, p, n);

			if (keys[idx] == null) {
				return;
			}

			if (keys[idx].equals(key) && !"deleted".equals(vals[idx])) {
				vals[idx] = "deleted";
				// Bei remove wird currentSize dekrementiert und deletedSize inkrementiert.
				// Die Summe currentSize + deletedSize bleibt dabei konstant,
				// sodass keine IllegalStateException von der Basisklasse erwartet wird.
				decrementCurrentSize();
				incrementDeletedSize();
				return;
			}
		}
	}

	private int probe(int initial, int step, int p, int n) {
		return (initial + step) % n;
	}

	@Override
	public int[] stairSearch(int[][] matrix, int target) {
		if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
			return new int[]{-1, -1};
		}
		int rows = matrix.length;
		int cols = matrix[0].length;

		int row = 0, col = cols - 1;

		while (row < rows && col >= 0) {
			if (matrix[row][col] == target) {
				return new int[]{row, col};
			} else if (matrix[row][col] < target) {
				row++;
			} else {
				col--;
			}
		}
		return new int[]{-1, -1};
	}

	@Override
	public int[] binarySearch2D(int[][] matrix, int target) {
		if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
			return new int[]{-1, -1};
		}
		int rows = matrix.length;
		int cols = matrix[0].length;
		int totalElements = rows * cols;

		int low = 0, high = totalElements - 1;

		while (low <= high) {
			int mid = low + (high - low) / 2;
			int r = mid / cols;
			int c = mid % cols;

			if (r < 0 || r >= rows || c < 0 || c >= cols) {
				return new int[]{-1, -1};
			}

			int val = matrix[r][c];

			if (val == target) {
				return new int[]{r, c};
			} else if (val < target) {
				low = mid + 1;
			} else {
				high = mid - 1;
			}
		}

		return new int[]{-1, -1};
	}
}
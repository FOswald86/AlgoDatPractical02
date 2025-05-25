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

		int p = (int) Math.sqrt(getMaxSize());
		int n = getMaxSize();
		int initialHash = hash(key);

		int firstDeletedIdx = -1; // Speichert den Index des ersten gefundenen "deleted" Slots

		for (int i = 0; i < n; i++) {
			int idx = probe(initialHash, i, p, n);

			// Fall 1: Slot ist leer (null)
			if (keys[idx] == null) {
				// Wenn wir einen zuvor gelöschten Slot gefunden haben, nutzen wir diesen bevorzugt
				if (firstDeletedIdx != -1) {
					keys[firstDeletedIdx] = key;
					vals[firstDeletedIdx] = val;
					try {
						incrementCurrentSize();    // Erhöhe Anzahl der aktiven Elemente
						decrementDeletedSize();    // Verringere Anzahl der gelöschten Markierungen
					} catch (IllegalStateException e) {
						// Rollback: Wenn die Basisklasse einen Fehler wirft, müssen wir den Zustand wiederherstellen
						keys[firstDeletedIdx] = null; // Den Slot wieder als leer markieren
						// Optional: Den alten "deleted" Status wiederherstellen, falls er vorher gesetzt war.
						// Da wir einen neuen Schlüssel einfügen wollten, war der Slot zuvor deleted.
						vals[firstDeletedIdx] = "deleted"; // Oder null, je nach genauer Semantik des Rollbacks
						throw new IllegalStateException("Failed to insert into deleted slot due to base class restrictions.", e);
					}
					return;
				} else {
					// Kein gelöschter Slot gefunden, direkter Insert in leeren Slot
					keys[idx] = key;
					vals[idx] = val;
					incrementCurrentSize();    // Erhöhe Anzahl der aktiven Elemente
					return;
				}
			}

			// Fall 2: Schlüssel bereits vorhanden
			if (keys[idx].equals(key)) {
				// Wenn der vorhandene Eintrag als "deleted" markiert war, reaktivieren wir ihn
				if ("deleted".equals(vals[idx])) {
					String oldVal = vals[idx]; // Sicherung des alten Werts für Rollback
					vals[idx] = val; // Überschreibe "deleted" mit neuem Wert
					try {
						incrementCurrentSize();    // Erhöhe Anzahl der aktiven Elemente
						decrementDeletedSize();    // Verringere Anzahl der gelöschten Markierungen
					} catch (IllegalStateException e) {
						// Rollback: Wenn die Basisklasse einen Fehler wirft, müssen wir den Zustand wiederherstellen
						vals[idx] = oldVal; // Wert zurücksetzen auf "deleted"
						throw new IllegalStateException("Failed to re-activate deleted entry due to base class restrictions.", e);
					}
					return;
				} else {
					// Schlüssel existiert und ist aktiv – Wert einfach aktualisieren
					vals[idx] = val;
					return;
				}
			}

			// Fall 3: Slot ist belegt, aber der Wert ist "deleted"
			// Merken wir uns diesen Slot, falls wir später einen neuen Schlüssel einfügen müssen
			if ("deleted".equals(vals[idx]) && firstDeletedIdx == -1) {
				firstDeletedIdx = idx;
			}
		}

		// Wenn die Schleife durchlaufen wurde und wir hier sind:
		// Wenn wir einen "deleted" Slot gefunden haben, können wir diesen nutzen,
		// um einen neuen Schlüssel einzufügen, wenn die Tabelle sonst voll wäre.
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

		// Wenn wir hier ankommen, ist die Tabelle wirklich voll.
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
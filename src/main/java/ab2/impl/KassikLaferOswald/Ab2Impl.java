package ab2.impl.KassikLaferOswald;

import ab2.AbstractCustomProbingHashTable;
import ab2.IMatrixSearch;

/**
 * Implementierung einer Hash-Tabelle mit modularer Multiplikationssondierung.
 * Außerdem werden zwei Suchalgorithmen für spezielle 2D-Matrizen implementiert.
 * Die Hash-Tabelle unterstützt Einfügen, Suchen und Entfernen von Schlüsseln mit einer offenen Adressierung.
 */
public class Ab2Impl extends AbstractCustomProbingHashTable implements IMatrixSearch {

	/**
	 * Konstruktor: Initialisiert die Hash-Tabelle mit gegebener Kapazität.
	 * @param capacity Die Größe der Hash-Tabelle. Muss eine Quadratzahl (p * p) sein.
	 */
	public Ab2Impl(int capacity) {
		super(capacity);
		int p = (int) Math.sqrt(capacity); // berechne Wurzel der Kapazität, soll eine Primzahl p sein
		if (p * p != capacity) {
			throw new IllegalArgumentException("Capacity must be a perfect square (p*p).");
		}
	}

	/**
	 * Fügt ein neues Schlüssel-Wert-Paar in die Hash-Tabelle ein oder überschreibt es.
	 * Kollisionen werden durch multiplikative Sondierung gelöst.
	 * @param key Der Schlüssel (int), der eingefügt werden soll.
	 * @param val Der zugehörige String-Wert. "deleted" ist als Eintrag nicht erlaubt.
	 */
	@Override
	public void insert(int key, String val) {
		if ("deleted".equals(val)) {
			throw new IllegalArgumentException("Cannot insert a value of 'deleted' as it is a reserved marker.");
		}

		int p = (int) Math.sqrt(getMaxSize()); // berechne Primzahl p aus maxSize = p*p
		int tableSize = getMaxSize(); // speichere Tabellenkapazität
		int hashIndex = hash(key); // berechne Startindex mit Hashfunktion
		int firstDeletedSlot = -1; // Index eines zuvor gelöschten Slots (für spätere Wiederverwendung)

		// versuche, einen freien oder passenden Slot zu finden
		for (int i = 0; i < tableSize; i++) {
			int index = probe(hashIndex, i, p, tableSize); // berechne nächsten Slot

			// leerer Slot gefunden
			if (keys[index] == null) {
				if (firstDeletedSlot != -1) {
					// benutze vorher gemerkten gelöschten Slot
					keys[firstDeletedSlot] = key;
					vals[firstDeletedSlot] = val;
					decrementDeletedSize();
					incrementCurrentSize();
				} else {
					// füge in aktuellen leeren Slot ein
					keys[index] = key;
					vals[index] = val;
					incrementCurrentSize();
				}
				return;
			}

			// Schlüssel existiert bereits
			if (keys[index].equals(key)) {
				if ("deleted".equals(vals[index])) {
					// reaktiviere gelöschten Slot
					vals[index] = val;
					decrementDeletedSize();
					incrementCurrentSize();
				} else {
					// überschreibe aktuellen Wert
					vals[index] = val;
				}
				return;
			}

			// merke ersten gelöschten Slot zur späteren Wiederverwendung
			if ("deleted".equals(vals[index]) && firstDeletedSlot == -1) {
				firstDeletedSlot = index;
			}
		}

		// kein freier Platz, aber gelöschter Slot vorhanden
		if (firstDeletedSlot != -1) {
			keys[firstDeletedSlot] = key;
			vals[firstDeletedSlot] = val;
			decrementDeletedSize();
			incrementCurrentSize();
			return;
		}

		// Tabelle ist voll, keine Einfügung möglich
		throw new IllegalStateException("Hash table is full");
	}

	/**
	 * Sucht nach dem Wert eines bestimmten Schlüssels in der Hash-Tabelle.
	 * @param key Der zu suchende Schlüssel.
	 * @return Der gespeicherte Wert oder null, wenn nicht vorhanden oder gelöscht.
	 */
	@Override
	public String get(int key) {
		int p = (int) Math.sqrt(getMaxSize()); // berechne Primzahl p
		int n = getMaxSize(); // Tabellenkapazität
		int hashIndex = hash(key); // Startindex durch Hashfunktion

		// iteriere durch mögliche Slots gemäß Sondierungsstrategie
		for (int i = 0; i < n; i++) {
			int index = probe(hashIndex, i, p, n); // nächsten Index berechnen

			if (keys[index] == null) return null; // Schlüssel existiert nicht

			if (keys[index].equals(key) && !"deleted".equals(vals[index])) {
				return vals[index]; // Schlüssel gefunden und gültig
			}
		}
		return null; // nicht gefunden
	}

	/**
	 * Entfernt ein Schlüssel-Wert-Paar aus der Hash-Tabelle (logisch durch Markierung).
	 * @param key Der zu entfernende Schlüssel.
	 */
	@Override
	public void remove(int key) {
		int p = (int) Math.sqrt(getMaxSize()); // berechne Primzahl p
		int n = getMaxSize(); // Tabellenkapazität
		int hashIndex = hash(key); // berechne Startindex mit Hashfunktion

		for (int i = 0; i < n; i++) {
			int index = probe(hashIndex, i, p, n); // berechne nächsten zu prüfenden Index

			if (keys[index] == null) return; // nicht vorhanden

			if (keys[index].equals(key) && !"deleted".equals(vals[index])) {
				vals[index] = "deleted"; // markiere als gelöscht
				decrementCurrentSize();
				incrementDeletedSize();
				return;
			}
		}
	}

	/**
	 * Sondiert mit modularer Multiplikation: index_i = (initial * p^i) % n
	 * @param initial Anfangsindex
	 * @param step i-ter Sondierungsschritt
	 * @param p Primzahl
	 * @param n Tabellenlänge
	 * @return Berechneter Index
	 */
	private int probe(int initial, int step, int p, int n) {
		long result = initial; // benutze long, um Überläufe zu vermeiden
		for (int i = 0; i < step; i++) {
			result = (result * p) % n; // multiplikative Sondierung
		}
		return (int) result;
	}

	/**
	 * Suche nach einem Wert in einer Matrix, bei der Zeilen und Spalten sortiert sind.
	 * @param matrix Die 2D-Matrix
	 * @param target Der gesuchte Wert
	 * @return int[] mit [Zeile, Spalte] oder {-1, -1} wenn nicht gefunden
	 */
	@Override
	public int[] stairSearch(int[][] matrix, int target) {
		if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
			return new int[]{-1, -1}; // ungültige Eingabe
		}

		int rows = matrix.length; // Anzahl der Zeilen
		int cols = matrix[0].length; // Anzahl der Spalten
		int row = 0; // Start in oberster Zeile
		int col = cols - 1; // Start in rechter Spalte

		while (row < rows && col >= 0) {
			int current = matrix[row][col];

			if (current == target) {
				return new int[]{row, col}; // Wert gefunden
			} else if (current < target) {
				row++; // gehe eine Zeile nach unten
			} else {
				col--; // gehe eine Spalte nach links
			}
		}

		return new int[]{-1, -1}; // nicht gefunden
	}

	/**
	 * Binäre Suche in einer Matrix, die in 1D-Darstellung sortiert ist (flat sorted).
	 * @param matrix Die Matrix
	 * @param target Gesuchter Wert
	 * @return int[] mit [Zeile, Spalte] oder {-1, -1} wenn nicht vorhanden
	 */
	@Override
	public int[] binarySearch2D(int[][] matrix, int target) {
		if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
			return new int[]{-1, -1}; // ungültige Eingabe
		}

		int rows = matrix.length; // Zeilenanzahl
		int cols = matrix[0].length; // Spaltenanzahl
		int low = 0; // untere Grenze
		int high = rows * cols - 1; // obere Grenze (1D-Darstellung)

		while (low <= high) {
			int mid = low + (high - low) / 2; // Mittelwert berechnen
			int value = matrix[mid / cols][mid % cols]; // 1D → 2D Mapping

			if (value == target) {
				return new int[]{mid / cols, mid % cols}; // gefunden
			} else if (value < target) {
				low = mid + 1; // rechte Hälfte durchsuchen
			} else {
				high = mid - 1; // linke Hälfte durchsuchen
			}
		}

		return new int[]{-1, -1}; // nicht gefunden
	}
}
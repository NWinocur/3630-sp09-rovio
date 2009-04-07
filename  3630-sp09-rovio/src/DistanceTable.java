import java.io.*;
import java.util.Comparator;
import java.util.Scanner;

public class DistanceTable {
	private double[][] table;
	
	public DistanceTable(final File file) throws FileNotFoundException {
		loadFile(file);
	}
		
	private void loadFile(final File file) throws FileNotFoundException {
		
		Scanner scanner = new Scanner(file);
		//System.out.println("test");
		
		int numLines = 0;
		while(scanner.hasNextLine()) {
			numLines++;
			scanner.nextLine();
		}
		scanner.close();
		
		scanner = new Scanner(file);

		table = new double[numLines][2];
		String[] values;
		String line;
		int row = 0;
		for(int i = 0; i < numLines; i++) {
			line = scanner.nextLine().trim();
			if(line != null && !line.equals("")) {
				values = line.split("\t");
				for(int j = 0; j < values.length; j++) {
					table[row][j] = Double.parseDouble(values[j]);
				}
				row++;
			}
		}
		
		scanner.close();
	}
	
	private static final Comparator<Double> doubleComparator = new Comparator<Double>() {
		@Override
		public int compare(Double arg0, Double arg1) {
			return Double.compare((Double)arg0, (Double)arg1);
		}
	};
	
	private static final Comparator<Double> doubleInvertedComparator = new Comparator<Double>() {
		@Override
		public int compare(Double arg0, Double arg1) {
			return Double.compare((Double)arg1, (Double)arg0);
		}
	};
	
	private int findClosestIndex(int column, double value) {
		final Comparator<Double> comparator = (table[0][column] - table[1][column]) > 0 ? doubleInvertedComparator : doubleComparator;
		double first, second;
		for(int i = 0; i < table.length - 1; i++) {
			first = table[i][column];

			if(comparator.compare(value, first) >= 0) {
				second = table[i + 1][column];
				
				if(comparator.compare(value, second) <= 0) {
					double distToFirst = Math.abs(value - first);
					double distToSecond = Math.abs(value - second);

//					System.out.println("between " + first + " and " + second);
//					System.out.print("Closest to ");
					
					if(distToFirst < distToSecond) {
//						System.out.println(first);
						return i;
					} else {
//						System.out.println(second);
						return i + 1;
					}
				}
			}
		}
		
		if(comparator.compare(value, table[0][column]) <= 0) {
			return 0;
		} else {
			return table.length - 1;
		}
		
//		return -1;
	}
	
	public double getDistance(int pixelHeight) {
		return table[findClosestIndex(1, pixelHeight)][0];
	}
	
/*	public static void main(String args[]) {
		try {
			DistanceTable t = new DistanceTable(new File("distanceTable.txt"));
			System.out.println(t.getDistance(200));
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
	}*/
}

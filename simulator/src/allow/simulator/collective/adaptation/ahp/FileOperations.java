package allow.simulator.collective.adaptation.ahp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class FileOperations {

    public static ArrayList<ArrayList<Double>> fileToArrayListOfArrayLists(String file) {
	ArrayList<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>();
	BufferedReader reader = null;
	try {
	    reader = new BufferedReader(new FileReader(new File(file)));
	    String line = null;
	    StringTokenizer st;
	    try {
		while ((line = reader.readLine()) != null) {
		    ArrayList<Double> matrixLine = new ArrayList<Double>();
		    st = new StringTokenizer(line, ", ");
		    while (st.hasMoreElements()) {
			String element = (String) st.nextElement();
			// System.out.print(element + " ");
			matrixLine.add(Double.parseDouble(element));
		    }
		    matrix.add(matrixLine);

		    // System.out.println(" +"+tableLine.get(0)+" ");
		}

	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	} catch (FileNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return matrix;
    }

    public static ArrayList<Double> fileToArrayList(String file) {
	ArrayList<Double> values = new ArrayList<Double>();
	BufferedReader reader = null;
	try {
	    reader = new BufferedReader(new FileReader(new File(file)));
	    String line = null;
	    StringTokenizer st;
	    try {
		while ((line = reader.readLine()) != null) {
		    st = new StringTokenizer(line, ", 	");
		    while (st.hasMoreElements()) {
			double element = Double.parseDouble((String) st.nextElement());
			values.add(element);
			// String element=(String) st.nextElement();
			// values.add(Double.parseDouble(element));
		    }
		}
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}
	return values;
    }

    public static ArrayList<ArrayList<Double>> fileArrayTo2DArrayList(String[] files) {
	ArrayList<ArrayList<Double>> values = new ArrayList<ArrayList<Double>>();
	for (int i = 0; i < files.length; i++) {
	    values.add(fileToArrayList(files[i]));
	}
	return values;
    }

    public static ArrayList<ArrayList<ArrayList<Double>>> fileArrayTo3DArrayList(String[] files) {
	ArrayList<ArrayList<ArrayList<Double>>> arrayList = new ArrayList<ArrayList<ArrayList<Double>>>();
	for (int i = 0; i < files.length; i++) {
	    arrayList.add(fileToArrayListOfArrayLists(files[i]));
	}
	return arrayList;

    }

    public FileOperations() {
	// TODO Auto-generated constructor stub
    }

}

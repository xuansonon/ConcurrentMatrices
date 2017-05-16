import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class MatrixOperations {
  static int NUM_THREADS;
  static int POOL_SIZE;
  static int[][] matrixA;
  static int[][] matrixB;
  static int[][] matrixC;

  /***
    Method: checkInputValues()
    Parameters: aMatrixSize (Integer), aNumThread (Integer)
    Description: This method will check if aNumThread is a valid number. In order to be valid,
    the value must be between 1 and aMatrixSize. aNumThread also needs to be divisible by
    matrix size for validity.
  ***/
  public static boolean checkInputValues(int aMatrixSize, int aNumThread) {
    if((aNumThread >= 1) && (aNumThread <= aMatrixSize) && ((aMatrixSize % aNumThread) == 0)) {
      return true;
    } else {
      return false;
    }
  }

  /***
    Method: matrixToString()
    Parameters: aMatrix (2D Array of Integers)
    Description: This method will convert a matrix to a String ready for export to a file, or
    a System output print.
  ***/
  public static String matrixToString(int[][] aMatrix) {
    String resultMatrix = "";
    for(int i = 0; i < aMatrix.length; i++) {
      for(int j = 0; j < aMatrix.length; j++) {
        resultMatrix += aMatrix[i][j] + " ";
      }
      resultMatrix += "\n";
    }
    return resultMatrix;
  }

  /***
    Method: readInputFile()
    Parameters: fileName (String)
    Description: This method will read any specified input file. The is assumed that the form of
    the file is correct, thus the method of reading follows a "HARDCODED" principle.
  ***/
  public static void readInputFile(String fileName) {
    try {
      BufferedReader br = new BufferedReader(new FileReader(fileName));
      int MATRIX_SIZE = Integer.parseInt(br.readLine().split("=")[1]);
      matrixA = new int[MATRIX_SIZE][MATRIX_SIZE];
      matrixB = new int[MATRIX_SIZE][MATRIX_SIZE];
      NUM_THREADS = Integer.parseInt(br.readLine().split("=")[1]);
      if(!checkInputValues(MATRIX_SIZE, NUM_THREADS)) {
        System.out.println("NUM_THREADS (" + NUM_THREADS + ") is not valid. Please recheck the input file. P needs to be between 1 and " + MATRIX_SIZE + ", and" + MATRIX_SIZE + "needs to be devisible by " + NUM_THREADS + ".\nApplication exitting...");
        System.exit(0);
      }
      POOL_SIZE = Integer.parseInt(br.readLine().split("=")[1]);
      String lineFound = null;
      for(int i = 0; i < MATRIX_SIZE + 1; i++) {
        lineFound = br.readLine().trim();
        if(!(lineFound.equals("A"))) {
          String[] lineSplit = lineFound.split(" ");
          for(int j = 0; j < lineSplit.length; j++) {
            matrixA[i-1][j] = Integer.parseInt(lineSplit[j]);
          }
        }
      }
      lineFound = null;
      for(int i = 0; i < MATRIX_SIZE + 1; i++) {
        lineFound = br.readLine().trim();
        if(!(lineFound.equals("B"))) {
          String[] lineSplit = lineFound.split(" ");
          for(int j = 0; j < lineSplit.length; j++) {
            matrixB[i-1][j] = Integer.parseInt(lineSplit[j]);
          }
        }
      }
      matrixC = new int[MATRIX_SIZE][MATRIX_SIZE];
      for(int i = 0; i < MATRIX_SIZE; i++) {
        Arrays.fill(matrixC[i], 0);
      }
    } catch(FileNotFoundException e) {
      e.printStackTrace();
    } catch(IOException e) {
      e.printStackTrace();
    }
  }

  /***
    Method: initWithoutInput()
    Description: If no input file is specified, the program will generate random matrix values for
    matrixA and matrixB. The NUM_THREADS and POOL_SIZE is also randomly generated. NUM_THREADS is generated
    under the condition that it is valid - and the POOL_SIZE is randomly generated without any condition(s).
    Once generated, the program will export values to a file called "input.txt" for reference.
  ***/
  public static void initWithoutInput() {
    Random rn = new Random();
    int MATRIX_SIZE = -1;
    while((MATRIX_SIZE = rn.nextInt(10) + 1) <= 0) {
      MATRIX_SIZE = rn.nextInt(10) + 1;
    }
    matrixA = new int[MATRIX_SIZE][MATRIX_SIZE];
    matrixB = new int[MATRIX_SIZE][MATRIX_SIZE];
    matrixC = new int[MATRIX_SIZE][MATRIX_SIZE];
    for(int i = 0; i < MATRIX_SIZE; i++) {
      for(int j = 0; j < MATRIX_SIZE; j++) {
        matrixA[i][j] = rn.nextInt(100);
        matrixB[i][j] = rn.nextInt(100);
      }
      Arrays.fill(matrixC[i], 0);
    }
    NUM_THREADS = -1;
    POOL_SIZE = 0;
    while(!checkInputValues(MATRIX_SIZE, NUM_THREADS)) {
      NUM_THREADS = rn.nextInt(MATRIX_SIZE) + 1; //nextInt(value) only goes up to value EXCLUSIVELY, +1 MAKES IT value INCLUSIVE.
    }
    try {
      PrintWriter pw = new PrintWriter(new File("input.txt"));
      pw.write("n="+MATRIX_SIZE+"\n");
      pw.write("P="+NUM_THREADS+"\n");
      pw.write("s="+POOL_SIZE+"\n");
      pw.write("A\n");
      pw.write(matrixToString(matrixA));
      pw.write("B\n");
      pw.write(matrixToString(matrixB));
      pw.close();
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  /***
    Method: testOperations()
    Description: Completes the calculation with ALL possible values of NUM_THREADS. Will also print to file.
  ***/
  public static void testOperations() {
    ArrayList<Integer> multiples = new ArrayList<Integer>();
    for(int i = 1; i <= matrixA.length; i++) {
      if(matrixA.length % i == 0) {
        multiples.add(i);
      }
    }
    System.out.print("Multiples of " + matrixA.length + ": ");
    for(int i = 0; i < multiples.size(); i++) {
      System.out.print(multiples.get(i));
      if(i != (multiples.size() - 1)) {
        System.out.print(", ");
      }
    }
    System.out.println();
    MatrixThread.initializeValues(matrixA, matrixB, matrixC);
    for(int a = 0; a < multiples.size(); a++) {
      NUM_THREADS = multiples.get(a);
      int startingPoint = 0;
      int endingPoint = (matrixA.length/NUM_THREADS)-1;
      int interval = (matrixA.length/NUM_THREADS);
      for(int i = 0; i < NUM_THREADS; i++) {
        Thread th = new Thread(new MatrixThread(startingPoint, endingPoint));
        th.start();
        try {
          th.join();
        } catch(InterruptedException e) {
          e.printStackTrace();
        }
        startingPoint = endingPoint + 1;
        endingPoint += interval;
      }
      printOutputMatrix(true);
      //Clear the matrix every time.
      for(int i = 0; i < matrixA.length; i++) {
        Arrays.fill(matrixC[i], 0);
      }
    }
  }

  /***
    Method: printOutputMatrix()
    Description: This method will print the resultant matrix to a file called "Output.txt".
  ***/
  public static void printOutputMatrix(boolean aMode) {
    try {
      //Clear the file
      PrintWriter pw = new PrintWriter(new FileWriter(new File("output.txt"), aMode));
      if(aMode) {
        pw.write("== TESTING OPERATION ENABLED HERE. ==\n");
        pw.write("NUM_THREADS: " + NUM_THREADS + "\n");
      }
      pw.write("C\n");
      pw.write(matrixToString(matrixC));
      if(aMode) {
        pw.write("== TESTING OPERATION FINISHED. ==\n");
      }
      pw.close();
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  /***
    Method: main()
    Parameters: args (String[])
    Description: The main method will check whether the user has specified a input file or not so the program
    will know if it needs to read or generate random values. This is also where Threads are used for
    concurrent calculation.
  ***/
  public static void main(String[] args) {
    if(args.length == 0) {
      initWithoutInput();
    } else {
      readInputFile(args[0]);
    }
    MatrixThread.initializeValues(matrixA, matrixB, matrixC);
    int startingPoint = 0;
    int endingPoint = (matrixA.length/NUM_THREADS)-1;
    int interval = (matrixA.length/NUM_THREADS);
    ExecutorService es = null;
    //Main method
    if(POOL_SIZE == 0) {
      for(int i = 0; i < NUM_THREADS; i++) {
        Thread th = new Thread(new MatrixThread(startingPoint, endingPoint));
        th.start();
        try {
          th.join();
        } catch(InterruptedException e) {
          e.printStackTrace();
        }
        startingPoint = endingPoint + 1;
        endingPoint += interval;
      }
    } else {
      es = Executors.newFixedThreadPool(POOL_SIZE);
      for(int i = 0; i < NUM_THREADS; i++) {
        es.submit(new MatrixThread(startingPoint, endingPoint));
        startingPoint = endingPoint + 1;
        endingPoint += interval;
      }
      es.shutdown();
      try {
        es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
      } catch(InterruptedException e) {
        e.printStackTrace();
      }
    }
    printOutputMatrix(false);
    //Testing Method
    //testOperations();
  }
}

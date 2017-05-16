class MatrixThread implements Runnable {

  static int[][] matrixA;
  static int[][] matrixB;
  static int[][] matrixC;
  int startingPoint;
  int endingPoint;

  /***
    Method: Matrix()
    Parameters: aStartingPoint (Integer), aEndingPoint (Integer)
    Description: This method initializes the starting and ending point
    in which the thread will complete it's tasks.
  ***/
  public MatrixThread(int aStartingPoint, int aEndingPoint) {
    startingPoint = aStartingPoint;
    endingPoint = aEndingPoint;
  }

  /***
    Method: initializeValues()
    Parameters: aMatrixA (2D Array of Integer), aMatrixB (2D Array of Integer), aMatrixC (2D Array of Integer)
    Description: This method is a static method which will set all the matrices. Every resource initialized here
    is shared with all instances of this class.
  ***/
  public static void initializeValues(int[][] aMatrixA, int[][] aMatrixB, int[][] aMatrixC) {
    matrixA = aMatrixA;
    matrixB = aMatrixB;
    matrixC = aMatrixC;
  }

  /***
    Method: calculate()
    Description: This method calculates the specified row of values (from starting point to ending point) for the
    result matrix.
  ***/
  public void calculate() {
    //System.out.println("Starting point: " + this.startingPoint + ", Ending point: " + this.endingPoint);
    for(int i = this.startingPoint; i <= this.endingPoint; i++) {
      for(int j = 0; j < matrixA.length; j++) {
        for(int k = 0; k < matrixA.length; k++) {
          matrixC[i][j] += (matrixA[i][k] * matrixB[k][j]);
        }
      }
    }
  }

  /***
    Method: run()
    Description: This method runs the calculations for the matrices.
  ***/
  public void run() {
    calculate();
  }
}

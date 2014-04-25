package in.ernet.iitr.puttauec.sensorutil;
//------------------------------------------------------------------------
//Author      : Anas Abidi
//Date        : 18 Dec 2004
//Version     : 2.0
//Description : MatrixLib Operations Library
//------------------------------------------------------------------------	
 public class MatrixLib
	{   private final int M;
	    private final int N;
		public double[][] in_Mat;
      
		/// <summary>
		/// MatrixLib object constructor, constructs an empty
		/// matrix with dimensions: rows = noRows and cols = noCols.
		/// </summary>
		/// <param name="noRows"> no. of rows in this matrix </param>
		/// <param name="noCols"> no. of columns in this matrix</param>
		public MatrixLib(int noRows, int noCols)
		{   M = noRows;
		    N=  noCols;		 
			this.in_Mat = new double[noRows][noCols];
		}

		/// <summary>
		/// MatrixLib object constructor, constructs a matrix from an
		/// already defined array object.
		/// </summary>
		/// <param name="Mat">the array the matrix will contain</param>
		 public MatrixLib(double [][] Mat)
		{   M = Mat.length;
		    N = Mat[0].length;
			this.in_Mat = new double[M][N];
			for (int i = 0; i < M; i++)
		            for (int j = 0; j < N; j++)
		                    this.in_Mat[i][j] = Mat[i][j];
		}

		// copy constructor
	    private MatrixLib(MatrixLib A) { this(A.in_Mat);}

		/// <summary>
		/// Returns the 2D form of a 1D array. i.e. array with
		/// dimension[n] is returned as an array with dimension [n,1]. 
		/// In case of an error the error is raised as an exception.
		/// </summary>
		/// <param name="Mat"> 
		/// the array to convert, with dimesion [n] 
		/// </param>
		/// <returns> the same array but with [n,1] dimension </returns>
		 public static double[][] OneD_2_TwoD (double[] Mat)
		{
			int Rows;
			//Find The dimensions !!
			Rows = Mat.length;
			double[][] Sol = new double[Rows][1];
			
			for (int i=0; i <Rows; i++)
			{
				Sol[i][0] = Mat[i];
			}
			return Sol;
		}

		/// <summary>
		/// Returns the 1D form of a 2D array. i.e. array with
		/// dimension[n,1] is returned as an array with dimension [n]. 
		/// In case of an error the error is raised as an exception.
		/// </summary>
		/// <param name="Mat">
		/// the array to convert, with dimesions [n,1] 
		/// </param>
		/// <returns>the same array but with [n] dimension</returns>
		 public static double[] TwoD_2_OneD(double[][] Mat)
		{
			int Rows;
			int Cols;
			Rows = Mat.length;
			Cols = Mat[0].length;
			
			double[] Sol = new double[Rows];

			for (int i=0; i<Rows;i++)
			{
				Sol[i] = Mat[i][0];
			}
			return Sol;
		}

		/// <summary>
		/// Returns an Identity matrix with dimensions [n,n] in the from of an array.
		/// </summary>
		/// <param name="n">the no. of rows or no. cols in the matrix</param>
		/// <returns>An identity MatrixLib with dimensions [n,n] in the form of an array</returns>
		 public static double[][] Identity(int n)
		{
			double[][] temp = new double[n][n];
			for (int i=0; i<n;i++) temp[i][i] = 1;
			return temp;
		}
		
		/// <summary>
		/// Returns the summation of two matrices with compatible 
		/// dimensions.
		/// In case of an error the error is raised as an exception.
		/// </summary>
		/// <param name="Mat1">First array in the summation</param>
		/// <param name="Mat2">Second array in the summation</param>
		/// <returns>Sum of Mat1 and Mat2 as an array</returns>
		 public static double[][] Add(double[][] Mat1, double[][] Mat2)
		{ 
			double[][] sol;
			int i,j;
			int Rows1, Cols1;
			int Rows2, Cols2;
            Rows1 = Mat1.length;
            Cols1 = Mat1[0].length;
            Rows2 = Mat2.length;
            Cols2 = Mat2[0].length;
		
			sol = new double[Rows1][Cols1];

			for (i = 0;i < Rows1; i++)
				for (j = 0; j< Cols1; j++)
				{
					sol[i][j] = Mat1[i][j] + Mat2[i][j];
				}

			return sol;
		}

		/// <summary>
		/// Returns the summation of two matrices with compatible 
		/// dimensions.
		/// In case of an error the error is raised as an exception.
		/// </summary>
		/// <param name="Mat1">First matrix in the summation</param>
		/// <param name="Mat2">Second matrix in the summation</param>
		/// <returns>Sum of Mat1 and Mat2 as a MatrixLib object</returns>
		 public static MatrixLib Add(MatrixLib Mat1, MatrixLib Mat2)
		  {return new MatrixLib(Add(Mat1.in_Mat,Mat2.in_Mat));}

      
		/// <summary>
		/// Returns the difference of two matrices with compatible 
		/// dimensions.
		/// In case of an error the error is raised as an exception.
		/// </summary>
		/// <param name="Mat1">First array in the subtraction</param>
		/// <param name="Mat2">Second array in the subtraction</param>
		/// <returns>Difference of Mat1 and Mat2 as an array</returns>
		 public static double[][] Subtract(double[][] Mat1, double[][] Mat2)
		{ 
			double[][] sol;
			int i,j;
			int Rows1, Cols1;
			int Rows2, Cols2;

			Rows1 = Mat1.length;
            Cols1 = Mat1[0].length;
            Rows2 = Mat2.length;
            Cols2 = Mat2[0].length;
			
			sol = new double[Rows1][Cols1];

			for (i = 0;i < Rows1; i++)
				for (j = 0; j< Cols1; j++)
				{
					sol[i][j] = Mat1[i][j] - Mat2[i][j];
				}

			return sol;
		}

		/// <summary>
		/// Returns the difference of two matrices with compatible 
		/// dimensions.
		/// In case of an error the error is raised as an exception.
		/// </summary>
		/// <param name="Mat1">First matrix in the subtraction</param>
		/// <param name="Mat2">Second matrix in the subtraction</param>
		/// <returns>Difference of Mat1 and Mat2 as a MatrixLib object</returns>
		 public static MatrixLib Subtract(MatrixLib Mat1, MatrixLib Mat2)
		{return new MatrixLib(Subtract(Mat1.in_Mat,Mat2.in_Mat));}

		/// <summary>
		/// Returns the multiplication of two matrices with compatible 
		/// dimensions.
		/// In case of an error the error is raised as an exception. 
		/// </summary>
		/// <param name="Mat1">First array in multiplication</param>
		/// <param name="Mat2">Second array in multiplication</param>
		/// <returns>Mat1 multiplied by Mat2 as an array</returns>
		 public static double[][] Multiply(double[][] Mat1, double[][] Mat2)
		{
			double[][] sol;
			int Rows1, Cols1;
			int Rows2, Cols2;
			double MulAdd = 0;

			Rows1 = Mat1.length;
            Cols1 = Mat1[0].length;
            Rows2 = Mat2.length;
            Cols2 = Mat2[0].length;
				
			if (Cols1 != Rows2) throw new RuntimeException("Illegal MatrixLib Multiplication");

			sol = new double[Rows1][Cols1];

			for (int i=0; i<Rows1; i++)
				for (int j = 0; j<Cols2; j++)
				{  for (int l = 0; l<Cols1; l++)
					{
						MulAdd = MulAdd + Mat1[i][l] * Mat2[l][j];
					}
					sol[i][j] = MulAdd;
					MulAdd = 0;
				}
			return sol;
		}

		/// <summary>
		/// Returns the multiplication of two matrices with compatible 
		/// dimensions OR the cross-product of two vectors.
		/// In case of an error the error is raised as an exception. 
		/// </summary>
		/// <param name="Mat1">
		/// First matrix or vector (i.e: dimension [3,1]) object in 
		/// multiplication
		/// </param>
		/// <param name="Mat2">
		/// Second matrix or vector (i.e: dimension [3,1]) object in 
		/// multiplication
		/// </param>
		/// <returns>Mat1 multiplied by Mat2 as a MatrixLib object</returns>
		 public static MatrixLib Multiply(MatrixLib Mat1, MatrixLib Mat2)
		{
			if ((Mat1.M==3) && (Mat2.M==3) &&
				(Mat1.N==1) && (Mat2.N==1)) 
			{return new MatrixLib(CrossProduct(Mat1.in_Mat,Mat2.in_Mat));}
			else
			{return new MatrixLib(Multiply(Mat1.in_Mat,Mat2.in_Mat));}
		}

		/// <summary>
		/// Returns the determinant of a matrix with [n,n] dimension.
		/// In case of an error the error is raised as an exception. 
		/// </summary>
		/// <param name="Mat">
		/// Array with [n,n] dimension whose determinant is to be found
		/// </param>
		/// <returns>Determinant of the array</returns>
		 public static double Det(double[][] Mat)
		{    
			int S,k, k1, i,j;
			double save, ArrayK, tmpDet;
			int Rows, Cols;
			
			Rows = Mat.length;
            Cols = Mat[0].length;
			
			if (Rows != Cols)  throw new RuntimeException("Determinant Not possible");

			S = Rows;
			tmpDet = 1;

			for (k = 0; k < S; k++)
			{
				if (Mat[k][k] == 0)
				{
					j = k;
					while ((j < S) && (Mat[k][j] == 0)) j = j + 1;
					if (Mat[k][j] == 0) return 0;
					else
					{   
						for (i = k; i < S; i++)
						{
							save = Mat[i][j];
							Mat[i][j] = Mat[i][k];
							Mat[i][k] = save;
						}
					}
					tmpDet = -tmpDet;
				}
				ArrayK = Mat[k][k];
				tmpDet = tmpDet * ArrayK;
				if (k < S) 
				{
					k1 = k + 1;
					for (i = k1; i < S; i++)
					{ 
						for (j = k1; j < S; j++) 
							Mat[i][j] = Mat[i][j] - Mat[i][k] * (Mat[k][j] / ArrayK);
					}
				}
			}
			return tmpDet;
		}

		/// <summary>
		/// Returns the determinant of a matrix with [n,n] dimension.
		/// In case of an error the error is raised as an exception. 
		/// </summary>
		/// <param name="Mat">
		/// MatrixLib object with [n,n] dimension whose determinant is to be found
		/// </param>
		/// <returns>Determinant of the MatrixLib object</returns>
		 public static double Det(MatrixLib Mat)
		{return Det(Mat.in_Mat);}
	
		
		/// <summary>
		/// Returns the inverse of a matrix with [n,n] dimension 
		/// and whose determinant is not zero.
		/// In case of an error the error is raised as an exception. 
		/// </summary>
		/// <param name="Mat">
		/// Array with [n,n] dimension whose inverse is to be found
		/// </param>
		/// <returns>Inverse of the array as an array</returns>
		 public static double[][] Inverse(double[][] Mat)
		{
			double[][] AI, Mat1;
			double AIN, AF;
			int Rows, Cols;
			int LL,LLM,L1,L2,LC,LCA,LCB;
            Rows = Mat.length;
            Cols = Mat.length;
			Mat1 = new double[Rows][];
			for (int r = 0; r < Rows; r++) {
			       Mat1[r] = Mat[r].clone();
			}
			if (Rows != Cols)  throw new RuntimeException("Inverse Not possible");
			if (Det(Mat) == 0) throw new RuntimeException("Singular MatrixLib");

			LL = Rows;
			LLM = Cols;
			AI = new double[LL][LL];

			for (L2 = 0; L2 < LL; L2++)
			{
				for (L1 = 0; L1 < LL; L1++) AI[L1][L2] = 0;
				AI[L2][L2] = 1;
			}

			for (LC = 0; LC < LL; LC++)
			{
				if (Math.abs(Mat1[LC][LC]) < 0.0000000001)
				{
					for (LCA = LC + 1; LCA < LL; LCA++)
					{
						if (LCA == LC) continue;
						if (Math.abs(Mat1[LC][LCA]) > 0.0000000001)
						{
							for (LCB = 0; LCB < LL; LCB++)
							{
								Mat1[LCB][LC] = Mat1[LCB][LC] + Mat1[LCB][LCA];
								AI[LCB][LC] = AI[LCB][LC] + AI[LCB][LCA];
							}
							break;
						}
					}
				}
				AIN = 1 / Mat1[LC][LC];
				for (LCA = 0; LCA < LL; LCA++)
				{
					Mat1[LCA][LC] = AIN * Mat1[LCA][LC];
					AI[LCA][LC] = AIN * AI[LCA][LC];
				}

				for (LCA = 0; LCA < LL; LCA++)
				{
					if (LCA == LC) continue;
					AF = Mat1[LC][LCA];
					for (LCB = 0; LCB < LL; LCB++)
					{
						Mat1[LCB][LCA] = Mat1[LCB][LCA] - AF * Mat1[LCB][LC];
						AI[LCB][LCA] = AI[LCB][LCA] - AF * AI[LCB][LC];
					}
				}
			}
			return AI;
		}

		/// <summary>
		/// Returns the inverse of a matrix with [n,n] dimension 
		/// and whose determinant is not zero.
		/// In case of an error the error is raised as an exception. 
		/// </summary>
		/// <param name="Mat">
		/// MatrixLib object with [n,n] dimension whose inverse is to be found
		/// </param>
		/// <returns>Inverse of the matrix as a MatrixLib object</returns>
		 public static MatrixLib Inverse(MatrixLib Mat)
		{return new MatrixLib(Inverse(Mat.in_Mat));}
		/// <summary>
		/// Returns the transpose of a matrix.
		/// In case of an error the error is raised as an exception. 
		/// </summary>
		/// <param name="Mat">Array whose transpose is to be found</param>
		/// <returns>Transpose of the array as an array</returns>
		 public static double[][] Transpose(double[][] Mat)
		{
			double[][] Tr_Mat;
			int i, j, Rows, Cols;
            Rows = Mat.length;
            Cols = Mat[0].length;
			
			Tr_Mat = new double[Cols][Rows];

			for (i = 0; i<Rows;i++)
				for (j = 0; j<Cols;j++) Tr_Mat[j][i] = Mat[i][j];
          
			return Tr_Mat;
		}		
		
		/// <summary>
		/// Returns the transpose of a matrix.
		/// In case of an error the error is raised as an exception. 
		/// </summary>
		/// <param name="Mat">MatrixLib object whose transpose is to be found</param>
		/// <returns>Transpose of the MatrixLib object as a MatrixLib object</returns>
		 public static MatrixLib Transpose(MatrixLib Mat)
		{return new MatrixLib(Transpose(Mat.in_Mat));}																			
		
	    /// <summary>
		/// Returns the multiplication of a matrix or a vector (i.e 
		/// dimension [3,1]) with a scalar quantity.
		/// In case of an error the error is raised as an exception.
		/// </summary>
		/// <param name="Value">The scalar value to multiply the array</param>
		/// <param name="Mat">Array which is to be multiplied by a scalar</param>
		/// <returns>The multiplication of the scalar and the array as an array</returns>
		 public static double[][] ScalarMultiply(double Value, double[][] Mat)
		{
			int i,j, Rows, Cols;
			double[][] sol;
            Rows = Mat.length;
            Cols = Mat[0].length;
			sol  = new double[Rows][Cols];
			for (i = 0; i<Rows;i++)
				for (j = 0; j<Cols;j++)
					sol[i][j] = Mat[i][j] * Value;

			return sol;
		}

		/// <summary>
		/// Returns the multiplication of a matrix or a vector (i.e 
		/// dimension [3,1]) with a scalar quantity.
		/// In case of an error the error is raised as an exception.
		/// </summary>
		/// <param name="Value">The scalar value to multiply the array</param>
		/// <param name="Mat">MatrixLib which is to be multiplied by a scalar</param>
		/// <returns>The multiplication of the scalar and the array as an array</returns>
		 public static MatrixLib ScalarMultiply(double Value, MatrixLib Mat)
		{return new MatrixLib(ScalarMultiply(Value,Mat.in_Mat));}

		/// <summary>
		/// Returns the division of a matrix or a vector (i.e 
		/// dimension [3,1]) by a scalar quantity.
		/// In case of an error the error is raised as an exception.
		/// </summary>
		/// <param name="Value">The scalar value to divide the array with</param>
		/// <param name="Mat">Array which is to be divided by a scalar</param>
		/// <returns>The division of the array and the scalar as an array</returns>
		 public static double[][] ScalarDivide(double Value, double[][] Mat)
		{
			int i,j, Rows, Cols;
			double[][] sol;

			Rows = Mat.length;
			Cols = Mat[0].length;
			
			sol = new double[Rows][Cols];

			for (i = 0; i<Rows;i++)
				for (j = 0; j<Cols;j++)
					sol[i][j] = Mat[i][j] / Value;

			return sol;
		}

		/// <summary>
		/// Returns the division of a matrix or a vector (i.e 
		/// dimension [3,1]) by a scalar quantity.
		/// In case of an error the error is raised as an exception.
		/// </summary>
		/// <param name="Value">The scalar value to divide the array with</param>
		/// <param name="Mat">MatrixLib which is to be divided by a scalar</param>
		/// <returns>The division of the array and the scalar as an array</returns>
		 public static MatrixLib ScalarDivide(double Value, MatrixLib Mat)
		{return new MatrixLib(ScalarDivide(Value,Mat.in_Mat));}

		/// <summary>
		/// Returns the cross product of two vectors whose
		/// dimensions should be [3] or [3,1].
		/// In case of an error the error is raised as an exception.
		/// </summary>
		/// <param name="V1">First vector array (dimension [3]) in the cross product</param>
		/// <param name="V2">Second vector array (dimension [3]) in the cross product</param>
		/// <returns>Cross product of V1 and V2 as an array (dimension [3])</returns>
		 public static double[] CrossProduct(double[] V1, double[] V2)
		{
			double i,j,k;
			double[] sol = new double[2];
			int Rows1 = V1.length;
			int Rows2 = V2.length;
			
			i = V1[1] * V2[2] - V1[2] * V2[1];
			j = V1[2] * V2[0] - V1[0] * V2[2];
			k = V1[0] * V2[1] - V1[1] * V2[0];

			sol[0] = i ; sol[1] = j ; sol[2] = k;

			return sol;
		}

		/// <summary>
		/// Returns the cross product of two vectors whose
		/// dimensions should be [3] or [3x1].
		/// In case of an error the error is raised as an exception.
		/// </summary>
		/// <param name="V1">First vector array (dimensions [3,1]) in the cross product</param>
		/// <param name="V2">Second vector array (dimensions [3,1]) in the cross product</param>
		/// <returns>Cross product of V1 and V2 as an array (dimension [3,1])</returns>
		 public static double[][] CrossProduct(double[][] V1, double[][] V2)
		{
			double i,j,k;
			double[][] sol = new double[3][1];
			int Rows1, Cols1;
			int Rows2, Cols2;
            Rows1 = V1.length; 
			Rows2 = V2.length;
			Cols1 = V1[0].length;
			Cols2 = V2[0].length; 
			
			i = V1[1][0] * V2[2][0] - V1[2][0] * V2[1][0];
			j = V1[2][0] * V2[0][0] - V1[0][0] * V2[2][0];
			k = V1[0][0] * V2[1][0] - V1[1][0] * V2[0][0];

			sol[0][0] = i ; sol[1][0] = j ; sol[2][0] = k;

			return sol;
		}

		/// <summary>
		/// Returns the cross product of two vectors whose
		/// dimensions should be [3] or [3x1].
		/// In case of an error the error is raised as an exception.
		/// </summary>
		/// <param name="V1">First MatrixLib (dimensions [3,1]) in the cross product</param>
		/// <param name="V2">Second MatrixLib (dimensions [3,1]) in the cross product</param>
		/// <returns>Cross product of V1 and V2 as a matrix (dimension [3,1])</returns>
		 public static MatrixLib CrossProduct(MatrixLib V1, MatrixLib V2)
		{return (new MatrixLib((CrossProduct(V1.in_Mat,V2.in_Mat))));}
		
		/// <summary>
		/// Returns the dot product of two vectors whose
		/// dimensions should be [3] or [3,1].
		/// In case of an error the error is raised as an exception.
		/// </summary>
		/// <param name="V1">First vector array (dimension [3]) in the dot product</param>
		/// <param name="V2">Second vector array (dimension [3]) in the dot product</param>
		/// <returns>Dot product of V1 and V2</returns>
		 public static double DotProduct(double[] V1, double[] V2)
		{
			int Rows1 = V1.length;
			int Rows2 = V2.length;
			return (V1[0] * V2[0] + V1[1] * V2[1] + V1[2] * V2[2]);
		}

		/// <summary>
		/// Returns the dot product of two vectors whose
		/// dimensions should be [3] or [3,1].
		/// In case of an error the error is raised as an exception.
		/// </summary>
		/// <param name="V1">First vector array (dimension [3,1]) in the dot product</param>
		/// <param name="V2">Second vector array (dimension [3,1]) in the dot product</param>
		/// <returns>Dot product of V1 and V2</returns>
		 public static double DotProduct(double[][] V1, double[][] V2)
		{
			int Rows1 = V1.length, Cols1= V1[0].length;
			int Rows2 = V2.length, Cols2= V2[0].length;
			return (V1[0][0] * V2[0][0] + V1[1][0] * V2[1][0] + V1[2][0] * V2[2][0]);
		}

		/// <summary>
		/// Returns the dot product of two vectors whose
		/// dimensions should be [3] or [3,1].
		/// In case of an error the error is raised as an exception.
		/// </summary>
		/// <param name="V1">First MatrixLib object (dimension [3,1]) in the dot product</param>
		/// <param name="V2">Second MatrixLib object (dimension [3,1]) in the dot product</param>
		/// <returns>Dot product of V1 and V2</returns>
		 public static double DotProduct(MatrixLib V1, MatrixLib V2)
		{return (DotProduct(V1.in_Mat, V2.in_Mat));}

		/// <summary>
		///  Returns the magnitude of a vector whose dimension is [3] or [3,1].
		///  In case of an error the error is raised as an exception.
		/// </summary>
		/// <param name="V">The vector array (dimension [3]) whose magnitude is to be found</param>
		/// <returns>The magnitude of the vector array</returns>
		 public static double VectorMagnitude(double[] V)
		{
			int Rows = V.length;
			return Math.sqrt(V[0] * V[0] + V[1] * V[1] + V[2] * V[2]);
		}
	
		/// <summary>
		///  Returns the magnitude of a vector whose dimension is [3] or [3,1].
		///  In case of an error the error is raised as an exception.
		/// </summary>
		/// <param name="V">The vector array (dimension [3,1]) whose magnitude is to be found</param>
		/// <returns>The magnitude of the vector array</returns>
		 public static double VectorMagnitude(double[][] V)
		{
			int Rows, Cols;
            Rows = V.length;
            Cols = V[0].length;
			
			return Math.sqrt(V[0][0] * V[0][0] + V[1][0] * V[1][0] + V[2][0] * V[2][0]);
		}														
		
		/// <summary>
		///  Returns the magnitude of a vector whose dimension is [3] or [3,1].
		///  In case of an error the error is raised as an exception.
		/// </summary>
		/// <param name="V">MatrixLib object (dimension [3,1]) whose magnitude is to be found</param>
		/// <returns>The magnitude of the MatrixLib object</returns>
		 public static double VectorMagnitude(MatrixLib V)
		{return (VectorMagnitude(V.in_Mat));}					

		/// <summary>
		/// Checks if two Arrays of equal dimensions are equal or not.
		/// In case of an error the error is raised as an exception.
		/// </summary>
		/// <param name="Mat1">First array in equality check</param>
		/// <param name="Mat2">Second array in equality check</param>
		/// <returns>If two matrices are equal or not</returns>
		 public static Boolean IsEqual (double[][] Mat1, double[][] Mat2)
		{
			double eps = 1E-14;
			int Rows1, Cols1;
			int Rows2, Cols2;
            Rows1 = Mat1.length; 
            Cols1 = Mat1[0].length;
			
            for (int i=0; i<Rows1; i++)
			{
				for (int j=0; j<Cols1; j++)
				{
					if (Math.abs(Mat1[i][j] - Mat2[i][j])>eps) return false;
				}
			}
			return true;
		}

		/// <summary>
		/// Checks if two matrices of equal dimensions are equal or not.
		/// In case of an error the error is raised as an exception.
		/// </summary>
		/// <param name="Mat1">First MatrixLib in equality check</param>
		/// <param name="Mat2">Second MatrixLib in equality check</param>
		/// <returns>If two matrices are equal or not</returns>
		 public static Boolean IsEqual (MatrixLib Mat1, MatrixLib Mat2)
		{return IsEqual(Mat1.in_Mat, Mat2.in_Mat);}
}

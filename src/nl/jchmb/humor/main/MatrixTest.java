package nl.jchmb.humor.main;

import Jama.Matrix;

public class MatrixTest {
	public static void main(String[] args) {
		double[][] vals = {
				{1., 2.},
				{1., 3.},
				{1., 4.}
			};
		Matrix m = new Matrix(vals);
		System.out.println(m);
	}
}

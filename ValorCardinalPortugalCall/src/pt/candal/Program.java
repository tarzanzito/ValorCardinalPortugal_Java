package pt.candal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Program {

	public static void main(String[] args) throws IOException {

		System.out.println("");
		System.out.println("ValorCardinalPortugal (Version: 1.0.4)");
		System.out.println("======================================");
		System.out.println("");

		System.out.print("Introduza valor '#0.00':");

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String valor = reader.readLine();

		System.out.println("");
		System.out.println("Processa :[" + valor + "]");
		System.out.println("");

		// executa
		ValorCardinalPortugal objIns = new ValorCardinalPortugal();
		String resultado = objIns.converte(valor);

		System.out.println("Resultado:[" + resultado + "]");
		System.out.println("");

		System.out.println("enter\n");
		int x = reader.read();
		
	}
}

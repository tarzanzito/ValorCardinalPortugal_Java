package pt.candal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class ValorCardinalPortugal {

	// campos em ordinal

	// xxx
	private static final String[] CARDINAL_UNIDADES = { "", "um", "dois", "três", "quatro", "cinco", "seis", "sete",
			"oito", "nove" };
	private static final String[] CARDINAL_DEZENAS = { "", "dez", "vinte", "trinta", "quarenta", "cinquenta",
			"sessenta", "setenta", "oitenta", "noventa" };
	private static final String[] CARDINAL_DEZENAS_DEZ = { "dez", "onze", "doze", "treze", "catorze", "quinze",
			"desasseis", "desassete", "dezoito", "dezanove" };
	private static final String[] CARDINAL_CENTENAS = { "", "cento", "duzentos", "trezentos", "quatrocentos",
			"quinhentos", "seiscentos", "setecentos", "oitocentos", "novecentos" };
	private static final String CARDINAL_ZERO = "zero";
	private static final String CARDINAL_UM = "um";
	private static final String CARDINAL_UMA = "uma";
	private static final String CARDINAL_DOIS = "dois";
	private static final String CARDINAL_DUAS = "duas";	
	private static final String CARDINAL_CEM = "cem";

	private static final String[] CARDINAL_GRUPOS_PLURAL = { "", "mil", "milhões", "milhares de milhão", "biliões",
			"dezenas de bilião", "centenas de bilião", "milhares de bilião", "dezenas de milhar de bilião",
			"centenas de milhar de bilião", "triliões" };

	private static final String[] CARDINAL_GRUPOS_SINGULAR = { "", "mil", "milhão", "milhar de milhão", "bilião",
			"dezena de bilião", "centena de bilião", "milhar de bilião", "dezena de milhar de bilião",
			"centena de milhar de bilião", "trilião" };

	private static final boolean[] CARDINAL_GRUPOS_MASCULINO = { true, true, true, true, true, false, false, true,
			false, false, true };

	private static final String FRASE_SUFIXO_AO = "ão";
	private static final String FRASE_SUFIXO_OES = "ões";

	private static final String FRASE_E = " e ";
	private static final String FRASE_VIRGULA = ", ";
	private static final String FRASE_DE = " de";
	private static final String FRASE_NOME_INTEIROS_PLURAL = "euros";
	private static final String FRASE_NOME_INTEIROS_SINGULAR = "euro";
	private static final String FRASE_NOME_DECIMAIS_PLURAL = "centimos";
	private static final String FRASE_NOME_DECIMAIS_SINGULAR = "centimo";
	private static final String FRASE_MENOS = "menos ";

	public String converte(BigDecimal valor) {

		return converte(valor, false, false);
	}

	public String converte(BigDecimal valor, boolean vazioSeZeroParteinteira) {

		return converte(valor, vazioSeZeroParteinteira, false);
	}

	public String converte(BigDecimal valor, boolean vazioSeZeroParteinteira, boolean vazioSeZeroParteDecimal) {

		BigDecimal bigDecimal = valor.setScale(2, RoundingMode.FLOOR);
		String temp = bigDecimal.toString();

		return converte(temp, vazioSeZeroParteinteira, vazioSeZeroParteDecimal);
	}

	public String converte(String valor) {

		return converte(valor, false, false);
	}

	public String converte(String valor, boolean vazioSeZeroParteinteira) {

		return converte(valor, vazioSeZeroParteinteira, false);
	}

	public String converte(String valor, boolean vazioSeZeroParteinteira, boolean vazioSeZeroParteDecimal) {

		String valorTrim = valor.trim();
		
		// validação e formatação do impute
		if (!validaValor(valorTrim))
			return "ERRO: não é um valor valido: " + valor;

		String valorForm = formataValor(valorTrim);

		// inicio
		boolean negativo = valorNegativo(valorForm);

		String valorInicial;
		if (negativo)
			valorInicial = valorForm.substring(1);
		else
			valorInicial = valorForm;

		// processa

		// separa parte inteira pare decimal
		String[] partes = divideEmPartesInteiraDecimal(valorInicial);

		// separa por grupos de mil "???"
		String[] gruposInteiros = divideEmGruposDeMil(partes[0]);
		String[] gruposDecimais = divideEmGruposDeMil(partes[1]);

		// descodifica os grupos inteiros
		String[] gruposCardinaisInteiros = new String[gruposInteiros.length];
		for (int x = 0; x < gruposInteiros.length; x++)
			gruposCardinaisInteiros[x] = descodificaCardinal(gruposInteiros[x], (gruposInteiros.length - x - 1));

		// descodifica os groupos decimais
		String[] gruposCardinaisDecimais = new String[gruposDecimais.length];
		for (int x = 0; x < gruposDecimais.length; x++)
			gruposCardinaisDecimais[x] = descodificaCardinal(gruposDecimais[x], (gruposDecimais.length - x - 1));

		// junta todos os grupos
		String finalInteiros = juntaTodosGruposDeMil(gruposCardinaisInteiros, vazioSeZeroParteinteira);
		String finalDecimais = juntaTodosGruposDeMil(gruposCardinaisDecimais, vazioSeZeroParteDecimal);

		// caso: se valor = 0.0 mostra sempre "zero"
		if ((finalInteiros.length() == 0) && (finalDecimais.length() == 0))
			finalInteiros = CARDINAL_ZERO + " " + FRASE_NOME_INTEIROS_PLURAL;

		// caso: analiza se coloca "de" antes do qualificador
		if (finalInteiros.length() > 2) {
			String temp = finalInteiros.substring((finalInteiros.length() - 3), finalInteiros.length());
			if (temp.equals(FRASE_SUFIXO_OES))
				finalInteiros += FRASE_DE;
			else {
				temp = finalInteiros.substring((finalInteiros.length() - 2), finalInteiros.length());
				if (temp.equals(FRASE_SUFIXO_AO))
					finalInteiros += FRASE_DE;
			}
		}

		// obtem qualificadores
		String qualificadorInteiros = obtemQualificadorParteInteira(partes[0], vazioSeZeroParteinteira);
		String qualificadoreDecimais = obtemQualificadorParteBigDecimal(partes[1], vazioSeZeroParteDecimal);

		// adiciona qualificador inteiros
		if (finalInteiros.length() > 0)
			finalInteiros += " " + qualificadorInteiros;

		// adiciona qualificador decimais
		if (finalDecimais.length() > 0)
			finalDecimais += " " + qualificadoreDecimais;

		// caso: adiciona " e " entre a frase inteiros & frase decimais
		String dual = "";
		if ((finalInteiros.length() > 0) && (finalDecimais.length() > 0))
			dual = FRASE_E;

		String resultdofinal = finalInteiros + dual + finalDecimais;
		if (negativo)
			resultdofinal = FRASE_MENOS + resultdofinal;

		return resultdofinal;
	}

	private String[] divideEmPartesInteiraDecimal(String valor) {

		if (valor.indexOf(".") == -1)
			valor += ".00";

		String[] partes = valor.split("\\.");

		return partes;
	}

	private String[] divideEmGruposDeMil(String valor) {

		// extrai
		List<String> list = new ArrayList<>();
		while (valor.length() > 3) {
			String str3 = valor.substring(valor.length() - 3);
			list.add(str3);
			valor = valor.substring(0, valor.length() - 3);
		}

		list.add(String.format("%1$3s", valor).replace(' ', '0')); // garante comprimento = 3

		// reverte array
		int count = list.size();
		String[] groupos = new String[count];
		for (int x = 0; x < count; x++)
			groupos[count - 1 - x] = list.get(x);

		return groupos;
	}

	private String juntaTodosGruposDeMil(String[] grouposEmCardinal, boolean vazioSeZero) {

		String resultado = "";
		int pos;
		
		for (int x = 0; x < grouposEmCardinal.length; x++) {

			if (grouposEmCardinal[x].length() == 0)
				continue;

			// no ultimo elemento analisa se coloca " e " no fim
			if ((x == (grouposEmCardinal.length - 1)) && (resultado.length() > 1)) {
				pos = grouposEmCardinal[x].indexOf(FRASE_E);
				if (pos == -1) {
					resultado = removeUltimasVirgulasEmExcesso(resultado);
					resultado += FRASE_E;
				}
			}

			resultado += grouposEmCardinal[x];
			resultado += FRASE_VIRGULA;
		}

		if ((resultado.length() == 0) && (!vazioSeZero))
			resultado = CARDINAL_ZERO;

		resultado = removeUltimasVirgulasEmExcesso(resultado);
		
        //caso: quantos " e " existem depois da ultima virgula? se zero substitui ultima ", " por " e " 
        pos = resultado.lastIndexOf(FRASE_VIRGULA);
        if (pos > 0)
        {
            String temp1 = resultado.substring(0, pos);
            String temp2 = resultado.substring(pos + FRASE_VIRGULA.length());
            pos = temp2.indexOf(FRASE_E);
            if (pos == -1)
                resultado = temp1 + FRASE_E + temp2;
        }        

		return resultado;
	}

	private String removeUltimasVirgulasEmExcesso(String valor) {

		if (valor.length() < 2)
			return valor;

		String resultado = valor;

		while (resultado.substring(resultado.length() - 2, resultado.length()).equals(FRASE_VIRGULA))
			resultado = resultado.substring(0, resultado.length() - 2);

		return resultado;
	}

	private String descodificaCardinal(String valor, int nivel) {

		if (valor.equals("000"))
			return "";

		String[] cardinalArray = new String[3];
		byte[] digitArray = new byte[3];

		for (byte x = 0; x < 3; x++)
			digitArray[x] = Byte.parseByte(valor.substring(x, (x + 1)));

		cardinalArray[0] = obtemCentenas(digitArray[0], digitArray[1], digitArray[2]);
		cardinalArray[1] = obtemDezenas(digitArray[1], digitArray[2]);
		cardinalArray[2] = obtemUnidades(digitArray[2], digitArray[1]);

		String resultado = juntaCentenasDezenasUnidades(cardinalArray[0], cardinalArray[1], cardinalArray[2]);

		resultado = adicionaSufixoDeGrupoMil(resultado, nivel);

		return resultado;
	}

	private String juntaCentenasDezenasUnidades(String centena, String dezena, String unidade) {

		String resultado = centena;
		if ((centena.length() > 0) && ((dezena.length() > 0) || (unidade.length() > 0)))
			resultado += FRASE_E;

		resultado += dezena;
		if ((dezena.length() > 0) && (unidade.length() > 0))
			resultado += FRASE_E;

		resultado += unidade;

		return resultado;
	}

	private String obtemUnidades(byte digito, byte dezena) {

		if (dezena == 1)
			return "";

		return CARDINAL_UNIDADES[digito];
	}

	private String obtemDezenas(byte digito, byte unidade) {

		if (digito == 1)
			return CARDINAL_DEZENAS_DEZ[unidade];

		return CARDINAL_DEZENAS[digito];
	}

	private String obtemCentenas(byte digito, byte dezena, byte unidade) {

		if ((digito == 1) && (dezena == 0) && (unidade == 0))
			return CARDINAL_CEM; // Caso : Cem

		return CARDINAL_CENTENAS[digito];
	}

	private String obtemQualificadorParteBigDecimal(String valor, boolean vazioSeZero) {

		byte valTemp = Byte.parseByte(valor);

		if (valTemp > 1)
			return FRASE_NOME_DECIMAIS_PLURAL;

		if (valTemp == 1)
			return FRASE_NOME_DECIMAIS_SINGULAR;

		if ((valTemp == 0) && (!vazioSeZero))
			return FRASE_NOME_DECIMAIS_PLURAL;

		return "";
	}

	private String obtemQualificadorParteInteira(String valor, boolean vazioSeZero) {

		double valTemp = Double.parseDouble(valor);

		if (valTemp > 1)
			return FRASE_NOME_INTEIROS_PLURAL;

		if (valTemp == 1)
			return FRASE_NOME_INTEIROS_SINGULAR;

		if ((valTemp == 0) && (!vazioSeZero))
			return FRASE_NOME_INTEIROS_PLURAL;

		return "";
	}

	private String adicionaSufixoDeGrupoMil(String valor, int nivel) {

		String resultado = "";

		switch (nivel) {

		case 0: // xxx -unidades, dezenas, centenas
			resultado = valor;
			break;

		case 1: // xxx.000 - milhares
			if (valor.equals(CARDINAL_UM))
				resultado = CARDINAL_GRUPOS_SINGULAR[nivel]; // special : remove palavra "um" (um mil)
			else
				resultado += valor + " " + CARDINAL_GRUPOS_PLURAL[nivel];
			break;

		default:

			if (valor.equals(CARDINAL_UM)) {
				if (CARDINAL_GRUPOS_MASCULINO[nivel])
					resultado = CARDINAL_UM;
				else
					resultado = CARDINAL_UMA;
				resultado += " " + CARDINAL_GRUPOS_SINGULAR[nivel];
			} else
				if (valor == CARDINAL_DOIS) {
					if (CARDINAL_GRUPOS_MASCULINO[nivel])
						resultado = CARDINAL_DOIS;
					else
						resultado = CARDINAL_DUAS;
					resultado += " " + CARDINAL_GRUPOS_PLURAL[nivel];
				} else
					resultado = valor + " " + CARDINAL_GRUPOS_PLURAL[nivel];

			break;
		}

		return resultado;
	}

	/////////////////////////

	private boolean validaValor(String valor) {

		if (valor.length() == 0)
			return true;

		char[] array;

		int pontos = 0;
		array = valor.toCharArray();

		for (int x = 0; x < array.length; x++) {

			char chr = array[x];

			if (chr == '-') {
				if (x > 0)
					return false;
				else
					continue;
			}

			if (chr == '.') {
				pontos++;
				continue;
			}

			if (chr < 48 || chr > 57)
				return false;
		}

		if (pontos > 1)
			return false;

		return true;
	}

	private String formataValor(String valor) {

		if (valor.length() == 0)
			return "0.00";

		String resultado = valor;

		int pos = valor.indexOf(".");
		if (pos == -1)
			resultado += ".00";
		else
			if (pos == 0)
				resultado = "0" + resultado;

		pos = resultado.indexOf(".");
		int rlen = resultado.length() - pos;
		if (rlen == 1)
			resultado += "00";
		else
			if (rlen == 2)
				resultado += "0";
			else
				resultado = resultado.substring(0, pos + 3);

		return resultado.trim();
	}

	private boolean valorNegativo(String valor) {

		return (valor.substring(0, 1).compareTo("-") == 0);
	}

}

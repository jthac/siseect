package util;

import java.io.File;

/**
 * 
 * Classe que define o caminho do conjunto de dados que serão testados na
 * aplicação
 * 
 * */

public class Constantes {
	/**
	 * Caminho do conjunto de dados 01 - Dados - Vazios Preenchidos e
	 * Transformados com CS e Web completos sem arquitetura vazio e atributos
	 * desnecessários
	 */
	//public static final String CAMINHO_PADRAO = "F:/Dropbox/Documentos/Doutorado/Tese/Dados/Rótulo Produtividade/Atributos entrada discretos/Dados ajustados/Dados preparados para java/";
	public static final String CAMINHO_PADRAO = new File("").getAbsolutePath()+"/";
	public static boolean IMPRIMIR_MELHOR_ALGORITMO = false;
	public static boolean IMPRIMIR_RESULTADO_INDIVIDUAL = true;
	public static boolean IMPRIMIR_ERRO_EXEMPLO = false;
	public static boolean IMPRIMIR_ESTIMATIVAS_DINAMICAS = false;
	public static final boolean IMPRIMIR_ESTIMATIVAS = false;
	public static boolean BASE_VALIDACAO = false;
	public static final double KNORA = 0.25;
	public static int INDICE_INICIAL = 1;
	public static int INDICE_FINAL = 50;
	public static int QUANTIDADE_CLASSIFICADOR = 0;
	public static int QUANTIDADE_REGRESSOR = 3;
	public static int FOLD = 10;
	public static int TAMANHO_LIMITE_LEAVE_ONE_OUT = 100;
	public static double TAMANHO_TREINAMENTO = 0.5;
	public static double TAMANHO_VALIDACAO = 0.25;
	public static final String MAR = "MAR";
	public static final String MARLOG = "MARLOG";
	public static final String MREAJUSTADO = "MREAJUSTADO";
	public static TipoMetricaAvaliacao TIPO_METRICA_AVALIACAO = null;




}

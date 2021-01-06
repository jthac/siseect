package util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import data.Padrao;

public class Utilidade {

	public static final String ENSEMBLE_1 = "_E1"; 
	public static final String ENSEMBLE_2 = "_E2"; 
	public static final String ENSEMBLE_3 = "_E3"; 
	public static final String ENSEMBLE_4 = "_E4"; 
	public static final String ENSEMBLE_5 = "_E5"; 
	public static final String ENSEMBLE_6 = "_E6"; 
	public static final String ENSEMBLE_7 = "_E7";
	public static boolean ADICIONAR_RESULTADOS_INDIVIDUAIS = true;
	public static String METRICA_AVALIACAO = Constantes.MAR;
	public static ArrayList<String> RESULTADOS_METRICA_MAR = new ArrayList<>();
	public static ArrayList<String> RESULTADOS_METRICA_MARLOG = new ArrayList<>();
	public static ArrayList<String> RESULTADOS_METRICA_MREAJUSTADO = new ArrayList<>();
	public static ArrayList<String> AUXILIAR_METRICA_MAR = new ArrayList<>();
	public static ArrayList<String> AUXILIAR_METRICA_MARLOG = new ArrayList<>();
	public static ArrayList<String> AUXILIAR_METRICA_MREAJUSTADO = new ArrayList<>();
	public static ArrayList<String> MEDIA_CONJUNTO_MAR = new ArrayList<>();
	public static ArrayList<String> MEDIA_CONJUNTO_MARLOG = new ArrayList<>();
	public static ArrayList<String> MEDIA_CONJUNTO_MREAJUSTADO = new ArrayList<>();
	
	public static String getCaminhoTreinamento(String nomeAlgoritmoModelo, Padrao padrao, int i){
		return padrao.toString() + "_" + "CONSTANTE" + "_TREINAMENTO" + "_" + (i) + ".arff";
	}
	public static String getCaminhoTeste(String nomeAlgoritmoModelo, Padrao padrao, int i){
		return padrao.toString() + "_" + "CONSTANTE" + "_TESTE" + "_" + (i) + ".arff";
	}
	public static String getCaminhoTreinamentoClassificacao(Padrao padrao, int quantidadeRegressores, TipoMetricaAvaliacao metricaAvaliacao, int i){
		return padrao.toString() + "_" + "CONSTANTE" + "_" + quantidadeRegressores + "_TREINAMENTO_CLASSIFICADOR" + "_" + metricaAvaliacao.getNome() + "_" + (i) + ".arff";
	}
	public static String getCaminhoTesteClassificacao(String nomeAlgoritmoModelo, int quantidadeRegressores, Padrao padrao, int i){
		return padrao.toString() + "_" + "CONSTANTE" + "_" + quantidadeRegressores + "_TESTE_CLASSIFICADOR" + "_" + (i) + ".arff";
	}
	public static String getCaminhoValidacaoClassificacao(String nomeAlgoritmoModelo, int quantidadeRegressores, Padrao padrao, int i){
		return padrao.toString() + "_" + "CONSTANTE" + "_" + quantidadeRegressores + "_VALIDACAO_CLASSIFICADOR" + "_" + (i) + ".arff";
	}
	public static String getCaminhoValidacao(String nomeAlgoritmoModelo,Padrao padrao, int i) {
		return padrao.toString() + "_" + "CONSTANTE" + "_VALIDACAO" + "_" + (i) + ".arff";
	}
	public static String getCaminhoTreinamentoLeaveOneOut(String nomeAlgoritmoModelo, Padrao padrao, int i){
		return padrao.toString() + "_" + "CONSTANTE" + "_TREINAMENTO_LOO" + "_" + (i) + ".arff";
	}
	public static String getCaminhoValidacaoLeaveOneOut(String nomeAlgoritmoModelo, Padrao padrao, int i){
		return padrao.toString() + "_" + "CONSTANTE" + "_VALIDACAO_LOO" + "_" + (i) + ".arff";
	}
	public static String getCaminhoTesteLeaveOneOut(String nomeAlgoritmoModelo, Padrao padrao, int i){
		return padrao.toString() + "_" + "CONSTANTE" + "_TESTE_LOO" + "_" + (i) + ".arff";
	}
	public static String getCaminhoTreinamentoClassificacaoLeaveOneOut(Padrao padrao, int quantidadeRegressores, TipoMetricaAvaliacao metricaAvaliacao, int i){
		return padrao.toString() + "_" + "CONSTANTE" + "_" + quantidadeRegressores + "_TREINAMENTO_CLASSIFICADOR_LOO" + "_" + metricaAvaliacao.getNome() + "_" + (i) + ".arff";
	}
	public static String getCaminhoTesteClassificacaoLeaveOneOut(String nomeAlgoritmoModelo, int quantidadeRegressores, Padrao padrao, int i){
		return padrao.toString() + "_" + "CONSTANTE" + "_" + quantidadeRegressores + "_TESTE_CLASSIFICADOR_LOO" + "_" + (i) + ".arff";
	}
	public static String getCaminhoValidacaoClassificacaoLeaveOneOut(String nomeAlgoritmoModelo, int quantidadeRegressores, Padrao padrao, int i){
		return padrao.toString() + "_" + "CONSTANTE" + "_" + quantidadeRegressores + "_VALIDACAO_CLASSIFICADOR_LOO" + "_" + (i) + ".arff";
	}

	private static void gerarArquivoErros(String nomeDoMetodo, String metrica, ArrayList<String> resultados) {

		PrintWriter printWriter = null;
		File arquivo = new File(Constantes.CAMINHO_PADRAO + metrica + "_" + nomeDoMetodo + ".txt");

		try {

			/*if(!arquivo.exists()){*/

				printWriter = new PrintWriter(arquivo);
				for(String resultado: resultados){
					printWriter.println(resultado);
				}
				printWriter.close();
			/*}else{

				bfr = Files.newBufferedReader(path, charset);
				ArrayList<String> resultados = new ArrayList<>();
				while(true){
					if(linha != null){
						linha += bfr.readLine()+"\n";
						resultados.add(linha);
					}else{
						break;
					}
				}
				printWriter = new PrintWriter(arquivo);
				for(String resultado: resultados){
					printWriter.println(resultado);
				}
				for(String resultado: texto){
					printWriter.println(resultado);
				}
				
			}*/
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		

	}
	
	public static void gerarArquivosMassa(long tempo, String metodo){

		Utilidade.RESULTADOS_METRICA_MAR.add("\n" + "Tempo: " + tempo); 
		Utilidade.RESULTADOS_METRICA_MARLOG.add("\n" + "Tempo: " + tempo); 
		Utilidade.RESULTADOS_METRICA_MREAJUSTADO.add("\n" + "Tempo: " + tempo); 
		adicionaLinhaArquivo();
		RESULTADOS_METRICA_MAR.addAll(MEDIA_CONJUNTO_MAR);
		RESULTADOS_METRICA_MARLOG.addAll(MEDIA_CONJUNTO_MARLOG);
		RESULTADOS_METRICA_MREAJUSTADO.addAll(MEDIA_CONJUNTO_MREAJUSTADO);
		Utilidade.gerarArquivoErros(metodo, Constantes.MAR, Utilidade.RESULTADOS_METRICA_MAR);
		Utilidade.gerarArquivoErros(metodo, Constantes.MARLOG, Utilidade.RESULTADOS_METRICA_MARLOG);
		Utilidade.gerarArquivoErros(metodo, Constantes.MREAJUSTADO, Utilidade.RESULTADOS_METRICA_MREAJUSTADO);
		
	}
	
	public static void adicionaCabecalhoDados(Padrao padrao) {
		Utilidade.RESULTADOS_METRICA_MAR.add(padrao.toString() + "\n");
		Utilidade.RESULTADOS_METRICA_MARLOG.add(padrao.toString() + "\n");
		Utilidade.RESULTADOS_METRICA_MREAJUSTADO.add(padrao.toString() + "\n");
	}
	
	public static void adicionaLinhaArquivo() {
		Utilidade.RESULTADOS_METRICA_MAR.add("\n");
		Utilidade.RESULTADOS_METRICA_MARLOG.add("\n");
		Utilidade.RESULTADOS_METRICA_MREAJUSTADO.add("\n");
	}
	public static String calcularMediaLista(ArrayList<String> lista) {

		// TODO Auto-generated method stub
		double somaDosValores = 0; 
		
		for(String valor: lista){
			somaDosValores += Double.parseDouble(valor.replace(",", "."));
		}
			
		
		return ("" + somaDosValores / lista.size()).replace(".", ",");
	}

	public static void calculaMediasMetricas() {
		
		Utilidade.MEDIA_CONJUNTO_MAR.add(Utilidade.calcularMediaLista(Utilidade.AUXILIAR_METRICA_MAR));
		Utilidade.MEDIA_CONJUNTO_MARLOG.add(Utilidade.calcularMediaLista(Utilidade.AUXILIAR_METRICA_MARLOG));
		Utilidade.MEDIA_CONJUNTO_MREAJUSTADO.add(Utilidade.calcularMediaLista(Utilidade.AUXILIAR_METRICA_MREAJUSTADO));
	}

	public static void inicializaListasMetricas() {
		
		Utilidade.RESULTADOS_METRICA_MAR = new ArrayList<String>();
		Utilidade.RESULTADOS_METRICA_MARLOG = new ArrayList<String>();
		Utilidade.RESULTADOS_METRICA_MREAJUSTADO = new ArrayList<String>();
		Utilidade.MEDIA_CONJUNTO_MAR = new ArrayList<String>();
		Utilidade.MEDIA_CONJUNTO_MARLOG = new ArrayList<String>();
		Utilidade.MEDIA_CONJUNTO_MREAJUSTADO = new ArrayList<String>();
		
		
	}
	public static void zerarMediasAuxiliares() {
		// TODO Auto-generated method stub
		AUXILIAR_METRICA_MAR = new ArrayList<>();
		AUXILIAR_METRICA_MARLOG = new ArrayList<>();
		AUXILIAR_METRICA_MREAJUSTADO = new ArrayList<>();
	
	}


}

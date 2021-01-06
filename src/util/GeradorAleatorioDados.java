package util;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.Projeto;
import weka.core.Instance;
import weka.core.Instances;
import data.Padrao;

public class GeradorAleatorioDados {
	
	List<Projeto> listaProjetos;
	Padrao padrao;
	String nomeAlgoritmoModelo;

	public GeradorAleatorioDados(String nomeAlgoritmoModelo, Padrao padrao) {

	}

	
	public static void gerarTreinamentoTeste(String nomeAlgoritmoModelo, Padrao padrao){
		try {

			Path path = Paths.get(Constantes.CAMINHO_PADRAO);

			String nomeArquivo = padrao.getNomeArquivo();
			String caminhoArquivo = Constantes.CAMINHO_PADRAO + nomeArquivo;
			FileReader reader = new FileReader(caminhoArquivo);
			Instances instancias = new Instances(reader);

			Charset charset = StandardCharsets.UTF_8;
			BufferedWriter bfw = null;

			Instance instancia;
			List<String> linhas = new ArrayList<String>();

			for (int i = 0; i < instancias.numInstances(); i++) {

				instancia = instancias.instance(i);
				linhas.add(instancia.toString().replace(",", "\t"));
			}

			List<String> linhasTreinamento = new ArrayList<String>();
			List<String> linhasTeste = new ArrayList<String>();
			int tamanhoTreinamento = (int) (linhas.size() * Constantes.TAMANHO_TREINAMENTO);

			for (int i = Constantes.INDICE_INICIAL; i <= Constantes.INDICE_FINAL; i++) {

				Collections.shuffle(linhas);
				linhasTreinamento = new ArrayList<String>();
				linhasTeste = new ArrayList<String>();

				for (int j = 0; j < linhas.size(); j++) {

					if (j < tamanhoTreinamento) {
						linhasTreinamento.add(linhas.get(j));
					}

					if (j >= tamanhoTreinamento) {
						linhasTeste.add(linhas.get(j));
					}
				}

				path = Paths.get(Constantes.CAMINHO_PADRAO + 
						Utilidade.getCaminhoTreinamento(nomeAlgoritmoModelo, padrao, i));
				bfw = Files.newBufferedWriter(path, charset);
				bfw.write(padrao.getCabecalho());
				bfw.newLine();

				for (int k = 0; k < linhasTreinamento.size(); k++) {
					bfw.write(linhasTreinamento.get(k));
					bfw.newLine();
				}
				bfw.flush();

				path = Paths.get(Constantes.CAMINHO_PADRAO + 
						Utilidade.getCaminhoTeste(nomeAlgoritmoModelo, padrao, i));
				bfw = Files.newBufferedWriter(path, charset);
				bfw.write(padrao.getCabecalho());
				bfw.newLine();

				for (int l = 0; l < linhasTeste.size(); l++) {
					bfw.write(linhasTeste.get(l));
					bfw.newLine();
				}
				bfw.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void gerarTreinamentoValidacaoTeste(String nomeAlgoritmoModelo, Padrao padrao){
		try {

			Path path = Paths.get(Constantes.CAMINHO_PADRAO);

			String nomeArquivo = padrao.getNomeArquivo();
			String caminhoArquivo = Constantes.CAMINHO_PADRAO + nomeArquivo;
			FileReader reader = new FileReader(caminhoArquivo);
			Instances instancias = new Instances(reader);

			Charset charset = StandardCharsets.UTF_8;
			BufferedWriter bfw = null;

			Instance instancia;
			List<String> linhas = new ArrayList<String>();

			for (int i = 0; i < instancias.numInstances(); i++) {

				instancia = instancias.instance(i);
				linhas.add(instancia.toString().replace(",", "\t"));
			}

			List<String> linhasTreinamento = new ArrayList<String>();
			List<String> linhasValidacao = new ArrayList<String>();
			List<String> linhasTeste = new ArrayList<String>();
			int tamanhoTreinamento = (int) (linhas.size() * Constantes.TAMANHO_TREINAMENTO);
			int tamanhoValidacao = (int) (linhas.size() * Constantes.TAMANHO_VALIDACAO);

			for (int i = Constantes.INDICE_INICIAL; i <= Constantes.INDICE_FINAL; i++) {

				Collections.shuffle(linhas);
				linhasTreinamento = new ArrayList<String>();
				linhasValidacao = new ArrayList<String>();
				linhasTeste = new ArrayList<String>();

				for (int j = 0; j < linhas.size(); j++) {

					if (j < tamanhoTreinamento) {
						linhasTreinamento.add(linhas.get(j));
					}

					if (j >= tamanhoTreinamento) {
						
						if(j < (tamanhoTreinamento + tamanhoValidacao)){
							linhasValidacao.add(linhas.get(j));
						}else{
							linhasTeste.add(linhas.get(j));
						}
					}
				}

				path = Paths.get(Constantes.CAMINHO_PADRAO + 
						Utilidade.getCaminhoTreinamento(nomeAlgoritmoModelo, padrao, i));
				bfw = Files.newBufferedWriter(path, charset);
				bfw.write(padrao.getCabecalho());
				bfw.newLine();

				for (int k = 0; k < linhasTreinamento.size(); k++) {
					bfw.write(linhasTreinamento.get(k));
					bfw.newLine();
				}
				bfw.flush();

				path = Paths.get(Constantes.CAMINHO_PADRAO + 
						Utilidade.getCaminhoValidacao(nomeAlgoritmoModelo, padrao, i));
				bfw = Files.newBufferedWriter(path, charset);
				bfw.write(padrao.getCabecalho());
				bfw.newLine();

				for (int l = 0; l < linhasValidacao.size(); l++) {
					bfw.write(linhasValidacao.get(l));
					bfw.newLine();
				}
				bfw.flush();

				path = Paths.get(Constantes.CAMINHO_PADRAO + 
						Utilidade.getCaminhoTeste(nomeAlgoritmoModelo, padrao, i));
				bfw = Files.newBufferedWriter(path, charset);
				bfw.write(padrao.getCabecalho());
				bfw.newLine();

				for (int l = 0; l < linhasTeste.size(); l++) {
					bfw.write(linhasTeste.get(l));
					bfw.newLine();
				}
				bfw.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void gerarTreinamentoTesteLeaveOneOut(String nomeAlgoritmoModelo, Padrao padrao){
		try {

			Path path = Paths.get(Constantes.CAMINHO_PADRAO);

			String nomeArquivo = padrao.getNomeArquivo();
			String caminhoArquivo = Constantes.CAMINHO_PADRAO + nomeArquivo;
			FileReader reader = new FileReader(caminhoArquivo);
			Instances instancias = new Instances(reader);

			Charset charset = StandardCharsets.UTF_8;
			BufferedWriter bfw = null;

			Instance instancia;
			List<String> linhas = new ArrayList<String>();

			for (int i = 0; i < instancias.numInstances(); i++) {

				instancia = instancias.instance(i);
				linhas.add(instancia.toString().replace(",", "\t"));
			}

			List<String> linhasTreinamento = new ArrayList<String>();
			List<String> linhasTeste = new ArrayList<String>();
			
			int leaveOneOut = 0;
			
			for (int i = Constantes.INDICE_INICIAL; i <= linhas.size(); i++) {

				linhasTreinamento = new ArrayList<String>();
				linhasTeste = new ArrayList<String>();
				

				for (int j = 0; j < linhas.size(); j++) {

					if (j != leaveOneOut) {
						linhasTreinamento.add(linhas.get(j));
					}

					if (j == leaveOneOut) {
						linhasTeste.add(linhas.get(leaveOneOut));
					}
				}
				
				leaveOneOut++;

				path = Paths.get(Constantes.CAMINHO_PADRAO + 
						Utilidade.getCaminhoTreinamentoLeaveOneOut(nomeAlgoritmoModelo, padrao, i));
				bfw = Files.newBufferedWriter(path, charset);
				bfw.write(padrao.getCabecalho());
				bfw.newLine();

				for (int k = 0; k < linhasTreinamento.size(); k++) {
					bfw.write(linhasTreinamento.get(k));
					bfw.newLine();
				}
				bfw.flush();

				path = Paths.get(Constantes.CAMINHO_PADRAO + 
						Utilidade.getCaminhoTesteLeaveOneOut(nomeAlgoritmoModelo, padrao, i));
				bfw = Files.newBufferedWriter(path, charset);
				bfw.write(padrao.getCabecalho());
				bfw.newLine();

				for (int l = 0; l < linhasTeste.size(); l++) {
					bfw.write(linhasTeste.get(l));
					bfw.newLine();
				}
				bfw.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void gerarTreinamentoValidacaoTesteLeaveOneOut(String nomeAlgoritmoModelo, Padrao padrao){
		try {

			Path path = Paths.get(Constantes.CAMINHO_PADRAO);

			String nomeArquivo = padrao.getNomeArquivo();
			String caminhoArquivo = Constantes.CAMINHO_PADRAO + nomeArquivo;
			FileReader reader = new FileReader(caminhoArquivo);
			Instances instancias = new Instances(reader);

			Charset charset = StandardCharsets.UTF_8;
			BufferedWriter bfw = null;

			Instance instancia;
			List<String> linhas = new ArrayList<String>();
			nomeAlgoritmoModelo = "CONSTANTE";
			
			for (int i = 0; i < instancias.numInstances(); i++) {

				instancia = instancias.instance(i);
				linhas.add(instancia.toString().replace(",", "\t"));
			}

			List<String> linhasTreinamento = new ArrayList<String>();
			List<String> linhasValidacao = new ArrayList<String>();
			List<String> linhasTeste = new ArrayList<String>();
			
			int leaveOneOut = 0;
			int leaveTwoOut = 1;
			boolean entrou = false;
			for (int i = Constantes.INDICE_INICIAL; i <= linhas.size(); i++) {

				linhasTreinamento = new ArrayList<String>();
				linhasValidacao = new ArrayList<String>();
				linhasTeste = new ArrayList<String>();
				
				
				for (int j = 0; j < linhas.size(); j++) {

					if (j != leaveOneOut && j!=leaveTwoOut) {
						linhasTreinamento.add(linhas.get(j));
					}
					
					if (j == leaveOneOut) {
						linhasValidacao.add(linhas.get(leaveOneOut));
					}

					if (j == leaveTwoOut || (leaveTwoOut == (linhas.size()) && !entrou)) {
						if(j == leaveTwoOut){
							linhasTeste.add(linhas.get(leaveTwoOut));
						}
						if(leaveTwoOut == (linhas.size())){
							linhasTeste.add(linhas.get(0));
							entrou = true;
						}
					}
				}
				
				leaveOneOut++;
				leaveTwoOut++;

				path = Paths.get(Constantes.CAMINHO_PADRAO + 
						Utilidade.getCaminhoTreinamentoLeaveOneOut(nomeAlgoritmoModelo, padrao, i));
				bfw = Files.newBufferedWriter(path, charset);
				bfw.write(padrao.getCabecalho());
				bfw.newLine();

				for (int k = 0; k < linhasTreinamento.size(); k++) {
					bfw.write(linhasTreinamento.get(k));
					bfw.newLine();
				}
				bfw.flush();

				path = Paths.get(Constantes.CAMINHO_PADRAO + 
						Utilidade.getCaminhoValidacaoLeaveOneOut(nomeAlgoritmoModelo, padrao, i));
				bfw = Files.newBufferedWriter(path, charset);
				bfw.write(padrao.getCabecalho());
				bfw.newLine();

				for (int l = 0; l < linhasValidacao.size(); l++) {
					bfw.write(linhasValidacao.get(l));
					bfw.newLine();
				}
				bfw.flush();

				path = Paths.get(Constantes.CAMINHO_PADRAO + 
						Utilidade.getCaminhoTesteLeaveOneOut(nomeAlgoritmoModelo, padrao, i));
				bfw = Files.newBufferedWriter(path, charset);
				bfw.write(padrao.getCabecalho());
				bfw.newLine();

				for (int l = 0; l < linhasTeste.size(); l++) {
					bfw.write(linhasTeste.get(l));
					bfw.newLine();
				}
				bfw.flush();
}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void gerarTreinamentoClassificador(List<Projeto> listaProjetos, String nomeAlgoritmoModelo, Padrao padrao, int i) {
		
	try {

			
			Path path = Paths.get(Constantes.CAMINHO_PADRAO + 
					Utilidade.getCaminhoTreinamentoClassificacao(padrao, Constantes.QUANTIDADE_REGRESSOR, Constantes.TIPO_METRICA_AVALIACAO, i));
			Charset charset = StandardCharsets.UTF_8;
			BufferedWriter bfw = null;
	

			List<String> linhas = new ArrayList<String>();
			for(Projeto projeto:listaProjetos){
				linhas.add(padrao.getLinhaClassificacao(projeto, true));
			}
	

			bfw = Files.newBufferedWriter(path, charset);
			bfw.write(padrao.getCabecalhoClassificacao());
			bfw.newLine();
	
			for (int l = 0; l < linhas.size(); l++) {
				bfw.write(linhas.get(l));
				bfw.newLine();
			}
			bfw.flush();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}


	public static void gerarTesteClassificador(List<Projeto> listaProjetos, String nomeAlgoritmoModelo, Padrao padrao, int i) {
		
	try {

			
			Path path = Paths.get(Constantes.CAMINHO_PADRAO + 
					Utilidade.getCaminhoTesteClassificacao(nomeAlgoritmoModelo, Constantes.QUANTIDADE_REGRESSOR, padrao, i));
			Charset charset = StandardCharsets.UTF_8;
			BufferedWriter bfw = null;
	

			List<String> linhas = new ArrayList<String>();
			for(Projeto projeto:listaProjetos){
				linhas.add(padrao.getLinhaClassificacao(projeto, false));
			}
	

			bfw = Files.newBufferedWriter(path, charset);
			bfw.write(padrao.getCabecalhoClassificacao());
			bfw.newLine();
	
			for (int l = 0; l < linhas.size(); l++) {
				bfw.write(linhas.get(l));
				bfw.newLine();
			}
			bfw.flush();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}

	public static void gerarValidacaoClassificador(List<Projeto> listaProjetos, String nomeAlgoritmoModelo, Padrao padrao, int i) {
		
	try {

			
			Path path = Paths.get(Constantes.CAMINHO_PADRAO + 
					Utilidade.getCaminhoValidacaoClassificacao(nomeAlgoritmoModelo, Constantes.QUANTIDADE_REGRESSOR, padrao, i));
			Charset charset = StandardCharsets.UTF_8;
			BufferedWriter bfw = null;
	

			List<String> linhas = new ArrayList<String>();
			for(Projeto projeto:listaProjetos){
				linhas.add(padrao.getLinhaClassificacao(projeto, false));
			}
	

			bfw = Files.newBufferedWriter(path, charset);
			bfw.write(padrao.getCabecalhoClassificacao());
			bfw.newLine();
	
			for (int l = 0; l < linhas.size(); l++) {
				bfw.write(linhas.get(l));
				bfw.newLine();
			}
			bfw.flush();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}

	public static void gerarTreinamentoClassificadorLeaveOneOut(List<Projeto> listaProjetos, String nomeAlgoritmoModelo, Padrao padrao, int i) {
		
	try {

			
			Path path = Paths.get(Constantes.CAMINHO_PADRAO + 
					Utilidade.getCaminhoTreinamentoClassificacaoLeaveOneOut(padrao, Constantes.QUANTIDADE_REGRESSOR,  Constantes.TIPO_METRICA_AVALIACAO, i));
			Charset charset = StandardCharsets.UTF_8;
			BufferedWriter bfw = null;
	

			List<String> linhas = new ArrayList<String>();
			for(Projeto projeto:listaProjetos){
				linhas.add(padrao.getLinhaClassificacao(projeto, true));
			}
	

			bfw = Files.newBufferedWriter(path, charset);
			bfw.write(padrao.getCabecalhoClassificacao());
			bfw.newLine();
	
			for (int l = 0; l < linhas.size(); l++) {
				bfw.write(linhas.get(l));
				bfw.newLine();
			}
			bfw.flush();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}

	public static void gerarTesteClassificadorLeaveOneOut(List<Projeto> listaProjetos, String nomeAlgoritmoModelo, Padrao padrao, int i) {
		
	try {

			
			Path path = Paths.get(Constantes.CAMINHO_PADRAO + 
					Utilidade.getCaminhoTesteClassificacaoLeaveOneOut(nomeAlgoritmoModelo, Constantes.QUANTIDADE_REGRESSOR, padrao, i));
			Charset charset = StandardCharsets.UTF_8;
			BufferedWriter bfw = null;
	

			List<String> linhas = new ArrayList<String>();
			for(Projeto projeto:listaProjetos){
				linhas.add(padrao.getLinhaClassificacao(projeto, false));
			}
	

			bfw = Files.newBufferedWriter(path, charset);
			bfw.write(padrao.getCabecalhoClassificacao());
			bfw.newLine();
	
			for (int l = 0; l < linhas.size(); l++) {
				bfw.write(linhas.get(l));
				bfw.newLine();
			}
			bfw.flush();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}

	public static void gerarValidacaoClassificadorLeaveOneOut(List<Projeto> listaProjetos, String nomeAlgoritmoModelo, Padrao padrao, int i) {
		
	try {

			
			Path path = Paths.get(Constantes.CAMINHO_PADRAO + 
					Utilidade.getCaminhoValidacaoClassificacaoLeaveOneOut(nomeAlgoritmoModelo, Constantes.QUANTIDADE_REGRESSOR, padrao, i));
			Charset charset = StandardCharsets.UTF_8;
			BufferedWriter bfw = null;
	

			List<String> linhas = new ArrayList<String>();
			for(Projeto projeto:listaProjetos){
				linhas.add(padrao.getLinhaClassificacao(projeto, false));
			}
	

			bfw = Files.newBufferedWriter(path, charset);
			bfw.write(padrao.getCabecalhoClassificacao());
			bfw.newLine();
	
			for (int l = 0; l < linhas.size(); l++) {
				bfw.write(linhas.get(l));
				bfw.newLine();
			}
			bfw.flush();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}



}

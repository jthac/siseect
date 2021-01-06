package method;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import main.TesteEnsembleEstatico;
import model.Resultado;
import util.Constantes;
import util.GeradorAleatorioDados;
import util.TipoMetodoCombinacao;
import util.TipoValidacao;
import util.Utilidade;
import weka.classifiers.Classifier;
import weka.core.Instance;
import data.Padrao;

public class EnsembleEstaticoGeral extends EnsembleEstaticoTecnica {

	private Classifier regressor1;
	private Classifier regressor2;
	private Classifier regressor3;
	private Classifier regressor4;
	private Classifier regressor5;
	
	public EnsembleEstaticoGeral(Classifier regressor1, Classifier regressor2, Classifier regressor3, Classifier regressor4, Classifier regressor5) {
		this.regressor1 = regressor1;
		this.regressor2 = regressor2;
		this.regressor3 = regressor3;
		this.regressor4 = regressor4;
		this.regressor5 = regressor5;

	}

	private void avaliarMetodo(Padrao padrao, Resultado resultado1,
			Resultado resultado2, Resultado resultado3, Resultado resultado4, Resultado resultado5, TipoMetodoCombinacao ensembleEstatico, TipoValidacao tipoValidacao, int i) throws Exception {

		if(tipoValidacao == TipoValidacao.HOLD_OUT){
			if(Constantes.BASE_VALIDACAO){
				// carrega os dados de validação nas instancias principais
				configurarDados(Utilidade.getCaminhoValidacao("CONSTANTE", padrao, i));
			}else{
				// carrega os dados de validação nas instancias principais
				configurarDados(Utilidade.getCaminhoTeste("CONSTANTE", padrao, i));
				
			}
		}

		if(tipoValidacao == TipoValidacao.LEAVE_ONE_OUT){
			
			if(Constantes.BASE_VALIDACAO){
				// carrega os dados de validação nas instancias principais
				configurarDados(Utilidade.getCaminhoValidacaoLeaveOneOut("CONSTANTE", padrao, i));
			}else{
				// carrega os dados de validação nas instancias principais
				configurarDados(Utilidade.getCaminhoTesteLeaveOneOut("CONSTANTE", padrao, i));
			}
		}

		// calcula as estimativas combinadas utilizando os resultados de KNN e
		// os dados de classificação para o melhor algoritimo
		calcularEstimativas(padrao, resultado1, resultado2, resultado3, resultado4, resultado5, ensembleEstatico);


		// imprimi os resultados em tela
		calcularResultadosGerais();
		//imprimirResultadosMdMRE();
	}


	private void calcularEstimativas(Padrao padrao, Resultado resultado1,
			Resultado resultado2, Resultado resultado3, Resultado resultado4, Resultado resultado5, TipoMetodoCombinacao ensembleEstatico) throws Exception {

		Double valorEstimado = null;
		Double valorEstimado1 = null;
		Double valorEstimado2 = null;
		Double valorEstimado3 = null;
		Double valorEstimado4 = null;
		Double valorEstimado5 = null;
		Double valorReal;
		Double erroAbsoluto, marlog, mreAjustado;
		Instance instancia;

		Classifier regressor1 = null;
		Classifier regressor2 = null;
		Classifier regressor3 = null;
		Classifier regressor4 = null;
		Classifier regressor5 = null;
		
		resultado = new Resultado();
		resultado.setNomeModelo(toString() + " - " + padrao.toString());

		if (resultado1 != null){
			// obtem o modelo
			ObjectInputStream objectInputStream1 = new ObjectInputStream(new FileInputStream(
					resultado1.getNomeModelo()));
			regressor1 = (Classifier) objectInputStream1.readObject();
			objectInputStream1.close();
		}
		
		if(resultado2 != null){
			// obtem o modelo
			ObjectInputStream objectInputStream2 = new ObjectInputStream(new FileInputStream(
					resultado2.getNomeModelo()));
			regressor2 = (Classifier) objectInputStream2.readObject();
			objectInputStream2.close();
		}
		
		if(resultado3 != null){
			// obtem o modelo
			ObjectInputStream objectInputStream3 = new ObjectInputStream(new FileInputStream(
					resultado3.getNomeModelo()));
			regressor3 = (Classifier) objectInputStream3.readObject();
			objectInputStream3.close();
		}
		
		if(resultado4 != null){
			// obtem o modelo
			ObjectInputStream objectInputStream4 = new ObjectInputStream(new FileInputStream(
					resultado4.getNomeModelo()));
			regressor4 = (Classifier) objectInputStream4.readObject();
			objectInputStream4.close();
		}

		if(resultado5 != null){
			// obtem o modelo
			ObjectInputStream objectInputStream5 = new ObjectInputStream(new FileInputStream(
					resultado5.getNomeModelo()));
			regressor5 = (Classifier) objectInputStream5.readObject();
			objectInputStream5.close();
		}
		
		// calcular a distância de cada elemento da lista de projetos validação
		// para a lista de projetos avaliados
		for (int i = 0; i < instancias.numInstances(); i++) {

			// pega cada instancia do conjunto de validação com rotulo de
			// esforço
			instancia = instancias.instance(i);
			
			if(regressor1 != null){
				// Classificamos esta instância com o algoritimo relativo ao valor
				valorEstimado1 = (double) Math.abs(regressor1.classifyInstance(instancia));
			}
			
			if(regressor2 != null){
				// Classificamos esta instância com o algoritimo relativo ao valor
				valorEstimado2 = (double) Math.abs(regressor2.classifyInstance(instancia));
			}

			if(regressor3 != null){
				// Classificamos esta instância com o algoritimo relativo ao valor
				valorEstimado3 = (double) Math.abs(regressor3.classifyInstance(instancia));
			}
			
			if(regressor4 != null){
				// Classificamos esta instância com o algoritimo relativo ao valor
				valorEstimado4 = (double) Math.abs(regressor4.classifyInstance(instancia));
			}

			if(regressor5 != null){
				// Classificamos esta instância com o algoritimo relativo ao valor
				valorEstimado5 = (double) Math.abs(regressor5.classifyInstance(instancia));
			}

			
			List<Double> valoresEstimados = new ArrayList<Double>();
			if(valorEstimado1 != null){
				valoresEstimados.add(valorEstimado1);
			}
			if(valorEstimado2 != null){
				valoresEstimados.add(valorEstimado2);
			}
			if(valorEstimado3 != null){
				valoresEstimados.add(valorEstimado3);
			}
			if(valorEstimado4 != null){
				valoresEstimados.add(valorEstimado4);
			}
			if(valorEstimado5 != null){
				valoresEstimados.add(valorEstimado5);
			}

			if(TipoMetodoCombinacao.MEDIA == ensembleEstatico){
				valorEstimado = calcularMediaLista(valoresEstimados);
			}

			if(TipoMetodoCombinacao.MEDIANA == ensembleEstatico){
				valorEstimado = calcularMedianaLista(valoresEstimados);
			}

			if (TipoMetodoCombinacao.MODA == ensembleEstatico) {
				valorEstimado = calcularModaLista(valoresEstimados);
			}

			if(TipoMetodoCombinacao.MINIMO == ensembleEstatico){
				valorEstimado = calcularMenorValor(valoresEstimados);
			}
			
			if(TipoMetodoCombinacao.MAXIMO == ensembleEstatico){
				valorEstimado = calcularMaiorValor(valoresEstimados);
			}
			
			if(TipoMetodoCombinacao.MEDIA_DAS_PONTAS == ensembleEstatico){
				valorEstimado = calcularMediaPontas(valoresEstimados);
			}

	

			
			
			// recupera o valor real do esforço
			valorReal = (double) instancia.classValue();

			// calcula o erro absoluto
			erroAbsoluto = Math.abs(valorReal - (valorEstimado));

			marlog = Math.log10(erroAbsoluto);
					

			// calcula o mreajustado
			mreAjustado = ((erroAbsoluto / valorReal) + (erroAbsoluto / valorEstimado)/2);

			// adiciona o erro a lista de erros absolutos
			resultado.getErrosAbsolutos().add(erroAbsoluto);


			
			resultado.getMarlogs().add(marlog);
			resultado.getMresAjustados().add(mreAjustado);

		}
	}


	@Override
	public void run(Padrao padrao, TipoMetodoCombinacao ensembleEstatico, TipoValidacao tipoValidacao) {

		RegressorIndividualGeral regressorIndividualGeral1 = null;
		RegressorIndividualGeral regressorIndividualGeral2 = null;
		RegressorIndividualGeral regressorIndividualGeral3 = null;
		RegressorIndividualGeral regressorIndividualGeral4 = null;
		RegressorIndividualGeral regressorIndividualGeral5 = null;
		
		Resultado resultado1 = null;
		Resultado resultado2 = null;
		Resultado resultado3 = null;
		Resultado resultado4 = null;
		Resultado resultado5 = null;
		
		try {
			
			Utilidade.adicionaCabecalhoDados(padrao);

			if(tipoValidacao == TipoValidacao.HOLD_OUT){
			
				File arquivo = null;
				arquivo = new File(Constantes.CAMINHO_PADRAO + Utilidade.getCaminhoTreinamento("CONSTANTE", padrao, 1));
				
				if(!arquivo.exists()){
					GeradorAleatorioDados.gerarTreinamentoValidacaoTeste("CONSTANTE", padrao);
				}
	
				for (int i = 1; i <= 50; i++) {
	
				if(regressor1 != null){
					// treina o regressor1
					regressorIndividualGeral1 = new RegressorIndividualGeral(regressor1);
					regressorIndividualGeral1.criarModelo(Utilidade.getCaminhoTreinamento("CONSTANTE", padrao, i));
					resultado1 = regressorIndividualGeral1.getResultado();
				}
				
				if(regressor2 != null){
					// treina o regressor2
					regressorIndividualGeral2 = new RegressorIndividualGeral(regressor2);
					regressorIndividualGeral2.criarModelo(Utilidade.getCaminhoTreinamento("CONSTANTE", padrao, i));
					resultado2 = regressorIndividualGeral2.getResultado();
				}
				
				if(regressor3 != null){
					// treina o regressor3
					regressorIndividualGeral3 = new RegressorIndividualGeral(regressor3);
					regressorIndividualGeral3.criarModelo(Utilidade.getCaminhoTreinamento("CONSTANTE", padrao, i));
					resultado3 = regressorIndividualGeral3.getResultado();
				}
				if(regressor4 != null){
					// treina o regressor4
					regressorIndividualGeral4 = new RegressorIndividualGeral(regressor4);
					regressorIndividualGeral4.criarModelo(Utilidade.getCaminhoTreinamento("CONSTANTE", padrao, i));
					resultado4 = regressorIndividualGeral4.getResultado();
				}
	
				if(regressor5 != null){
					// treina o regressor5
					regressorIndividualGeral5 = new RegressorIndividualGeral(regressor5);
					regressorIndividualGeral5.criarModelo(Utilidade.getCaminhoTreinamento("CONSTANTE", padrao, i));
					resultado5 = regressorIndividualGeral5.getResultado();
				}

				
				// carrega a lista de projetos que foram testados
				//			setListaProjetos(Padrao.converteInstanciasParaProjetos(padrao,
				//					null));
	
				avaliarMetodo(padrao, resultado1, resultado2, resultado3, resultado4, resultado5, ensembleEstatico, tipoValidacao, i);
				
				}
			}
			
			if(tipoValidacao == TipoValidacao.LEAVE_ONE_OUT){
				
				File arquivo = null;
				arquivo = new File(Constantes.CAMINHO_PADRAO + Utilidade.getCaminhoTreinamentoLeaveOneOut("CONSTANTE", padrao, 1));
				
				if(!arquivo.exists()){
					GeradorAleatorioDados.gerarTreinamentoValidacaoTesteLeaveOneOut("CONSTANTE", padrao);
				}
	
				for (int i = 1; i <= padrao.getTamanhoBase(); i++) {
	
					if(regressor1 != null){
						// treina o regressor1
						regressorIndividualGeral1 = new RegressorIndividualGeral(regressor1);
						regressorIndividualGeral1.criarModelo(Utilidade.getCaminhoTreinamentoLeaveOneOut("CONSTANTE", padrao, i));
						resultado1 = regressorIndividualGeral1.getResultado();
					}
					
					if(regressor2 != null){
						// treina o regressor2
						regressorIndividualGeral2 = new RegressorIndividualGeral(regressor2);
						regressorIndividualGeral2.criarModelo(Utilidade.getCaminhoTreinamentoLeaveOneOut("CONSTANTE", padrao, i));
						resultado2 = regressorIndividualGeral2.getResultado();
					}
					
					if(regressor3 != null){
						// treina o regressor3
						regressorIndividualGeral3 = new RegressorIndividualGeral(regressor3);
						regressorIndividualGeral3.criarModelo(Utilidade.getCaminhoTreinamentoLeaveOneOut("CONSTANTE", padrao, i));
						resultado3 = regressorIndividualGeral3.getResultado();
					}
					if(regressor4 != null){
						// treina o regressor4
						regressorIndividualGeral4 = new RegressorIndividualGeral(regressor4);
						regressorIndividualGeral4.criarModelo(Utilidade.getCaminhoTreinamentoLeaveOneOut("CONSTANTE", padrao, i));
						resultado4 = regressorIndividualGeral4.getResultado();
					}
		
					if(regressor5 != null){
						// treina o regressor5
						regressorIndividualGeral5 = new RegressorIndividualGeral(regressor5);
						regressorIndividualGeral5.criarModelo(Utilidade.getCaminhoTreinamentoLeaveOneOut("CONSTANTE", padrao, i));
						resultado5 = regressorIndividualGeral5.getResultado();
					}

					
					// carrega a lista de projetos que foram testados
					//			setListaProjetos(Padrao.converteInstanciasParaProjetos(padrao,
					//					null));
		
					avaliarMetodo(padrao, resultado1, resultado2, resultado3, resultado4, resultado5, ensembleEstatico, tipoValidacao, i);
					
					}
			}
			
			Utilidade.calculaMediasMetricas();
			Utilidade.zerarMediasAuxiliares();
			Utilidade.adicionaLinhaArquivo();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	

	@Override
	public String toString() {
		return "EnsembleEstatico" + TesteEnsembleEstatico.experimento;
	}

	@Override
	public void run(Padrao padrao, TipoValidacao tipoValidacao) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run(Padrao padrao, TipoMetodoCombinacao ensembleEstatico) {
		// TODO Auto-generated method stub
		
	}
}

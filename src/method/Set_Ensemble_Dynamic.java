package method;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import model.Resultado;
import model.ResultadoIdeal;
import util.Constantes;
import util.GeradorAleatorioDados;
import util.TipoMetodoCombinacao;
import util.TipoValidacao;
import util.Utilidade;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instance;
import data.Padrao;

public class Set_Ensemble_Dynamic extends SetEnsembleDynamicTecnica {
	
	private static final String REGRESSOR1 = "REGRESSOR1";
	private static final String REGRESSOR2 = "REGRESSOR2";
	private static final String REGRESSOR3 = "REGRESSOR3";
	
	private Classifier regressor1;
	private Classifier regressor2;
	private Classifier regressor3;

	public Set_Ensemble_Dynamic(Classifier regressor1, Classifier regressor2, Classifier regressor3) {
		this.regressor1 = regressor1;
		this.regressor2 = regressor2;
		this.regressor3 = regressor3;
	}
	
	private void avaliarMetodo(Padrao padrao, Resultado resultadoRegressor1, Resultado resultadoRegressor2,
			Resultado resultadoRegressor3, Integer tipoClassificador1, Integer tipoClassificador2, Integer tipoClassificador3, Integer tipoClassificador4, Integer tipoClassificador5, Integer tipoClassificador6, Integer tipoClassificador7, TipoMetodoCombinacao tipoSelecaoDinamica, TipoValidacao tipoValidacao, int i) throws Exception{
		
		if(tipoValidacao == TipoValidacao.HOLD_OUT){
			if(Constantes.BASE_VALIDACAO == true){
				// carrega os dados de validação nas instancias principais
				configurarDados(Utilidade.getCaminhoValidacao("CONSTANTE", padrao, i));
			}
			if(Constantes.BASE_VALIDACAO == false){
				// carrega os dados de validação nas instancias principais
				configurarDados(Utilidade.getCaminhoTeste("CONSTANTE", padrao, i));
			}
		}
		if(tipoValidacao == TipoValidacao.LEAVE_ONE_OUT){
			if(Constantes.BASE_VALIDACAO == true){
				// carrega os dados de validação nas instancias principais
				configurarDados(Utilidade.getCaminhoValidacaoLeaveOneOut("CONSTANTE", padrao, i));
			}
			if(Constantes.BASE_VALIDACAO == false){
				// carrega os dados de validação nas instancias principais
				configurarDados(Utilidade.getCaminhoTesteLeaveOneOut("CONSTANTE", padrao, i));
			}
		}

		// calcula as estimativas combinadas utilizando os resultados de KNN e os dados de classificação para o melhor algoritimo 
		calcularEstimativas(padrao, resultadoRegressor1, resultadoRegressor2, resultadoRegressor3, tipoClassificador1, tipoClassificador2, tipoClassificador3, tipoClassificador4, tipoClassificador5, tipoClassificador6, tipoClassificador7, tipoSelecaoDinamica, tipoValidacao, i);
		
		
		// imprimi os resultados em tela
		//imprimirResultadosMRE();
		calcularResultadosGerais();
		
	}

	private void calcularEstimativas(Padrao padrao, Resultado resultadoRegressor1,
			Resultado resultadoRegressor2, Resultado resultadoRegressor3, Integer tipoClassificador1, Integer tipoClassificador2, Integer tipoClassificador3, Integer tipoClassificador4,Integer tipoClassificador5, Integer tipoClassificador6,Integer tipoClassificador7, TipoMetodoCombinacao tipoSelecaoDinamica, TipoValidacao tipoValidacao, int i)
			throws Exception {

		
		double valorEstimado = 0;
		double valorReal = 0;
		double erroAbsoluto, marlog, mreAjustado;
		Instance instancia;
		resultado = new Resultado();
		resultado.setNomeModelo(toString() + " - " + padrao.toString());
		
		Classifier classificador1 = null, 
				classificador2 = null, 
				classificador3 = null, 
				classificador4 = null, 
				classificador5 = null, 
				classificador6 = null, 
				classificador7 = null;
		
		//obtem o modelo regressor1 criado
		ObjectInputStream oisRegressor1 = new ObjectInputStream(new FileInputStream(resultadoRegressor1.getNomeModelo()));
		Classifier regressor1 = (Classifier) oisRegressor1.readObject();
		oisRegressor1.close();

		//obtem o modelo regressor2 criado
		ObjectInputStream oisRegressor2 = new ObjectInputStream(new FileInputStream(resultadoRegressor2.getNomeModelo()));
		Classifier regressor2 = (Classifier) oisRegressor2.readObject();
		oisRegressor2.close();
		
		//obtem o modelo regressor3 criado
		ObjectInputStream oisRegressor3 = new ObjectInputStream(new FileInputStream(resultadoRegressor3.getNomeModelo()));
		Classifier regressor3 = (Classifier) oisRegressor3.readObject();
		oisRegressor3.close();
		
		if(tipoValidacao == TipoValidacao.HOLD_OUT){

			if(Constantes.BASE_VALIDACAO == true){
				// configura as instancias com o classificador de melhor tecnica e sem o classificador 		
				File arquivoValidacaoClassificador = new File(Constantes.CAMINHO_PADRAO + Utilidade.getCaminhoValidacaoClassificacao(getNomeAlgoritmoModeloEnsemble(tipoSelecaoDinamica), Constantes.QUANTIDADE_REGRESSOR, padrao, i));
				if(!arquivoValidacaoClassificador.exists()){
					setListaProjetosValidacao(Padrao.converteInstanciasParaProjetos(padrao, Utilidade.getCaminhoValidacao(getNomeAlgoritmoModeloEnsemble(tipoSelecaoDinamica), padrao, i)));
					GeradorAleatorioDados.gerarValidacaoClassificador(getListaProjetosValidacao(), getNomeAlgoritmoModeloEnsemble(tipoSelecaoDinamica), padrao, i);
				}
		
				configurarDadosClassificacao(Utilidade.getCaminhoTreinamentoClassificacao(padrao, Constantes.QUANTIDADE_REGRESSOR,  Constantes.TIPO_METRICA_AVALIACAO, i), Utilidade.getCaminhoValidacaoClassificacao(getNomeAlgoritmoModeloEnsemble(tipoSelecaoDinamica), Constantes.QUANTIDADE_REGRESSOR, padrao, i));
			}

			if(Constantes.BASE_VALIDACAO == false){
				// configura as instancias com o classificador de melhor tecnica e sem o classificador 		
				File arquivoTesteClassificador = new File(Constantes.CAMINHO_PADRAO + Utilidade.getCaminhoTesteClassificacao(getNomeAlgoritmoModeloEnsemble(tipoSelecaoDinamica), Constantes.QUANTIDADE_REGRESSOR, padrao, i));
				if(!arquivoTesteClassificador.exists()){
					setListaProjetosTeste(Padrao.converteInstanciasParaProjetos(padrao, Utilidade.getCaminhoTeste(getNomeAlgoritmoModeloEnsemble(tipoSelecaoDinamica), padrao, i)));
					GeradorAleatorioDados.gerarTesteClassificador(getListaProjetosTeste(), getNomeAlgoritmoModeloEnsemble(tipoSelecaoDinamica), padrao, i);
				}
		
				configurarDadosClassificacao(Utilidade.getCaminhoTreinamentoClassificacao(padrao,Constantes.QUANTIDADE_REGRESSOR, Constantes.TIPO_METRICA_AVALIACAO, i), Utilidade.getCaminhoTesteClassificacao(getNomeAlgoritmoModeloEnsemble(tipoSelecaoDinamica), Constantes.QUANTIDADE_REGRESSOR, padrao, i));
			}
		}
		if(tipoValidacao == TipoValidacao.LEAVE_ONE_OUT){

			if(Constantes.BASE_VALIDACAO == true){
				// configura as instancias com o classificador de melhor tecnica e sem o classificador 		
				File arquivoValidacaoClassificador = new File(Constantes.CAMINHO_PADRAO + Utilidade.getCaminhoValidacaoClassificacaoLeaveOneOut(getNomeAlgoritmoModeloEnsemble(tipoSelecaoDinamica), Constantes.QUANTIDADE_REGRESSOR, padrao, i));
				if(!arquivoValidacaoClassificador.exists()){
					setListaProjetosValidacao(Padrao.converteInstanciasParaProjetos(padrao, Utilidade.getCaminhoValidacaoLeaveOneOut(getNomeAlgoritmoModeloEnsemble(tipoSelecaoDinamica), padrao, i)));
					GeradorAleatorioDados.gerarValidacaoClassificadorLeaveOneOut(getListaProjetosValidacao(), getNomeAlgoritmoModeloEnsemble(tipoSelecaoDinamica), padrao, i);
				}
		
				configurarDadosClassificacao(Utilidade.getCaminhoTreinamentoClassificacaoLeaveOneOut(padrao, Constantes.QUANTIDADE_REGRESSOR, Constantes.TIPO_METRICA_AVALIACAO, i), Utilidade.getCaminhoValidacaoClassificacaoLeaveOneOut(getNomeAlgoritmoModeloEnsemble(tipoSelecaoDinamica), Constantes.QUANTIDADE_REGRESSOR, padrao, i));
			}

			if(Constantes.BASE_VALIDACAO == false){
				// configura as instancias com o classificador de melhor tecnica e sem o classificador 		
				File arquivoTesteClassificador = new File(Constantes.CAMINHO_PADRAO + Utilidade.getCaminhoTesteClassificacaoLeaveOneOut(getNomeAlgoritmoModeloEnsemble(tipoSelecaoDinamica), Constantes.QUANTIDADE_REGRESSOR, padrao, i));
				if(!arquivoTesteClassificador.exists()){
					setListaProjetosTeste(Padrao.converteInstanciasParaProjetos(padrao, Utilidade.getCaminhoTesteLeaveOneOut(getNomeAlgoritmoModeloEnsemble(tipoSelecaoDinamica), padrao, i)));
					GeradorAleatorioDados.gerarTesteClassificadorLeaveOneOut(getListaProjetosTeste(), getNomeAlgoritmoModeloEnsemble(tipoSelecaoDinamica), padrao, i);
				}
		
				configurarDadosClassificacao(Utilidade.getCaminhoTreinamentoClassificacaoLeaveOneOut(padrao, Constantes.QUANTIDADE_REGRESSOR, Constantes.TIPO_METRICA_AVALIACAO, i), Utilidade.getCaminhoTesteClassificacaoLeaveOneOut(getNomeAlgoritmoModeloEnsemble(tipoSelecaoDinamica), Constantes.QUANTIDADE_REGRESSOR, padrao, i));
			}
		}

		// obtem os classificadores treinados com as instancias que foram configuradas
		if(tipoClassificador1 != null){
			classificador1 = getClassificadorTreinado(tipoClassificador1);
		}
		if(tipoClassificador2 != null){
			classificador2 = getClassificadorTreinado(tipoClassificador2);
		}
		if(tipoClassificador3 != null){
			classificador3 = getClassificadorTreinado(tipoClassificador3);
		}
		if(tipoClassificador4 != null){
			classificador4 = getClassificadorTreinado(tipoClassificador4);
		}
		if(tipoClassificador5 != null){
			classificador5 = getClassificadorTreinado(tipoClassificador5);
		}
		if(tipoClassificador6 != null){
			classificador6 = getClassificadorTreinado(tipoClassificador6);
		}
		if(tipoClassificador7 != null){
			classificador7 = getClassificadorTreinado(tipoClassificador7);
		}

		String algoritmoEscolhidoPeloClassificador = null;
		Instance instanciaSemClassificador;
		List<Double> valoresEstimados = new ArrayList<Double>();
		

		
		//calcular a distância de cada elemento da lista de projetos validação para a lista de projetos avaliados
		for(int j = 0 ; j < instancias.numInstances() ; j++){
			
			ResultadoIdeal resultadoIdeal;
			
			//pega cada instancia do conjunto de validação com rotulo de esforço
			instancia = instancias.instance(j);
			
			//pega cada instancia do conjunto de validação com rotulo de melhor algoritimo vazio
			instanciaSemClassificador = instanciasSemClassificacao.instance(j);
			
			valoresEstimados = new ArrayList<Double>();
			
			switch (Constantes.QUANTIDADE_CLASSIFICADOR) {
			
			case 7:				
				valorEstimado = calculaEstimativaDinamica(padrao, classificador1, instanciaSemClassificador, algoritmoEscolhidoPeloClassificador, regressor1, regressor2, regressor3, instancia);
				valoresEstimados.add(valorEstimado);

			case 6:				
				valorEstimado = calculaEstimativaDinamica(padrao, classificador2, instanciaSemClassificador, algoritmoEscolhidoPeloClassificador, regressor1, regressor2, regressor3, instancia);
				valoresEstimados.add(valorEstimado);

			case 5:				
				valorEstimado = calculaEstimativaDinamica(padrao, classificador3, instanciaSemClassificador, algoritmoEscolhidoPeloClassificador, regressor1, regressor2, regressor3, instancia);
				valoresEstimados.add(valorEstimado);
				
			case 4:				
				valorEstimado = calculaEstimativaDinamica(padrao, classificador4, instanciaSemClassificador, algoritmoEscolhidoPeloClassificador, regressor1, regressor2, regressor3, instancia);
				valoresEstimados.add(valorEstimado);
				
			case 3:				
				valorEstimado = calculaEstimativaDinamica(padrao, classificador5, instanciaSemClassificador, algoritmoEscolhidoPeloClassificador, regressor1, regressor2, regressor3, instancia);
				valoresEstimados.add(valorEstimado);
				
			case 2:	
				valorEstimado = calculaEstimativaDinamica(padrao, classificador6, instanciaSemClassificador, algoritmoEscolhidoPeloClassificador, regressor1, regressor2, regressor3, instancia);
				valoresEstimados.add(valorEstimado);
				
			case 1:	
				valorEstimado = calculaEstimativaDinamica(padrao, classificador7, instanciaSemClassificador, algoritmoEscolhidoPeloClassificador, regressor1, regressor2, regressor3, instancia);
				valoresEstimados.add(valorEstimado);

			}

			valorEstimado = getEstimativaEnsemble(tipoSelecaoDinamica, valoresEstimados);
			
			// recupera o valor real do esforço
			valorReal = (double) instancia.classValue();
			
			// Processo para encontrar o melhor algoritimo na verdade e comparar com o escolhido			
			resultadoIdeal = obtemResultadoIdeal(valorReal,
					instancia, regressor1,  regressor2, regressor3, algoritmoEscolhidoPeloClassificador);

			if (resultadoIdeal.isAcertouAlgoritmo()){
				resultado.addAcerto();
			}else{
				resultado.addErro();
			}

			//calcula o erro absoluto
			erroAbsoluto = Math
					.abs(valorReal - (valorEstimado));
			
			//calcula marlog
			marlog = Math.log10(erroAbsoluto);
		
			//calcula mreAjustado
			mreAjustado = ((erroAbsoluto/valorReal) + (erroAbsoluto/valorEstimado))/2;
			
			//adiciona o erro a lista de erros absolutos
			resultado.getErrosAbsolutos().add(erroAbsoluto);
			
			//adiciona o marlog a lista de marlogs
			resultado.getMarlogs().add(marlog);			
			
		
			// adiciona o mreajustado a lista de mreajustados
			resultado.getMresAjustados().add(mreAjustado);
			
				
		}
	}

	
	private ResultadoIdeal obtemResultadoIdeal(double valorReal,
			Instance instancia, Classifier regressor1, Classifier regressor2, Classifier regressor3,
			String algoritmoEscolhidoPeloClassificador) throws Exception {

		double valorEstimado, melhorValorEstimado;
		double erroAbs;
		double erroMre, menorErroMre;
		String melhorAlgoritmo;
		ResultadoIdeal resultadoIdeal = new ResultadoIdeal();

		
		// Classificamos esta instância com o algoritimo relativo ao valor para o knn
		valorEstimado = (double) (regressor1.classifyInstance(instancia));
		erroAbs = Math.abs(valorReal - (valorEstimado));
		erroMre = erroAbs / valorReal;
		
		melhorValorEstimado = valorEstimado;
		melhorAlgoritmo = REGRESSOR1;
		menorErroMre = erroMre;
		

		// Classificamos esta instância com o algoritimo relativo ao valor para o regressor21
		valorEstimado = (double)(regressor2.classifyInstance(instancia));
		erroAbs = Math.abs(valorReal - (valorEstimado));
		erroMre = erroAbs / valorReal;
		if (erroMre < menorErroMre){
			melhorValorEstimado = valorEstimado;
			melhorAlgoritmo = REGRESSOR2;
			menorErroMre = erroMre;
		}

		// Classificamos esta instância com o algoritimo relativo ao valor para o regressor3
		valorEstimado = (double)(regressor3.classifyInstance(instancia));
		erroAbs = Math.abs(valorReal - (valorEstimado));
		erroMre = erroAbs / valorReal;
		if (erroMre < menorErroMre){
			melhorValorEstimado = valorEstimado;
			melhorAlgoritmo = REGRESSOR3;
			menorErroMre = erroMre;
		}

		resultadoIdeal.setMelhorAlgoritmo(melhorAlgoritmo);
		resultadoIdeal.setMelhorValorEstimado(melhorValorEstimado);
		resultadoIdeal.setMenorErroEstimado(menorErroMre);
		
//		System.out.print("	" + resultadoIdeal.getMelhorAlgoritmo());
//		System.out.print("	" +  algoritmoEscolhidoPeloClassificador);
//		System.out.println();
		
		if(melhorAlgoritmo.equals(algoritmoEscolhidoPeloClassificador)){
			resultadoIdeal.setAcertouAlgoritmo(true);
		}else{
			resultadoIdeal.setAcertouAlgoritmo(false);
		}
		
		return resultadoIdeal;
	}
	
	private Double calculaEstimativaDinamica(Padrao padrao,
			Classifier classificador, Instance instanciaSemClassificador,
			String algoritmoEscolhidoPeloClassificador, Classifier regressor1, Classifier regressor2, Classifier regressor3,
			Instance instancia) throws Exception {
		
		// configura o rótulo
		double rotuloClassificador = 0;
		Attribute rotulo = instanciasClassificadas.attribute(padrao.getIndiceRotuloClassificador());
		double valorEstimado;

		// classifica dinâmicamente a instancia com o melhor regressor usando o
		// algoritimo de classificação
		rotuloClassificador = classificador.classifyInstance(instanciaSemClassificador);

		// converte a classificação obtida para valor de caracteres que
		// representa o melho algoritimo
		algoritmoEscolhidoPeloClassificador = rotulo.value((int) rotuloClassificador);

		// verifica o valor do classificador estimado
		if (algoritmoEscolhidoPeloClassificador.equals(REGRESSOR1)) {

			// Classificamos esta instância com o algoritimo relativo ao valor
			// knn
			valorEstimado = (double) Math.abs((regressor1.classifyInstance(instancia)));

		} else if (algoritmoEscolhidoPeloClassificador.equals(REGRESSOR2)) {

			// Classificamos esta instância com o algoritimo relativo ao valor
			// regressor2
			valorEstimado = (double) Math.abs((regressor2.classifyInstance(instancia)));

		} else if (algoritmoEscolhidoPeloClassificador.equals(REGRESSOR3)) {

			// Classificamos esta instância com o algoritimo relativo ao valor
			// regressor3
			valorEstimado = (double) Math.abs((regressor3.classifyInstance(instancia)));

		} else {
			System.out.println(algoritmoEscolhidoPeloClassificador);
			throw new Exception("Melhor Algoritmo não encontrado");
		}
		return valorEstimado;

	}
	
	@Override
	public void run(Padrao padrao, Integer tipoClassificador1, Integer tipoClassificador2, Integer tipoClassificador3, Integer tipoClassificador4, Integer tipoClassificador5, Integer tipoClassificador6, Integer tipoClassificador7, TipoMetodoCombinacao tipoSelecaoDinamica) {

		File arquivo = null;
		Utilidade.adicionaCabecalhoDados(padrao);

		try {
			
			arquivo = new File(Constantes.CAMINHO_PADRAO + Utilidade.getCaminhoTreinamento("CONSTANTE", padrao, Constantes.INDICE_INICIAL));
			
			if(!arquivo.exists()){
				GeradorAleatorioDados.gerarTreinamentoValidacaoTeste("CONSTANTE", padrao);
			}

			for(int i = Constantes.INDICE_INICIAL; i <= Constantes.INDICE_FINAL; i++){
			
			//treina o regressor1
			RegressorIndividualGeral regressorIndividualGeral1 = new RegressorIndividualGeral(regressor1);
			regressorIndividualGeral1.run(Utilidade.getCaminhoTreinamento("CONSTANTE", padrao, i),
					Utilidade.getCaminhoTreinamento("CONSTANTE", padrao, i));

			//treina o regressor2
			RegressorIndividualGeral regressorIndividualGeral2 = new RegressorIndividualGeral(regressor2);
			regressorIndividualGeral2.run(Utilidade.getCaminhoTreinamento("CONSTANTE", padrao, i), 
					Utilidade.getCaminhoTreinamento("CONSTANTE", padrao, i));

			//treina o regressor3
			RegressorIndividualGeral regressorIndividualGeral3 = new RegressorIndividualGeral(regressor3);
			regressorIndividualGeral3.run(Utilidade.getCaminhoTreinamento("CONSTANTE", padrao, i),
					Utilidade.getCaminhoTreinamento("CONSTANTE", padrao, i));
			
			
			//carrega a lista de projetos que foram testados
			setListaProjetos(Padrao.converteInstanciasParaProjetos(padrao, Utilidade.getCaminhoTreinamento("CONSTANTE", padrao, i)));
			
			//adiciona os campos melhor algoritimo e menor erro a lista de projetos
			adicionarCamposListaProjetosAvaliados(padrao, regressorIndividualGeral1.getResultado(), regressorIndividualGeral2.getResultado(), regressorIndividualGeral3.getResultado(), "CONSTANTE", TipoValidacao.HOLD_OUT, i);
			
			//avalia o modelo combinado
			avaliarMetodo(padrao, regressorIndividualGeral1.getResultado(), regressorIndividualGeral2.getResultado(), regressorIndividualGeral3.getResultado(), tipoClassificador1, tipoClassificador2, tipoClassificador3, tipoClassificador4, tipoClassificador5, tipoClassificador6, tipoClassificador7, tipoSelecaoDinamica, TipoValidacao.HOLD_OUT, i);
			
			}
			
			Utilidade.calculaMediasMetricas();
			Utilidade.zerarMediasAuxiliares();
			Utilidade.adicionaLinhaArquivo();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private String getNomeAlgoritmoModeloEnsemble(TipoMetodoCombinacao tipoMetodoCombinacao){
		
		return "CONSTANTE";
	}

	@Override
	public void run(Padrao padrao, TipoValidacao tipoValidacao) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run(Padrao padrao, Integer tipoClassificador1, Integer tipoClassificador2, Integer tipoClassificador3, Integer tipoClassificador4, Integer tipoClassificador5, Integer tipoClassificador6, Integer tipoClassificador7,
			TipoMetodoCombinacao tipoSelecaoDinamica, TipoValidacao tipoValidacao) {
		try {
			
			File arquivo = null;
			Utilidade.adicionaCabecalhoDados(padrao);
			
			
			if(tipoValidacao == TipoValidacao.HOLD_OUT){
				arquivo = new File(Constantes.CAMINHO_PADRAO + Utilidade.getCaminhoTreinamento("CONSTANTE", padrao, Constantes.INDICE_INICIAL));
				
				if(!arquivo.exists()){
					GeradorAleatorioDados.gerarTreinamentoValidacaoTeste("CONSTANTE", padrao);
				}
	
				for(int i = Constantes.INDICE_INICIAL; i <= Constantes.INDICE_FINAL; i++){
				
				Utilidade.ADICIONAR_RESULTADOS_INDIVIDUAIS = false;
				//treina o regressor1
				RegressorIndividualGeral regressorIndividualGeral1 = new RegressorIndividualGeral(regressor1);
				regressorIndividualGeral1.run(Utilidade.getCaminhoTreinamento("CONSTANTE", padrao, i),
						Utilidade.getCaminhoTreinamento("CONSTANTE", padrao, i));
	
				//treina o regressor2
				RegressorIndividualGeral regressorIndividualGeral2 = new RegressorIndividualGeral(regressor2);
				regressorIndividualGeral2.run(Utilidade.getCaminhoTreinamento("CONSTANTE", padrao, i), 
						Utilidade.getCaminhoTreinamento("CONSTANTE", padrao, i));
	
				//treina o regressor3
				RegressorIndividualGeral regressorIndividualGeral3 = new RegressorIndividualGeral(regressor3);
				regressorIndividualGeral3.run(Utilidade.getCaminhoTreinamento("CONSTANTE", padrao, i),
						Utilidade.getCaminhoTreinamento("CONSTANTE", padrao, i));
				Utilidade.ADICIONAR_RESULTADOS_INDIVIDUAIS = true;
				
				//carrega a lista de projetos que foram testados
				setListaProjetos(Padrao.converteInstanciasParaProjetos(padrao, Utilidade.getCaminhoTreinamento("CONSTANTE", padrao, i)));
				
				//adiciona os campos melhor algoritimo e menor erro a lista de projetos
				adicionarCamposListaProjetosAvaliados(padrao, regressorIndividualGeral1.getResultado(), regressorIndividualGeral2.getResultado(), regressorIndividualGeral3.getResultado(), "CONSTANTE", tipoValidacao, i);
				
				//avalia o modelo combinado
				avaliarMetodo(padrao, regressorIndividualGeral1.getResultado(), regressorIndividualGeral2.getResultado(), regressorIndividualGeral3.getResultado(), tipoClassificador1, tipoClassificador2, tipoClassificador3, tipoClassificador4, tipoClassificador5, tipoClassificador6, tipoClassificador7, tipoSelecaoDinamica, tipoValidacao, i);
				
				}
			}
			
			if(tipoValidacao == TipoValidacao.LEAVE_ONE_OUT){
				arquivo = new File(Constantes.CAMINHO_PADRAO + Utilidade.getCaminhoTreinamentoLeaveOneOut("CONSTANTE", padrao, Constantes.INDICE_INICIAL));
				
				if(!arquivo.exists()){
					GeradorAleatorioDados.gerarTreinamentoValidacaoTesteLeaveOneOut("CONSTANTE", padrao);
				}
	
				for(int i = Constantes.INDICE_INICIAL; i <= padrao.getTamanhoBase(); i++){

				Utilidade.ADICIONAR_RESULTADOS_INDIVIDUAIS = false;
				//treina o Regressor1
				RegressorIndividualGeral regressorIndividualGeral1 = new RegressorIndividualGeral(regressor1);
				regressorIndividualGeral1.run(Utilidade.getCaminhoTreinamentoLeaveOneOut("CONSTANTE", padrao, i),
						Utilidade.getCaminhoTreinamentoLeaveOneOut("CONSTANTE", padrao, i));
	
				//treina o Regressor2
				RegressorIndividualGeral regressorIndividualGeral2 = new RegressorIndividualGeral(regressor2);
				regressorIndividualGeral2.run(Utilidade.getCaminhoTreinamentoLeaveOneOut("CONSTANTE", padrao, i), 
						Utilidade.getCaminhoTreinamentoLeaveOneOut("CONSTANTE", padrao, i));
	
				//treina o Regressor3
				RegressorIndividualGeral regressorIndividualGeral3 = new RegressorIndividualGeral(regressor3);
				regressorIndividualGeral3.run(Utilidade.getCaminhoTreinamentoLeaveOneOut("CONSTANTE", padrao, i),
						Utilidade.getCaminhoTreinamentoLeaveOneOut("CONSTANTE", padrao, i));
				Utilidade.ADICIONAR_RESULTADOS_INDIVIDUAIS = true;
		
				
				//carrega a lista de projetos que foram testados
				setListaProjetos(Padrao.converteInstanciasParaProjetos(padrao, Utilidade.getCaminhoTreinamentoLeaveOneOut("CONSTANTE", padrao, i)));
				
				//adiciona os campos melhor algoritimo e menor erro a lista de projetos
				adicionarCamposListaProjetosAvaliados(padrao, regressorIndividualGeral1.getResultado(), regressorIndividualGeral2.getResultado(), regressorIndividualGeral3.getResultado(), "CONSTANTE", tipoValidacao, i);
				
				//avalia o modelo combinado
				avaliarMetodo(padrao, regressorIndividualGeral1.getResultado(), regressorIndividualGeral2.getResultado(), regressorIndividualGeral3.getResultado(), tipoClassificador1, tipoClassificador2, tipoClassificador3, tipoClassificador4, tipoClassificador5, tipoClassificador6, tipoClassificador7, tipoSelecaoDinamica, tipoValidacao, i);
				
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
	
}

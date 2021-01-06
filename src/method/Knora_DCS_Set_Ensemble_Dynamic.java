package method;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import main.TesteKnoraDCSSetEnsembleDynamic;
import model.Resultado;
import model.ResultadoIdeal;
import util.Constantes;
import util.GeradorAleatorioDados;
import util.TipoMetodoCombinacao;
import util.TipoValidacao;
import util.Utilidade;
import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.neighboursearch.LinearNNSearch;
import data.Padrao;

public class Knora_DCS_Set_Ensemble_Dynamic extends SetEnsembleDynamicTecnica {
	
	private Classifier regressor1;
	private Classifier regressor2;
	private Classifier regressor3;
	
	public Knora_DCS_Set_Ensemble_Dynamic() {
	}

	public Knora_DCS_Set_Ensemble_Dynamic(Classifier regressor1, Classifier regressor2, Classifier regressor3) {
		this.regressor1 = regressor1;
		this.regressor2 = regressor2;
		this.regressor3 = regressor3;
	}

	private void avaliarMetodo(Padrao padrao, Resultado resultado1, Resultado resultado2,
			Resultado resultado3, TipoMetodoCombinacao tipoSelecaoDinamica, TipoValidacao tipoValidacao, int i) throws Exception{
		
		
		if(tipoValidacao == TipoValidacao.HOLD_OUT){
			// carrega os dados de validação nas instancias principais
			configurarDados(Utilidade.getCaminhoTeste("CONSTANTE", padrao, i));
		}

		if(tipoValidacao == TipoValidacao.LEAVE_ONE_OUT){
			// carrega os dados de validação nas instancias principais
			configurarDados(Utilidade.getCaminhoTesteLeaveOneOut("CONSTANTE", padrao, i));
		}

		// calcula as estimativas combinadas utilizando os resultados de KNN e os dados de classificação para o melhor algoritimo 
		calcularEstimativas(padrao, resultado1, resultado2, resultado3, tipoSelecaoDinamica, tipoValidacao, i);
	
		calcularResultadosGerais();

	}

	private void calcularEstimativas(Padrao padrao, Resultado resultado1,
			Resultado resultado2, Resultado resultado3, TipoMetodoCombinacao tipoSelecaoDinamica, TipoValidacao tipoValidacao, int i)
			throws Exception {

		
		double valorEstimado = 0;
		double valorReal = 0;
		double erroAbsoluto, marlog, mreAjustado;
		Instance instancia;
		double menor = 0;

		
		resultado = new Resultado();
		resultado.setNomeModelo(toString() + " - " + padrao.toString());
			
		//obtem o modelo 1 criado
		ObjectInputStream objectInputStream1 = new ObjectInputStream(new FileInputStream(resultado1.getNomeModelo()));
		Classifier regressor1 = (Classifier) objectInputStream1.readObject();
		objectInputStream1.close();

		//obtem o modelo 2 criado
		ObjectInputStream objectInputStream2 = new ObjectInputStream(new FileInputStream(resultado2.getNomeModelo()));
		Classifier regressor2 = (Classifier) objectInputStream2.readObject();
		objectInputStream2.close();
		
		//obtem o modelo 3 criado
		ObjectInputStream objectInputStream3 = new ObjectInputStream(new FileInputStream(resultado3.getNomeModelo()));
		Classifier regressor3 = (Classifier) objectInputStream3.readObject();
		objectInputStream3.close();
		
		//calcular a distância de cada elemento da lista de projetos validação para a lista de projetos avaliados
		for(int j = 0 ; j < instancias.numInstances() ; j++){
			
			ResultadoIdeal resultadoIdeal;
			
			//pega cada instancia do conjunto de validação com rotulo de esforço
			instancia = instancias.instance(j);
			
			valorEstimado = Math.abs(calculaEstimativaDinamica(padrao, regressor1, regressor2, regressor3, tipoSelecaoDinamica, tipoValidacao, instancia, i));

			// recupera o valor real do esforço
			valorReal = (double) instancia.classValue();
			
			// Processo para encontrar o melhor algoritimo na verdade e comparar com o escolhido			
			resultadoIdeal = obtemResultadoIdeal(valorReal,
					instancia, regressor1,  regressor2, regressor3);

			if (resultadoIdeal.isAcertouAlgoritmo()){
				resultado.addAcerto();
			}else{
				resultado.addErro();
			}

			//calcula o erro absoluto
			erroAbsoluto = Math
					.abs(valorReal - (valorEstimado));

			marlog = Math.log10(erroAbsoluto);


			// calcula o bre
			if (valorReal < valorEstimado){
				menor = valorReal;
			}else{
				menor = valorEstimado;
			}
			

			// calcula o mreajustado
			mreAjustado = ((erroAbsoluto / valorReal) + (erroAbsoluto / valorEstimado)/2);

			//adiciona o erro a lista de erros absolutos
			resultado.getErrosAbsolutos().add(erroAbsoluto);
			
	
			resultado.getMarlogs().add(marlog);
			resultado.getMresAjustados().add(mreAjustado);

		}
	}

	
	private ResultadoIdeal obtemResultadoIdeal(double valorReal,
			Instance instancia, Classifier regressor1, Classifier regressor2, Classifier regressor3) throws Exception {

		double valorEstimado, melhorValorEstimado;
		double erroAbs;
		double erroMre, menorErroMre;
		String melhorAlgoritmo;
		ResultadoIdeal resultadoIdeal = new ResultadoIdeal();

		
		// Classificamos esta instância com o algoritimo relativo ao valor para o regressor1
		valorEstimado = (double) (regressor1.classifyInstance(instancia));
		erroAbs = Math.abs(valorReal - (valorEstimado));
		erroMre = erroAbs / valorReal;
		
		melhorValorEstimado = valorEstimado;
		melhorAlgoritmo = regressor1.getClass().getName();
		menorErroMre = erroMre;
		

		// Classificamos esta instância com o algoritimo relativo ao valor para o regressor2
		valorEstimado = (double)(regressor2.classifyInstance(instancia));
		erroAbs = Math.abs(valorReal - (valorEstimado));
		erroMre = erroAbs / valorReal;
		if (erroMre < menorErroMre){
			melhorValorEstimado = valorEstimado;
			melhorAlgoritmo = regressor2.getClass().getName();
			menorErroMre = erroMre;
		}

		// Classificamos esta instância com o algoritimo relativo ao valor para o regressor3
		valorEstimado = (double)(regressor3.classifyInstance(instancia));
		erroAbs = Math.abs(valorReal - (valorEstimado));
		erroMre = erroAbs / valorReal;
		if (erroMre < menorErroMre){
			melhorValorEstimado = valorEstimado;
			melhorAlgoritmo = regressor3.getClass().getName();
			menorErroMre = erroMre;
		}

		resultadoIdeal.setMelhorAlgoritmo(melhorAlgoritmo);
		resultadoIdeal.setMelhorValorEstimado(melhorValorEstimado);
		resultadoIdeal.setMenorErroEstimado(menorErroMre);
		
//		System.out.print("	" + resultadoIdeal.getMelhorAlgoritmo());
//		System.out.print("	" +  algoritmoEscolhidoPeloClassificador);
//		System.out.println();
		
/*		if(melhorAlgoritmo.equals(algoritmoEscolhidoPeloClassificador)){
			resultadoIdeal.setAcertouAlgoritmo(true);
		}else{
			resultadoIdeal.setAcertouAlgoritmo(false);
		}
*/		
		return resultadoIdeal;
	}
	
	private Double calculaEstimativaDinamica(Padrao padrao, Classifier regressor1, Classifier regressor2, Classifier regressor3, TipoMetodoCombinacao tipoSelecaoDinamica, TipoValidacao tipoValidacao,
			Instance instancia, int i) throws Exception {
		

		KNN_Knora knnSelector = new KNN_Knora();
		if(tipoValidacao == TipoValidacao.HOLD_OUT){
			knnSelector.criarModeloKNORA(7, Utilidade.getCaminhoTreinamento("CONSTANTE", padrao, i));
		}

		if(tipoValidacao == TipoValidacao.LEAVE_ONE_OUT){
			knnSelector.criarModeloKNORA(7, Utilidade.getCaminhoTreinamentoLeaveOneOut("CONSTANTE", padrao, i));
		}

		ObjectInputStream oisKNNSelector = new ObjectInputStream(new FileInputStream("KNORA_"+ 7));
		IBk knnKNORA = (IBk) oisKNNSelector.readObject();
		oisKNNSelector.close();
	
		LinearNNSearch nns = (LinearNNSearch)knnKNORA.getNearestNeighbourSearchAlgorithm();
		Instances instanciasK = nns.kNearestNeighbours(instancia, 7);
		double[] distancias = (nns.getDistances());

		double valorEstimado1 = 0;
		double valorEstimado2 = 0;
		double valorEstimado3 = 0;
		
		double valorReal;
		Instance instanciaAtual;
		
		List<Boolean> acertos1 = new ArrayList<>();
		List<Boolean> acertos2 = new ArrayList<>();
		List<Boolean> acertos3 = new ArrayList<>();
		
	
		int numeroDeSelecionados = 0;
		double valorEstimado = 0;

		if(tipoSelecaoDinamica == TipoMetodoCombinacao.DCS_LA){
			
			double erro1 = 0;
			double erro2 = 0;
			double erro3 = 0;
			
			
			for(int j = 0; j < instanciasK.numInstances(); j++){
				
				instanciaAtual = instanciasK.instance(j);
				valorReal = instanciaAtual.classValue();
				
				
				valorEstimado1 = Math.abs(regressor1.classifyInstance(instanciasK.instance(j)));
				if(Utilidade.METRICA_AVALIACAO.equals(Constantes.MAR)){
					erro1 += (Math.abs(valorReal - valorEstimado1));
				}
				if(Utilidade.METRICA_AVALIACAO.equals(Constantes.MARLOG)){
					erro1 += Math.log10((Math.abs(valorReal - valorEstimado1)));
				}
				if(Utilidade.METRICA_AVALIACAO.equals(Constantes.MREAJUSTADO)){
					erro1 += ((Math.abs(valorReal - valorEstimado1) / valorReal) + (Math.abs(valorReal - valorEstimado1) / valorEstimado1)) / 2;
				}
	
				valorEstimado2 = Math.abs(regressor2.classifyInstance(instanciasK.instance(j)));
				if(Utilidade.METRICA_AVALIACAO.equals(Constantes.MAR)){
					erro2 += (Math.abs(valorReal - valorEstimado2));
				}
				if(Utilidade.METRICA_AVALIACAO.equals(Constantes.MARLOG)){
					erro2 += Math.log10((Math.abs(valorReal - valorEstimado2)));
				}
				if(Utilidade.METRICA_AVALIACAO.equals(Constantes.MREAJUSTADO)){
					erro2 += ((Math.abs(valorReal - valorEstimado2) / valorReal) + (Math.abs(valorReal - valorEstimado2) / valorEstimado2)) / 2;
				}
	
				valorEstimado3 = Math.abs(regressor3.classifyInstance(instanciasK.instance(j)));
				if(Utilidade.METRICA_AVALIACAO.equals(Constantes.MAR)){
					erro3 += (Math.abs(valorReal - valorEstimado3));
				}
				if(Utilidade.METRICA_AVALIACAO.equals(Constantes.MARLOG)){
					erro3 += Math.log10((Math.abs(valorReal - valorEstimado3)));
				}
				if(Utilidade.METRICA_AVALIACAO.equals(Constantes.MREAJUSTADO)){
					erro3 += (((Math.abs(valorReal - valorEstimado3) / valorReal) + (Math.abs(valorReal - valorEstimado3) / valorEstimado3)) / 2);
				}
	
				
		
			}
	
			erro1 /= instanciasK.numInstances();
			erro2 /= instanciasK.numInstances();
			erro3 /= instanciasK.numInstances();
			
			if (erro1 < erro2){
				if (erro1 < erro3){
					valorEstimado = Math.abs(regressor1.classifyInstance(instancia));
				}else{
					valorEstimado = Math.abs(regressor2.classifyInstance(instancia));
				}
			}else {
				if (erro2 < erro3){
					valorEstimado = Math.abs(regressor2.classifyInstance(instancia));
				}else{
					valorEstimado = Math.abs(regressor3.classifyInstance(instancia));
				}
			}		
		
		}

		if(tipoSelecaoDinamica == TipoMetodoCombinacao.DCS_LAW){
			
			double erro1 = 0;
			double erro2 = 0;
			double erro3 = 0;
			
			
			for(int j = 0; j < instanciasK.numInstances(); j++){
				
				instanciaAtual = instanciasK.instance(j);
				valorReal = instanciaAtual.classValue();
				
				
				valorEstimado1 = Math.abs(regressor1.classifyInstance(instanciasK.instance(j)));
				if(Utilidade.METRICA_AVALIACAO.equals(Constantes.MAR)){
					erro1 += ((Math.abs(valorReal - valorEstimado1)) / distancias[j]);
				}
				if(Utilidade.METRICA_AVALIACAO.equals(Constantes.MARLOG)){
					erro1 += (Math.log10((Math.abs(valorReal - valorEstimado1))) / distancias[j]);
				}
				if(Utilidade.METRICA_AVALIACAO.equals(Constantes.MREAJUSTADO)){
					erro1 += (((Math.abs(valorReal - valorEstimado1) / valorReal) + (Math.abs(valorReal - valorEstimado1) / valorEstimado1)) / 2) / distancias[j];
				}
	
				valorEstimado2 = Math.abs(regressor2.classifyInstance(instanciasK.instance(j)));
				if(Utilidade.METRICA_AVALIACAO.equals(Constantes.MAR)){
					erro2 += ((Math.abs(valorReal - valorEstimado2)) / distancias[j]);
				}
				if(Utilidade.METRICA_AVALIACAO.equals(Constantes.MARLOG)){
					erro2 += (Math.log10((Math.abs(valorReal - valorEstimado2))) / distancias[j]);
				}
				if(Utilidade.METRICA_AVALIACAO.equals(Constantes.MREAJUSTADO)){
					erro2 += (((Math.abs(valorReal - valorEstimado2) / valorReal) + (Math.abs(valorReal - valorEstimado2) / valorEstimado2)) / 2) / distancias[j];
				}
	
				valorEstimado3 = Math.abs(regressor3.classifyInstance(instanciasK.instance(j)));
				if(Utilidade.METRICA_AVALIACAO.equals(Constantes.MAR)){
					erro3 += ((Math.abs(valorReal - valorEstimado3)) / distancias[j]);
				}
				if(Utilidade.METRICA_AVALIACAO.equals(Constantes.MARLOG)){
					erro3 += (Math.log10((Math.abs(valorReal - valorEstimado3))) / distancias[j]);
				}
				if(Utilidade.METRICA_AVALIACAO.equals(Constantes.MREAJUSTADO)){
					erro3 += (((Math.abs(valorReal - valorEstimado3) / valorReal) + (Math.abs(valorReal - valorEstimado3) / valorEstimado3)) / 2) / distancias[j];
				}
	
				
		
			}
	
			erro1 /= instanciasK.numInstances();
			erro2 /= instanciasK.numInstances();
			erro3 /= instanciasK.numInstances();
			
			if (erro1 < erro2){
				if (erro1 < erro3){
					valorEstimado = Math.abs(regressor1.classifyInstance(instancia));
				}else{
					valorEstimado = Math.abs(regressor2.classifyInstance(instancia));
				}
			}else {
				if (erro2 < erro3){
					valorEstimado = Math.abs(regressor2.classifyInstance(instancia));
				}else{
					valorEstimado = Math.abs(regressor3.classifyInstance(instancia));
				}
			}		
		
		}


		if(tipoSelecaoDinamica == TipoMetodoCombinacao.KNORA_U){
		
			double erro1 = 0;
			double erro2 = 0;
			double erro3 = 0;

			for(int j = 0; j < instanciasK.numInstances(); j++){
				
				instanciaAtual = instanciasK.instance(j);
				valorReal = instanciaAtual.classValue();
				
				
				valorEstimado1 = Math.abs(regressor1.classifyInstance(instanciasK.instance(j)));
				if(Utilidade.METRICA_AVALIACAO.equals(Constantes.MAR)){
					erro1 += (Math.abs(valorReal - valorEstimado1));
					if (erro1 < padrao.getKnoraMediaMar()){
						acertos1.add(true);
					}else{
						acertos1.add(false);
					}
				}
				if(Utilidade.METRICA_AVALIACAO.equals(Constantes.MARLOG)){
					erro1 += Math.log10((Math.abs(valorReal - valorEstimado1)));
					if (erro1 < padrao.getKnoraMediaMarlog()){
						acertos1.add(true);
					}else{
						acertos1.add(false);
					}
				}
				if(Utilidade.METRICA_AVALIACAO.equals(Constantes.MREAJUSTADO)){
					erro1 += ((Math.abs(valorReal - valorEstimado1) / valorReal) + (Math.abs(valorReal - valorEstimado1) / valorEstimado1)) / 2;
					if (erro1 < padrao.getKnoraMediaMreajustado()){
						acertos1.add(true);
					}else{
						acertos1.add(false);
					}
				}
				
	
				
				valorEstimado2 = Math.abs(regressor2.classifyInstance(instanciasK.instance(j)));
				if(Utilidade.METRICA_AVALIACAO.equals(Constantes.MAR)){
					erro2 += (Math.abs(valorReal - valorEstimado2));
					if (erro2 < padrao.getKnoraMediaMar()){
						acertos2.add(true);
					}else{
						acertos2.add(false);
					}
				}
				if(Utilidade.METRICA_AVALIACAO.equals(Constantes.MARLOG)){
					erro2 += Math.log10((Math.abs(valorReal - valorEstimado2)));
					if (erro2 < padrao.getKnoraMediaMarlog()){
						acertos2.add(true);
					}else{
						acertos2.add(false);
					}
				}
				if(Utilidade.METRICA_AVALIACAO.equals(Constantes.MREAJUSTADO)){
					erro2 += ((Math.abs(valorReal - valorEstimado2) / valorReal) + (Math.abs(valorReal - valorEstimado2) / valorEstimado2)) / 2;
					if (erro2 < padrao.getKnoraMediaMreajustado()){
						acertos2.add(true);
					}else{
						acertos2.add(false);
					}
				}
	
				valorEstimado3 = Math.abs(regressor3.classifyInstance(instanciasK.instance(j)));
				if(Utilidade.METRICA_AVALIACAO.equals(Constantes.MAR)){
					erro3 += (Math.abs(valorReal - valorEstimado3));
					if (erro3 < padrao.getKnoraMediaMar()){
						acertos3.add(true);
					}else{
						acertos3.add(false);
					}
				}
				if(Utilidade.METRICA_AVALIACAO.equals(Constantes.MARLOG)){
					erro3 += Math.log10((Math.abs(valorReal - valorEstimado3)));
					if (erro3 < padrao.getKnoraMediaMarlog()){
						acertos3.add(true);
					}else{
						acertos3.add(false);
					}
				}
				if(Utilidade.METRICA_AVALIACAO.equals(Constantes.MREAJUSTADO)){
					erro3 += ((Math.abs(valorReal - valorEstimado3) / valorReal) + (Math.abs(valorReal - valorEstimado3) / valorEstimado3)) / 2;
					if (erro3 < padrao.getKnoraMediaMreajustado()){
						acertos3.add(true);
					}else{
						acertos3.add(false);
					}
				}
			}
			
			
			if (acertos1.contains(true)){
				valorEstimado += Math.abs(regressor1.classifyInstance(instancia));
				numeroDeSelecionados++;
			}
			if (acertos2.contains(true)){
				valorEstimado += Math.abs(regressor2.classifyInstance(instancia));
				numeroDeSelecionados++;
			}
			if (acertos3.contains(true)){
				valorEstimado += Math.abs(regressor3.classifyInstance(instancia));
				numeroDeSelecionados++;
			}
			
			if(acertos1.contains(true) || acertos2.contains(true) || acertos3.contains(true)){
			
				valorEstimado /= numeroDeSelecionados;
				
			}else{
				valorEstimado = Math.abs(regressor1.classifyInstance(instancia));
				valorEstimado += Math.abs(regressor2.classifyInstance(instancia));
				valorEstimado += Math.abs(regressor3.classifyInstance(instancia));

				valorEstimado /= 3;
			}
		}
		
		
		if(tipoSelecaoDinamica == TipoMetodoCombinacao.KNORA_E){
		
			int max = 7;
			numeroDeSelecionados = 0;

			boolean isRegressor1 = false;
			boolean isRegressor2 = false;
			boolean isRegressor3 = false;
	
			double erro1 = 0;
			double erro2 = 0;
			double erro3 = 0;

			for(int j = 0; j < instanciasK.numInstances(); j++){
				
				instanciaAtual = instanciasK.instance(j);
				valorReal = instanciaAtual.classValue();
				
				
				valorEstimado1 = Math.abs(regressor1.classifyInstance(instanciasK.instance(j)));
				
				if(Utilidade.METRICA_AVALIACAO.equals(Constantes.MAR)){
					erro1 += (Math.abs(valorReal - valorEstimado1));
					if (erro1 < padrao.getKnoraMediaMar()){
						acertos1.add(true);
					}else{
						acertos1.add(false);
					}

				}
				if(Utilidade.METRICA_AVALIACAO.equals(Constantes.MARLOG)){
					erro1 += Math.log10((Math.abs(valorReal - valorEstimado1)));
					if (erro1 < padrao.getKnoraMediaMarlog()){
						acertos1.add(true);
					}else{
						acertos1.add(false);
					}
				}
				if(Utilidade.METRICA_AVALIACAO.equals(Constantes.MREAJUSTADO)){
					erro1 += ((Math.abs(valorReal - valorEstimado1) / valorReal) + (Math.abs(valorReal - valorEstimado1) / valorEstimado1)) / 2;
					if (erro1 < padrao.getKnoraMediaMreajustado()){
						acertos1.add(true);
					}else{
						acertos1.add(false);
					}
				}
	
				valorEstimado2 = Math.abs(regressor2.classifyInstance(instanciasK.instance(j)));
				
				if(Utilidade.METRICA_AVALIACAO.equals(Constantes.MAR)){
					erro2 += (Math.abs(valorReal - valorEstimado2));
					if (erro2 < padrao.getKnoraMediaMar()){
						acertos2.add(true);
					}else{
						acertos2.add(false);
					}
				}
				if(Utilidade.METRICA_AVALIACAO.equals(Constantes.MARLOG)){
					erro2 += Math.log10((Math.abs(valorReal - valorEstimado2)));
					if (erro2 < padrao.getKnoraMediaMarlog()){
						acertos2.add(true);
					}else{
						acertos2.add(false);
					}
				}
				if(Utilidade.METRICA_AVALIACAO.equals(Constantes.MREAJUSTADO)){
					erro2 += ((Math.abs(valorReal - valorEstimado2) / valorReal) + (Math.abs(valorReal - valorEstimado2) / valorEstimado2)) / 2;
					if (erro2 < padrao.getKnoraMediaMreajustado()){
						acertos2.add(true);
					}else{
						acertos2.add(false);
					}
				}
	
				valorEstimado3 = Math.abs(regressor3.classifyInstance(instanciasK.instance(j)));
				if(Utilidade.METRICA_AVALIACAO.equals(Constantes.MAR)){
					erro3 += (Math.abs(valorReal - valorEstimado3));
					if (erro3 < padrao.getKnoraMediaMar()){
						acertos3.add(true);
					}else{
						acertos3.add(false);
					}
				}
				if(Utilidade.METRICA_AVALIACAO.equals(Constantes.MARLOG)){
					erro3 += Math.log10((Math.abs(valorReal - valorEstimado3)));
					if (erro3 < padrao.getKnoraMediaMarlog()){
						acertos3.add(true);
					}else{
						acertos3.add(false);
					}
				}
				if(Utilidade.METRICA_AVALIACAO.equals(Constantes.MREAJUSTADO)){
					erro3 += ((Math.abs(valorReal - valorEstimado3) / valorReal) + (Math.abs(valorReal - valorEstimado3) / valorEstimado3)) / 2;
					if (erro3 < padrao.getKnoraMediaMreajustado()){
						acertos3.add(true);
					}else{
						acertos3.add(false);
					}
				}
	
				
		
			}
	
			do {
				
				for (int indice = 0; indice < max; indice++) {
					if (acertos1.get(indice)){
						isRegressor1 = true;
					}else{
						isRegressor1 = false;
						break;
					}
				}
	
				for (int indice = 0; indice < max; indice++) {
					if (acertos2.get(indice)){
						isRegressor2 = true;
					}else{
						isRegressor2 = false;
						break;
					}
				}
	
				for (int indice = 0; indice < max; indice++) {
					if (acertos3.get(indice)){
						isRegressor3 = true;
					}else{
						isRegressor3 = false;
						break;
					}
				}
	
				if(isRegressor1 || isRegressor2 || isRegressor3){
					break;
				}
				max--;
				
			} while (max >= 0);
			
			if (isRegressor1){
				valorEstimado += Math.abs(regressor1.classifyInstance(instancia));
				numeroDeSelecionados++;
			}
			
			if(isRegressor2){
				valorEstimado += Math.abs(regressor2.classifyInstance(instancia));
				numeroDeSelecionados++;
			}
			
			if(isRegressor3){
				valorEstimado += Math.abs(regressor3.classifyInstance(instancia));
				numeroDeSelecionados++;
			}
			
			valorEstimado /= numeroDeSelecionados;
			
			
			if(isRegressor1 == false && isRegressor2 == false && isRegressor3 == false){
				valorEstimado1 = Math.abs(regressor1.classifyInstance(instancia));
				valorEstimado2 = Math.abs(regressor2.classifyInstance(instancia));
				valorEstimado3 = Math.abs(regressor3.classifyInstance(instancia));
				
				valorEstimado = valorEstimado1 + valorEstimado2 + valorEstimado3;
				valorEstimado /= 3;
			}
		
		}


		

		return valorEstimado;

	}
	
	@Override
	public void run(Padrao padrao, TipoMetodoCombinacao tipoSelecaoDinamica, TipoValidacao tipoValidacao) {

		try {
			
			File arquivo = null;
			Utilidade.adicionaCabecalhoDados(padrao);
			
			if(tipoValidacao == TipoValidacao.HOLD_OUT){
			
				arquivo = new File(Constantes.CAMINHO_PADRAO + Utilidade.getCaminhoTreinamento("CONSTANTE", padrao, 1));
				
				if(!arquivo.exists()){
					GeradorAleatorioDados.gerarTreinamentoValidacaoTeste("CONSTANTE", padrao);
				}
	
				for(int i = 1; i <= 50; i++){
				
				//treina o regressor1
				RegressorIndividualGeral regressor1 = new RegressorIndividualGeral(this.regressor1);
				regressor1.criarModelo(Utilidade.getCaminhoTreinamento("CONSTANTE", padrao, i));
	
				//treina o regressor2
				RegressorIndividualGeral regressor2 = new RegressorIndividualGeral(this.regressor2);
				regressor2.criarModelo(Utilidade.getCaminhoTreinamento("CONSTANTE", padrao, i));
	
				//treina o regressor3
				RegressorIndividualGeral regressor3 = new RegressorIndividualGeral(this.regressor3);
				regressor3.criarModelo(Utilidade.getCaminhoTreinamento("CONSTANTE", padrao, i));
				
				
				//carrega a lista de projetos que foram testados
				//setListaProjetos(Padrao.converteInstanciasParaProjetos(padrao, Utilidade.getCaminhoTreinamento(getNomeAlgoritmoModeloEnsemble(tipoSelecaoDinamica), padrao, i)));
				
				//adiciona os campos melhor algoritimo e menor erro a lista de projetos
				//adicionarCamposListaProjetosAvaliados(padrao, lms.getResultado(), m5r.getResultado(), svmr.getResultado(), getNomeAlgoritmoModeloEnsemble(tipoSelecaoDinamica), tipoValidacao, i);
				
				//avalia o modelo combinado
				avaliarMetodo(padrao, regressor1.getResultado(), regressor2.getResultado(), regressor3.getResultado(), tipoSelecaoDinamica, tipoValidacao, i);
				
				}
			}

			if(tipoValidacao == TipoValidacao.LEAVE_ONE_OUT){
				
				arquivo = new File(Constantes.CAMINHO_PADRAO + Utilidade.getCaminhoTreinamentoLeaveOneOut("CONSTANTE", padrao, 1));
				
				if(!arquivo.exists()){
					GeradorAleatorioDados.gerarTreinamentoValidacaoTesteLeaveOneOut("CONSTANTE", padrao);
				}
	
				for(int i = 1; i <= padrao.getTamanhoBase(); i++){
				
				//treina o regressor1
				RegressorIndividualGeral regressor1 = new RegressorIndividualGeral(this.regressor1);
				regressor1.criarModelo(Utilidade.getCaminhoTreinamentoLeaveOneOut("CONSTANTE", padrao, i));
	
				//treina o regressor2
				RegressorIndividualGeral regressor2 = new RegressorIndividualGeral(this.regressor2);
				regressor2.criarModelo(Utilidade.getCaminhoTreinamentoLeaveOneOut("CONSTANTE", padrao, i));
	
				//treina o regressor3
				RegressorIndividualGeral regressor3 = new RegressorIndividualGeral(this.regressor3);
				regressor3.criarModelo(Utilidade.getCaminhoTreinamentoLeaveOneOut("CONSTANTE", padrao, i));
				
				
				//carrega a lista de projetos que foram testados
				//setListaProjetos(Padrao.converteInstanciasParaProjetos(padrao, Utilidade.getCaminhoTreinamento(getNomeAlgoritmoModeloEnsemble(tipoSelecaoDinamica), padrao, i)));
				
				//adiciona os campos melhor algoritimo e menor erro a lista de projetos
				//adicionarCamposListaProjetosAvaliados(padrao, lms.getResultado(), m5r.getResultado(), svmr.getResultado(), getNomeAlgoritmoModeloEnsemble(tipoSelecaoDinamica), tipoValidacao, i);
				
				//avalia o modelo combinado
				avaliarMetodo(padrao, regressor1.getResultado(), regressor2.getResultado(), regressor3.getResultado(), tipoSelecaoDinamica, tipoValidacao, i);
				
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
	public void run(Padrao padrao, TipoMetodoCombinacao tipoSelecaoDinamica) {

		try {
			
			File arquivo = null;
			Utilidade.adicionaCabecalhoDados(padrao);
			
			
			arquivo = new File(Constantes.CAMINHO_PADRAO + Utilidade.getCaminhoTreinamento("CONSTANTE", padrao, 1));
			
			if(!arquivo.exists()){
				GeradorAleatorioDados.gerarTreinamentoValidacaoTeste("CONSTANTE", padrao);
			}

			for(int i = 1; i <= 50; i++){
			
			//treina o regressor1
			RegressorIndividualGeral regressor1 = new RegressorIndividualGeral(this.regressor1);
			regressor1.criarModelo(Utilidade.getCaminhoTreinamento("CONSTANTE", padrao, i));

			//treina o regressor2
			RegressorIndividualGeral regressor2 = new RegressorIndividualGeral(this.regressor2);
			regressor2.criarModelo(Utilidade.getCaminhoTreinamento("CONSTANTE", padrao, i));

			//treina o regressor3
			RegressorIndividualGeral regressor3 = new RegressorIndividualGeral(this.regressor3);
			regressor3.criarModelo(Utilidade.getCaminhoTreinamento("CONSTANTE", padrao, i));
			
			
			//carrega a lista de projetos que foram testados
			//setListaProjetos(Padrao.converteInstanciasParaProjetos(padrao, Utilidade.getCaminhoTreinamento(getNomeAlgoritmoModeloEnsemble(tipoSelecaoDinamica), padrao, i)));
			
			//adiciona os campos melhor algoritimo e menor erro a lista de projetos
			//adicionarCamposListaProjetosAvaliados(padrao, lms.getResultado(), m5r.getResultado(), svmr.getResultado(), getNomeAlgoritmoModeloEnsemble(tipoSelecaoDinamica), tipoValidacao, i);
			
			//avalia o modelo combinado
			avaliarMetodo(padrao, regressor1.getResultado(), regressor2.getResultado(), regressor3.getResultado(), tipoSelecaoDinamica, TipoValidacao.HOLD_OUT,  i);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	@Override
	public String toString() {
		return "Knora_DCS_Set_Ensemble_Dynamic " + TesteKnoraDCSSetEnsembleDynamic.experimento;
	}


	@Override
	public void run(Padrao padrao, TipoValidacao tipoValidacao) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run(Padrao padrao, Integer tipoClassificador1,
			Integer tipoClassificador2, Integer tipoClassificador3,
			Integer tipoClassificador4, Integer tipoClassificador5,
			Integer tipoClassificador6, Integer tipoClassificador7,
			TipoMetodoCombinacao tipoSelecaoDinamica) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run(Padrao padrao, Integer tipoClassificador1,
			Integer tipoClassificador2, Integer tipoClassificador3,
			Integer tipoClassificador4, Integer tipoClassificador5,
			Integer tipoClassificador6, Integer tipoClassificador7,
			TipoMetodoCombinacao tipoSelecaoDinamica,
			TipoValidacao tipoValidacao) {
		// TODO Auto-generated method stub
		
	}
	
}

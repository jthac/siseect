package main;

import method.Set_Ensemble_Dynamic;
import util.Constantes;
import util.TipoMetodoCombinacao;
import util.TipoMetricaAvaliacao;
import util.TipoValidacao;
import util.Utilidade;
import util.WekaExperiment;
import weka.classifiers.Classifier;
import weka.classifiers.functions.SMOreg;
import weka.classifiers.meta.AdditiveRegression;
import weka.classifiers.meta.Bagging;
import weka.classifiers.meta.Stacking;
import data.PadraoMiyazaki94;

public class TesteExperimento {
	
	
	public static void main(String []args){
		

		Utilidade.METRICA_AVALIACAO = Constantes.MAR;
		Utilidade.ADICIONAR_RESULTADOS_INDIVIDUAIS = true;

		Constantes.BASE_VALIDACAO = false;
		Constantes.TIPO_METRICA_AVALIACAO = TipoMetricaAvaliacao.MAR;
		Constantes.IMPRIMIR_RESULTADO_INDIVIDUAL = false;
	

		String experimento = "Experimento";
		String avaliacao = "Teste";

		TesteEnsembleEstatico.experimento = experimento;
		TesteKnoraDCSSetEnsembleDynamic.experimento = experimento;
		
		Classifier regressor1 = new WekaExperiment().createClassifier(WekaExperiment.LEAST_MED_SQ);
		Classifier regressor2 = new WekaExperiment().createClassifier(WekaExperiment.SUPPORT_VECTOR_REGRESSION);
		Classifier regressor3 = new WekaExperiment().createClassifier(WekaExperiment.M5P);
		Classifier regressor4 = null;
		Classifier regressor5 = null;

		// Bagging
		Bagging bagging1 = new Bagging();
		Bagging bagging2 = new Bagging();
		Bagging bagging3 = new Bagging();
		
		AdditiveRegression additiveRegression1 = new AdditiveRegression();
		AdditiveRegression additiveRegression2 = new AdditiveRegression();
		AdditiveRegression additiveRegression3 = new AdditiveRegression();

		Stacking stacking = new Stacking();
		stacking.setMetaClassifier(new SMOreg());
		Classifier[] classifiers = new Classifier[3];
		classifiers[0] = regressor1;
		classifiers[1] = regressor2;
		classifiers[2] = regressor3;
		stacking.setClassifiers(classifiers);

		TesteIndividualGeral.regressor = regressor1;	
		TesteIndividualGeral.main(args);

		TesteIndividualGeral.regressor = regressor2;	
		TesteIndividualGeral.main(args);

		TesteIndividualGeral.regressor = regressor3;	
		TesteIndividualGeral.main(args);
				
		TesteEnsembleEstatico.regressor1 = regressor1;	
		TesteEnsembleEstatico.regressor2 = regressor2;	
		TesteEnsembleEstatico.regressor3 = regressor3;	
		TesteEnsembleEstatico.regressor4 = regressor4;	
		TesteEnsembleEstatico.regressor5 = regressor5;	
		
		TesteEnsembleEstatico.metodoDeCombinacao = TipoMetodoCombinacao.MEDIA;
		TesteEnsembleEstatico.main(args);

		TesteEnsembleEstatico.metodoDeCombinacao = TipoMetodoCombinacao.MEDIANA;
		TesteEnsembleEstatico.main(args);

		TesteEnsembleEstatico.metodoDeCombinacao = TipoMetodoCombinacao.MEDIA_DAS_PONTAS;
		TesteEnsembleEstatico.main(args);

		TesteEnsembleEstatico.metodoDeCombinacao = TipoMetodoCombinacao.MAXIMO;
		TesteEnsembleEstatico.main(args);

		TesteEnsembleEstatico.metodoDeCombinacao = TipoMetodoCombinacao.MINIMO;
		TesteEnsembleEstatico.main(args);

		bagging1.setClassifier(regressor1);
		TesteIndividualGeral.regressor = bagging1;	
		TesteIndividualGeral.main(args);

		bagging2.setClassifier(regressor2);
		TesteIndividualGeral.regressor = bagging2;	
		TesteIndividualGeral.main(args);

		bagging3.setClassifier(regressor3);
		TesteIndividualGeral.regressor = bagging3;	
		TesteIndividualGeral.main(args);

		additiveRegression1.setClassifier(regressor1);
		TesteIndividualGeral.regressor = additiveRegression1;	
		TesteIndividualGeral.main(args);

		additiveRegression2.setClassifier(regressor2);
		TesteIndividualGeral.regressor = additiveRegression2;	
		TesteIndividualGeral.main(args);

		additiveRegression3.setClassifier(regressor3);
		TesteIndividualGeral.regressor = additiveRegression3;	
		TesteIndividualGeral.main(args);
	
		TesteIndividualGeral.regressor = stacking;	
		TesteIndividualGeral.main(args);
		
		TesteKnoraDCSSetEnsembleDynamic.regressor1 = regressor1;	
		TesteKnoraDCSSetEnsembleDynamic.regressor2 = regressor2;	
		TesteKnoraDCSSetEnsembleDynamic.regressor3 = regressor3;	
		
		TesteKnoraDCSSetEnsembleDynamic.metodoDeSelecaoPorDisntacia = TipoMetodoCombinacao.DCS_LA;
		TesteKnoraDCSSetEnsembleDynamic.main(args);

		TesteKnoraDCSSetEnsembleDynamic.metodoDeSelecaoPorDisntacia = TipoMetodoCombinacao.DCS_LAW;
		TesteKnoraDCSSetEnsembleDynamic.main(args);

		TesteKnoraDCSSetEnsembleDynamic.metodoDeSelecaoPorDisntacia = TipoMetodoCombinacao.KNORA_E;
		TesteKnoraDCSSetEnsembleDynamic.main(args);

		TesteKnoraDCSSetEnsembleDynamic.metodoDeSelecaoPorDisntacia = TipoMetodoCombinacao.KNORA_U;
		TesteKnoraDCSSetEnsembleDynamic.main(args);


	runPeetacoDS(experimento, regressor1, regressor2, regressor3, avaliacao);

	Constantes.QUANTIDADE_CLASSIFICADOR = 2;		
	runPeetacoDES(experimento, regressor1, regressor2, regressor3, avaliacao, TipoMetodoCombinacao.MEDIA);
	
	Constantes.QUANTIDADE_CLASSIFICADOR = 3;		
	runPeetacoDES(experimento, regressor1, regressor2, regressor3, avaliacao, TipoMetodoCombinacao.MEDIA);

	Constantes.QUANTIDADE_CLASSIFICADOR = 4;		
	runPeetacoDES(experimento, regressor1, regressor2, regressor3, avaliacao, TipoMetodoCombinacao.MEDIA);
	runPeetacoDES(experimento, regressor1, regressor2, regressor3, avaliacao, TipoMetodoCombinacao.MEDIANA);
	runPeetacoDES(experimento, regressor1, regressor2, regressor3, avaliacao, TipoMetodoCombinacao.MEDIA_DAS_PONTAS);
	runPeetacoDES(experimento, regressor1, regressor2, regressor3, avaliacao, TipoMetodoCombinacao.MAXIMO);
	runPeetacoDES(experimento, regressor1, regressor2, regressor3, avaliacao, TipoMetodoCombinacao.MINIMO);

	Constantes.QUANTIDADE_CLASSIFICADOR = 5;		
	runPeetacoDES(experimento, regressor1, regressor2, regressor3, avaliacao, TipoMetodoCombinacao.MEDIA);
	runPeetacoDES(experimento, regressor1, regressor2, regressor3, avaliacao, TipoMetodoCombinacao.MEDIANA);
	runPeetacoDES(experimento, regressor1, regressor2, regressor3, avaliacao, TipoMetodoCombinacao.MEDIA_DAS_PONTAS);
	runPeetacoDES(experimento, regressor1, regressor2, regressor3, avaliacao, TipoMetodoCombinacao.MAXIMO);
	runPeetacoDES(experimento, regressor1, regressor2, regressor3, avaliacao, TipoMetodoCombinacao.MINIMO);

	

		
	}
	
	private static void runPeetacoDS(String experimento, Classifier regressor1,
			Classifier regressor2, Classifier regressor3, String avaliacao) {
		
		long inicio = System.currentTimeMillis();
		Constantes.QUANTIDADE_CLASSIFICADOR = 1;
		TipoMetodoCombinacao metodoDeCombinacao = TipoMetodoCombinacao.SD;
		
		Utilidade.inicializaListasMetricas();
		

		System.out.println("----- Teste com Essemble " +  experimento + " utilizando Novo Ensemble e o Padrão MIYAZAKI94 GERAL " + Constantes.QUANTIDADE_CLASSIFICADOR + "Classificadores");
		new Set_Ensemble_Dynamic(regressor1, regressor2, regressor3).run(new PadraoMiyazaki94(), null, null, null, null, null, null, WekaExperiment.KNN5,  metodoDeCombinacao, TipoValidacao.LEAVE_ONE_OUT);


		long tempo = System.currentTimeMillis() - inicio;
		
		Utilidade.gerarArquivosMassa(tempo, "SET_DYNAMIC_SELECTION" + experimento + "_" + metodoDeCombinacao + "_PEETACODS_" +  avaliacao);
	}


	private static void runPeetacoDES(String experimento, Classifier regressor1, Classifier regressor2, Classifier regressor3, String avaliacao, TipoMetodoCombinacao metodoDeCombinacao) {
		
		long inicio = System.currentTimeMillis();

		
		Utilidade.inicializaListasMetricas();
		

		System.out.println("----- Teste com Essemble " +  experimento + " utilizando Novo Ensemble e o Padrão MIYAZAKI94 GERAL " + Constantes.QUANTIDADE_CLASSIFICADOR + "Classificadores");
		new Set_Ensemble_Dynamic(regressor1, regressor2, regressor3).run(new PadraoMiyazaki94(), null, null, WekaExperiment.DECISION_TABLE, WekaExperiment.LOCALLY_WEIGHTED_LEARNING, WekaExperiment.KNN3, WekaExperiment.KNN7, WekaExperiment.KNN5,  metodoDeCombinacao, TipoValidacao.LEAVE_ONE_OUT);


		long tempo = System.currentTimeMillis() - inicio;
		
		Utilidade.gerarArquivosMassa(tempo, "SET_ENSEMBLE_DYNAMIC_" + experimento + "_" + metodoDeCombinacao + "_PEETACODES_" + Constantes.QUANTIDADE_CLASSIFICADOR + "CLASSIFICADORES" + avaliacao);
	}


}

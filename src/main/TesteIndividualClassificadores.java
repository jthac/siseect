package main;

import util.Constantes;
import util.TipoMetodoCombinacao;
import util.TipoMetricaAvaliacao;
import util.WekaExperiment;

public class TesteIndividualClassificadores {
	
	
	public static void main(String []args){
		
		Constantes.BASE_VALIDACAO = true;
		Constantes.IMPRIMIR_RESULTADO_INDIVIDUAL = false;
		Constantes.QUANTIDADE_CLASSIFICADOR = 1;		

		TesteSetEnsembleDynamicValidacao.regressor1 = new WekaExperiment().createClassifier(WekaExperiment.LEAST_MED_SQ);
		TesteSetEnsembleDynamicValidacao.regressor2 = new WekaExperiment().createClassifier(WekaExperiment.SUPPORT_VECTOR_REGRESSION);
		TesteSetEnsembleDynamicValidacao.regressor3 = new WekaExperiment().createClassifier(WekaExperiment.M5P);
		TesteSetEnsembleDynamicValidacao.regressor4 = new WekaExperiment().createClassifier(WekaExperiment.MLP2);
		
		TesteSetEnsembleDynamicValidacao.experimento = "Experimento 06";
		TesteSetEnsembleDynamicValidacao.metodoDeCombinacao = TipoMetodoCombinacao.SD;
		Constantes.TIPO_METRICA_AVALIACAO = TipoMetricaAvaliacao.MAR;
		
		TesteSetEnsembleDynamicValidacao.classificador = (WekaExperiment.ADABOOST);	
		TesteSetEnsembleDynamicValidacao.main(args);
		TesteSetEnsembleDynamicValidacao.classificador = (WekaExperiment.DECISION_TABLE);	
		TesteSetEnsembleDynamicValidacao.main(args);
		TesteSetEnsembleDynamicValidacao.classificador = (WekaExperiment.NAIVE_BAYES);	
		TesteSetEnsembleDynamicValidacao.main(args);
		TesteSetEnsembleDynamicValidacao.classificador = (WekaExperiment.J48);	
		TesteSetEnsembleDynamicValidacao.main(args);
		TesteSetEnsembleDynamicValidacao.classificador = (WekaExperiment.LOGISTIC_REGRESSION);	
		TesteSetEnsembleDynamicValidacao.main(args);
		TesteSetEnsembleDynamicValidacao.classificador = (WekaExperiment.KNN3);	
		TesteSetEnsembleDynamicValidacao.main(args);
		TesteSetEnsembleDynamicValidacao.classificador = (WekaExperiment.KNN5);	
		TesteSetEnsembleDynamicValidacao.main(args);
		TesteSetEnsembleDynamicValidacao.classificador = (WekaExperiment.KNN7);	
		TesteSetEnsembleDynamicValidacao.main(args);
		TesteSetEnsembleDynamicValidacao.classificador = (WekaExperiment.MLP1);	
		TesteSetEnsembleDynamicValidacao.main(args);
		TesteSetEnsembleDynamicValidacao.classificador = (WekaExperiment.BAGGING);	
		TesteSetEnsembleDynamicValidacao.main(args);
		TesteSetEnsembleDynamicValidacao.classificador = (WekaExperiment.KSTAR);	
		TesteSetEnsembleDynamicValidacao.main(args);
		TesteSetEnsembleDynamicValidacao.classificador = (WekaExperiment.DECISION_STUMP);	
		TesteSetEnsembleDynamicValidacao.main(args);
		TesteSetEnsembleDynamicValidacao.classificador = (WekaExperiment.RANDOM_FOREST);	
		TesteSetEnsembleDynamicValidacao.main(args);
		TesteSetEnsembleDynamicValidacao.classificador = (WekaExperiment.REP_TREE);	
		TesteSetEnsembleDynamicValidacao.main(args);
		TesteSetEnsembleDynamicValidacao.classificador = (WekaExperiment.ONER);	
		TesteSetEnsembleDynamicValidacao.main(args);
		TesteSetEnsembleDynamicValidacao.classificador = (WekaExperiment.RBF_NETWORK);	
		TesteSetEnsembleDynamicValidacao.main(args);
		TesteSetEnsembleDynamicValidacao.classificador = (WekaExperiment.SVM);	
		TesteSetEnsembleDynamicValidacao.main(args);
		TesteSetEnsembleDynamicValidacao.classificador = (WekaExperiment.LOCALLY_WEIGHTED_LEARNING);	
		TesteSetEnsembleDynamicValidacao.main(args);
		TesteSetEnsembleDynamicValidacao.classificador = (WekaExperiment.BEST_FIRST_TREE);	
		TesteSetEnsembleDynamicValidacao.main(args);
		TesteSetEnsembleDynamicValidacao.classificador = (WekaExperiment.BAYES_NET);	
		TesteSetEnsembleDynamicValidacao.main(args);
		TesteSetEnsembleDynamicValidacao.classificador = (WekaExperiment.J_RIP);	
		TesteSetEnsembleDynamicValidacao.main(args);
		TesteSetEnsembleDynamicValidacao.classificador = (WekaExperiment.LMT);	
		TesteSetEnsembleDynamicValidacao.main(args);
		TesteSetEnsembleDynamicValidacao.classificador = (WekaExperiment.LAD_TREE);	
		TesteSetEnsembleDynamicValidacao.main(args);
		
}

}

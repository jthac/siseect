package main;

import method.Set_Ensemble_Dynamic;
import util.Constantes;
import util.TipoMetodoCombinacao;
import util.TipoMetricaAvaliacao;
import util.TipoValidacao;
import util.Utilidade;
import util.WekaExperiment;
import weka.classifiers.Classifier;
import data.PadraoMiyazaki94;

public class TesteExperimentoNucci {
	
	
	public static void main(String []args){
	
		String experimento = "Experimento_NUCCI_MAR";
		String avaliacao = "Teste";

		Constantes.BASE_VALIDACAO = false;
	
		Utilidade.METRICA_AVALIACAO = Constantes.MAR;
		Constantes.TIPO_METRICA_AVALIACAO = TipoMetricaAvaliacao.MAR;
		
		Constantes.QUANTIDADE_CLASSIFICADOR = 1;

		Classifier regressor1 = new WekaExperiment().createClassifier(WekaExperiment.LEAST_MED_SQ);
		Classifier regressor2 = new WekaExperiment().createClassifier(WekaExperiment.SUPPORT_VECTOR_REGRESSION);
		Classifier regressor3 = new WekaExperiment().createClassifier(WekaExperiment.M5P);



		TipoMetodoCombinacao metodoDeCombinacao = TipoMetodoCombinacao.SD;
		
		Utilidade.inicializaListasMetricas();
		

		new Set_Ensemble_Dynamic(regressor1, regressor2, regressor3).run(new PadraoMiyazaki94(), null, null, null, null, null, null, WekaExperiment.RANDOM_FOREST,  metodoDeCombinacao, TipoValidacao.LEAVE_ONE_OUT);


		Utilidade.gerarArquivosMassa(0, "SET_DYNAMIC_SELECTION_RANDOM_FOREST" + experimento + "_" + metodoDeCombinacao + "_NUCCI_" +  avaliacao);

		
		
	}
	

	

}

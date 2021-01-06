package main;
import method.EnsembleEstaticoGeral;
import util.Constantes;
import util.TipoMetodoCombinacao;
import util.TipoValidacao;
import util.Utilidade;
import util.WekaExperiment;
import weka.classifiers.Classifier;
import data.PadraoMiyazaki94;

public class TesteEnsembleEstatico {

	public static TipoMetodoCombinacao metodoDeCombinacao;
	public static Classifier regressor1, regressor2, regressor3, regressor4, regressor5;
	public static String experimento;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// Experiment name
		experimento = "Myazaki94";
		
		Constantes.BASE_VALIDACAO = false;
		
		Utilidade.inicializaListasMetricas();

		metodoDeCombinacao = TipoMetodoCombinacao.MEDIA;
		
		regressor1 = new WekaExperiment().createClassifier(WekaExperiment.LEAST_MED_SQ);	
		regressor2 = new WekaExperiment().createClassifier(WekaExperiment.SUPPORT_VECTOR_REGRESSION);	
		regressor3 = new WekaExperiment().createClassifier(WekaExperiment.M5P);	


		new EnsembleEstaticoGeral(regressor1, regressor2, regressor3, regressor4, regressor5).run(new PadraoMiyazaki94(), metodoDeCombinacao, TipoValidacao.LEAVE_ONE_OUT);
		
		Utilidade.gerarArquivosMassa(0, "ENSEMBLE_" + metodoDeCombinacao + experimento);

	}

}

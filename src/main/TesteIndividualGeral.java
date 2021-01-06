package main;
import method.RegressorIndividualGeral;
import util.Constantes;
import util.TipoValidacao;
import util.Utilidade;
import util.WekaExperiment;
import weka.classifiers.Classifier;
import weka.classifiers.meta.AdditiveRegression;
import weka.classifiers.meta.Bagging;
import data.PadraoMiyazaki94;

public class TesteIndividualGeral {

	/**
	 * @param args
	 */
	
	public static Classifier regressor;
	
	
	
	public static void main(String[] args) {
		

		TesteIndividualGeral.regressor = new WekaExperiment().createClassifier(WekaExperiment.SUPPORT_VECTOR_REGRESSION);	
		
		Constantes.BASE_VALIDACAO = false;
	
		Utilidade.inicializaListasMetricas();
		
		
		new RegressorIndividualGeral(regressor).run(new PadraoMiyazaki94(), TipoValidacao.LEAVE_ONE_OUT);


		// get the full name of regressor if the regressor is bagging or boosting type
		String regressorBasico = "";
		if(regressor instanceof Bagging){
			Bagging bagging = (Bagging) regressor;
			regressorBasico = bagging.getClassifier().getClass().getName();
		}
		if(regressor instanceof AdditiveRegression){
			AdditiveRegression additiveRegression = (AdditiveRegression) regressor;
			regressorBasico = additiveRegression.getClassifier().getClass().getName();
		}

		// setting the string variable to make the name of file  
		String avaliacao;
		if(Constantes.BASE_VALIDACAO){
			avaliacao = "Validação";
		}else{
			avaliacao = "Teste";
		}
		
		// generate the file with results
		Utilidade.gerarArquivosMassa(0, regressor.getClass().getName() + regressorBasico + "_" + avaliacao + "_");
	}


}

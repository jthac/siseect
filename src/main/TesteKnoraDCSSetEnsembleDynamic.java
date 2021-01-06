package main;
import method.Knora_DCS_Set_Ensemble_Dynamic;
import util.Constantes;
import util.TipoMetodoCombinacao;
import util.TipoValidacao;
import util.Utilidade;
import util.WekaExperiment;
import weka.classifiers.Classifier;
import data.PadraoMiyazaki94;

public class TesteKnoraDCSSetEnsembleDynamic {

	public static TipoMetodoCombinacao metodoDeSelecaoPorDisntacia;
	public static Classifier regressor1, regressor2, regressor3;
	public static String experimento;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

			
			try {
				
		
					
				// Experiment name
				experimento = "Myazaki94";
				
				Constantes.BASE_VALIDACAO = false;
				
				metodoDeSelecaoPorDisntacia = TipoMetodoCombinacao.DCS_LA;
				Utilidade.METRICA_AVALIACAO = Constantes.MAR; 
				
				regressor1 = new WekaExperiment().createClassifier(WekaExperiment.LEAST_MED_SQ);	
				regressor2 = new WekaExperiment().createClassifier(WekaExperiment.SUPPORT_VECTOR_REGRESSION);	
				regressor3 = new WekaExperiment().createClassifier(WekaExperiment.M5P);	
					

				new Knora_DCS_Set_Ensemble_Dynamic(regressor1,regressor2,regressor3).run(new PadraoMiyazaki94(), metodoDeSelecaoPorDisntacia, TipoValidacao.LEAVE_ONE_OUT);
			
				Utilidade.gerarArquivosMassa(0, "DYNAMIC_SELECTION_" + experimento + metodoDeSelecaoPorDisntacia);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
				e.printStackTrace();
			}

	}
		

}

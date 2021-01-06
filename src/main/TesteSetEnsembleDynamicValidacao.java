package main;
import method.Set_Ensemble_Dynamic;
import util.Constantes;
import util.TipoMetodoCombinacao;
import util.TipoValidacao;
import util.Utilidade;
import util.WekaExperiment;
import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;
import data.PadraoMiyazaki94;

public class TesteSetEnsembleDynamicValidacao {

	
	public static int classificador;
	public static String experimento;
	public static TipoMetodoCombinacao metodoDeCombinacao;
	public static Classifier regressor1;
	public static Classifier regressor2;
	public static Classifier regressor3;
	public static Classifier regressor4;

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {


		try {
			
			
			
				Utilidade.inicializaListasMetricas();
				

				new Set_Ensemble_Dynamic(regressor1, regressor2, regressor3).run(new PadraoMiyazaki94(), null, null, null, null, null, null, classificador,  metodoDeCombinacao, TipoValidacao.LEAVE_ONE_OUT);
	
				

				String avaliacao;
				if(Constantes.BASE_VALIDACAO){
					avaliacao = "Validação";
				}else{
					avaliacao = "Teste";
				}
				
				Classifier classifier = new WekaExperiment().createClassifier(classificador);
				String nomeDoClassificador;
				if(classifier instanceof IBk){
					IBk ibk = (IBk) classifier;
					nomeDoClassificador = classifier.getClass().getName() + ibk.getKNN(); 
				}else{
					nomeDoClassificador = classifier.getClass().getName();
				}
				
				Utilidade.gerarArquivosMassa(0, "SetEnsembleDynamic" + experimento + "_" + metodoDeCombinacao + "_" + nomeDoClassificador +  "_" +  avaliacao);

	


		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

	}

}

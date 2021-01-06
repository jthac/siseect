package method;

import java.io.File;

import model.Resultado;
import util.Constantes;
import util.GeradorAleatorioDados;
import util.TipoValidacao;
import util.Utilidade;
import weka.classifiers.lazy.IBk;
import data.Padrao;

public class KNN_Knora extends Tecnica {

	public static final String KNN3 = "KNN3";
	public static final String KNN5 = "KNN5";
	public static final String KNN7 = "KNN7";
	public static final String KNN = "KNN";
	private IBk ibk;

	private int k;
	
	public KNN_Knora() {

	}

	public KNN_Knora(int k) {
		this.k = k;
	}

	public void criarModelo(Integer k, String nomeArquivoTreino) throws Exception {

		configurarDados(nomeArquivoTreino);
		IBk ibk = new IBk(k);
		ibk.buildClassifier(instancias);
		
		
//		we = new WekaExperiment();
//
//		we.setTrainingData(instancias);
//
//		// o buildClassifier é feito aqui dentro.
//		switch (k) {
//		case 1:
//			we.KFoldCrossValidationStratified(Constantes.FOLD, 1, WekaExperiment.KNN1); 
//			break;
//		case 3: 
//			we.KFoldCrossValidationStratified(Constantes.FOLD, 1, WekaExperiment.KNN3);
//			break;
//		case 5: 
//			we.KFoldCrossValidationStratified(Constantes.FOLD, 1, WekaExperiment.KNN5);
//			break;
//		case 7: 
//			we.KFoldCrossValidationStratified(Constantes.FOLD, 1, WekaExperiment.KNN7);
//			break;
//		default:
//			we.KFoldCrossValidationStratified(Constantes.FOLD, 1, WekaExperiment.KNN1); 
//			break;
//		}
//		
//		// retorna o classificador gerado
//		ibk = (IBk) we.getClassifier();

		//gerarModelo(ibk, nomeArquivoTreino.replace("/", "_").replace(".arff", ""));
		gerarModelo(ibk, KNN + k);

	}

	public void usarModelo(String nomeArquivoTeste) throws Exception {

		ibk = (IBk) recuperarModelo();
		configurarDados(nomeArquivoTeste);
		
		resultado = new Resultado();
		resultado.avaliarModelo(ibk, instancias, nomeModeloCriado);
	}

	
	public void criarModeloKNORA(Integer k, String nomeArquivoTreino) throws Exception {

		configurarDados(nomeArquivoTreino);
		IBk ibk = new IBk(k);
		ibk.buildClassifier(instancias);

//		we = new WekaExperiment();
//
//		we.setTrainingData(instancias);
//
//		// o buildClassifier é feito aqui dentro.
//		switch (k) {
//		case 1:
//			we.KFoldCrossValidationStratified(Constantes.FOLD, 1, WekaExperiment.KNN1); 
//			break;
//		case 3: 
//			we.KFoldCrossValidationStratified(Constantes.FOLD, 1, WekaExperiment.KNN3);
//			break;
//		case 5: 
//			we.KFoldCrossValidationStratified(Constantes.FOLD, 1, WekaExperiment.KNN5);
//			break;
//		case 7: 
//			we.KFoldCrossValidationStratified(Constantes.FOLD, 1, WekaExperiment.KNN7);
//			break;
//		default:
//			we.KFoldCrossValidationStratified(Constantes.FOLD, 1, WekaExperiment.KNN1); 
//			break;
//		}
//		
//		// retorna o classificador gerado
//		ibk = (IBk) we.getClassifier();

		//gerarModelo(ibk, nomeArquivoTreino.replace("/", "_").replace(".arff", ""));
		gerarModelo(ibk, "KNORA_" + k);

	}

	public IBk getIbk() {
		return ibk;
	}

	public void setIbk(IBk ibk) {
		this.ibk = ibk;
	}

	public static String getKnn() {
		return KNN;
	}

	public void run(String treino, String validacao, Integer k) {
		try {
			
			criarModelo(k, treino);
			usarModelo(validacao);
			
		} catch (Exception e) {

			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void run(Padrao padrao, TipoValidacao tipoValidacao) {
		try {
			
			File arquivo = null;
			Utilidade.adicionaCabecalhoDados(padrao);
			
			if(tipoValidacao == TipoValidacao.HOLD_OUT){
				
				arquivo = new File(Constantes.CAMINHO_PADRAO + Utilidade.getCaminhoTreinamento("CONSTANTE", padrao, 1));

				if(!arquivo.exists()){
					GeradorAleatorioDados.gerarTreinamentoValidacaoTeste("CONSTANTE", padrao);
				}
				
				for(int i = 1; i <= 50; i++){
					KNN_Knora knn = new KNN_Knora();
					knn.criarModelo(k, Utilidade.getCaminhoTreinamento("CONSTANTE",padrao, i));
					if(Constantes.BASE_VALIDACAO){
						knn.usarModelo(Utilidade.getCaminhoValidacao("CONSTANTE", padrao, i));
					}else{
						knn.usarModelo(Utilidade.getCaminhoTeste("CONSTANTE", padrao, i));						
					}
				}
			}
			
			if(tipoValidacao == TipoValidacao.LEAVE_ONE_OUT){

				arquivo = new File(Constantes.CAMINHO_PADRAO + Utilidade.getCaminhoTreinamentoLeaveOneOut("CONSTANTE", padrao, 1));

				if(!arquivo.exists()){
					GeradorAleatorioDados.gerarTreinamentoValidacaoTesteLeaveOneOut("CONSTANTE", padrao);
				}

				for(int i = 1; i <= padrao.getTamanhoBase(); i++){
					KNN_Knora knn = new KNN_Knora();
					knn.criarModelo(k, Utilidade.getCaminhoTreinamentoLeaveOneOut("CONSTANTE",padrao, i));
					if(Constantes.BASE_VALIDACAO){
						knn.usarModelo(Utilidade.getCaminhoValidacaoLeaveOneOut("CONSTANTE", padrao, i));
					}else{
						knn.usarModelo(Utilidade.getCaminhoTesteLeaveOneOut("CONSTANTE", padrao, i));
					}
				}

			}
			
			Utilidade.calculaMediasMetricas();
			Utilidade.zerarMediasAuxiliares();
			Utilidade.adicionaLinhaArquivo();
			 
			
		} catch (Exception e) {

			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void run(Padrao padrao) {
		File arquivo = null;
		try{
			
				
			Utilidade.adicionaCabecalhoDados(padrao);
			arquivo = new File(Constantes.CAMINHO_PADRAO + Utilidade.getCaminhoTreinamento("CONSTANTE", padrao, 1));

			if(!arquivo.exists()){
				GeradorAleatorioDados.gerarTreinamentoValidacaoTeste("CONSTANTE", padrao);
			}
			
			for(int i = 1; i <= 50; i++){
				KNN_Knora knn = new KNN_Knora();
				knn.criarModelo(k, Utilidade.getCaminhoTreinamento("CONSTANTE",padrao, i));
				knn.usarModelo(Utilidade.getCaminhoValidacao("CONSTANTE", padrao, i));
			}
	
			Utilidade.calculaMediasMetricas();
			Utilidade.zerarMediasAuxiliares();
			Utilidade.adicionaLinhaArquivo();

		}catch (Exception e) {

			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void runKNORA(String treino, String validacao, Integer k) {
		try {
			
			criarModeloKNORA(k, treino);
			usarModelo(validacao);
			
		} catch (Exception e) {

			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	

	
}

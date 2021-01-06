package method;

import java.io.File;

import model.Resultado;
import util.Constantes;
import util.GeradorAleatorioDados;
import util.TipoValidacao;
import util.Utilidade;
import weka.classifiers.Classifier;
import data.Padrao;

public class RegressorIndividualGeral extends Tecnica {

	private Classifier regressor;
	
	public RegressorIndividualGeral(Classifier regressor) {
		this.regressor = regressor;
	}

	public void criarModelo(String nomeArquivoTreino) throws Exception {

		configurarDados(nomeArquivoTreino);
		regressor.buildClassifier(instancias);
		gerarModelo(regressor, regressor.getClass().getName());

	}

	public void usarModelo(String nomeArquivoTeste) throws Exception {

		regressor = recuperarModelo();
		configurarDados(nomeArquivoTeste);
		
		resultado = new Resultado();
		resultado.avaliarModelo(regressor, instancias, nomeModeloCriado);
	}

	
	public void run(String treino, String validacao) {
		try {
			
			criarModelo(treino);
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
					criarModelo(Utilidade.getCaminhoTreinamento("CONSTANTE",padrao, i));
					if(Constantes.BASE_VALIDACAO){
						usarModelo(Utilidade.getCaminhoValidacao("CONSTANTE", padrao, i));
					}else{
						usarModelo(Utilidade.getCaminhoTeste("CONSTANTE", padrao, i));						
					}
				}
			}
			
			if(tipoValidacao == TipoValidacao.LEAVE_ONE_OUT){

				arquivo = new File(Constantes.CAMINHO_PADRAO + Utilidade.getCaminhoTreinamentoLeaveOneOut("CONSTANTE", padrao, 1));

				if(arquivo == null || !arquivo.exists()){
					GeradorAleatorioDados.gerarTreinamentoValidacaoTesteLeaveOneOut("CONSTANTE", padrao);
				}

				System.out.println("BASE DE VALIDAÇÃO: " + Constantes.BASE_VALIDACAO);

				for(int i = 1; i <= padrao.getTamanhoBase(); i++){
					criarModelo(Utilidade.getCaminhoTreinamentoLeaveOneOut("CONSTANTE",padrao, i));
					if(Constantes.BASE_VALIDACAO){
						usarModelo(Utilidade.getCaminhoValidacaoLeaveOneOut("CONSTANTE", padrao, i));
					}else{
						usarModelo(Utilidade.getCaminhoTesteLeaveOneOut("CONSTANTE", padrao, i));
					}
				}

			}
			
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
			
				
			arquivo = new File(Constantes.CAMINHO_PADRAO + Utilidade.getCaminhoTreinamento("CONSTANTE", padrao, 1));

			if(!arquivo.exists()){
				GeradorAleatorioDados.gerarTreinamentoValidacaoTeste("CONSTANTE", padrao);
			}
			
			for(int i = 1; i <= 50; i++){
				criarModelo(Utilidade.getCaminhoTreinamento("CONSTANTE",padrao, i));
				if(Constantes.BASE_VALIDACAO){
					usarModelo(Utilidade.getCaminhoValidacao("CONSTANTE", padrao, i));
				}else{
					usarModelo(Utilidade.getCaminhoTeste("CONSTANTE", padrao, i));						
				}
			}
	
			Utilidade.calculaMediasMetricas();
			Utilidade.zerarMediasAuxiliares();
			Utilidade.adicionaLinhaArquivo();

		}catch (Exception e) {

			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
}

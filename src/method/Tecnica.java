package method;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.Projeto;
import model.Resultado;
import util.Constantes;
import util.GeradorAleatorioDados;
import util.TipoMetodoCombinacao;
import util.TipoMetricaAvaliacao;
import util.TipoValidacao;
import util.Utilidade;
import util.WekaExperiment;
import weka.classifiers.Classifier;
import weka.core.Instances;
import data.Padrao;

public abstract class Tecnica {

	protected String nomeModeloCriado;
	protected String caminhoArquivo;
	protected FileReader reader;
	protected Instances instancias;
	protected WekaExperiment we;
	protected Resultado resultado;
	// instâncias da base classificada com o melhor algoritimo
	protected Instances instanciasClassificadas;

	// instancias da base sem a classificação de melhor algoritimo as quais
	// serão utilizadas pelo classificador
	protected Instances instanciasSemClassificacao;

	// classificador de melhor algoritimo
	protected Classifier classificador;
	
	List<Projeto> listaProjetos = new ArrayList<Projeto>();
	List<Projeto> listaProjetosTeste = new ArrayList<Projeto>();
	List<Projeto> listaProjetosValidacao = new ArrayList<Projeto>();
	
	protected void configurarDados(String nomeArquivo)
			throws Exception {

		// Usaremos a base definida no parâmetro caminho
		caminhoArquivo = Constantes.CAMINHO_PADRAO + nomeArquivo;
		reader = new FileReader(caminhoArquivo);
		instancias = new Instances(reader);

		// Inicialmente os atributos serão fixos

		// configura o rótulo
		instancias.setClassIndex(instancias.numAttributes() - 1);
		
	}

	protected void gerarModelo(Classifier classificador,
			String nomeArquivoTreino) throws IOException, FileNotFoundException {

		// serializando o modelo, por enquanto estamos criando o modelo, depois
		// essa parte deve ser desnecessária
		//nomeModeloCriado = nomeArquivoTreino.replace("_TREINO", "") + ".ser";
		resultado = new Resultado();
		resultado.setNomeModelo(nomeArquivoTreino);
		nomeModeloCriado = nomeArquivoTreino;
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
				nomeModeloCriado));
		oos.writeObject(classificador);
		oos.close();
	}

	protected Classifier recuperarModelo() throws IOException, FileNotFoundException,
			ClassNotFoundException {
		// Recuperamos o modelo criado pelo o KNN com k igual k
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
				nomeModeloCriado));
		Classifier classificador = (Classifier) ois.readObject();
		ois.close();
		return classificador;
	}
	
	

	

	

	
	
	protected void imprimirResultados() throws Exception{
		
		resultado.calculaErroAbsoluto();
		resultado.calculaTaxaDeAcertoDoClassificador();
		System.out.println(resultado.toStringSimples());
	}
	protected void imprimirResultadosBasicos() throws Exception{
		
		resultado.calculaErroAbsoluto();
		System.out.println(resultado.toStringSimples());
	}	
	
	protected void calcularResultadosGerais() {
	
		resultado.calculaErroAbsoluto();
		resultado.calculaMarLog();
		resultado.calculaMREAjustado();
		
	}

	protected void adicionarCamposListaProjetosAvaliados(Padrao padrao, Resultado resultado1, Resultado resultado2,
			Resultado resultado3, String nomeAlgoritmoModelo, TipoValidacao tipoValidacao, int i) {


		int tamanhoListaProjetos = this.listaProjetos.size();

		
		for (int j = 0; j < tamanhoListaProjetos; j++) {
			
			List<Resultado> resultados = new ArrayList<Resultado>();
			resultados.add(resultado1);
			resultados.add(resultado2);
			resultados.add(resultado3);
			
			Projeto projetoComMelhoresResultados = menorResultado(resultados, j);
			//Projeto projetoComMelhoresResultados = menorEstimativa(resultados, j);
			

			//projetos que vieram no parâmetro recebe qual foi o menor erro obtido e o melhor algoritmo para o projeto
			listaProjetos.get(j).setMenorErro(projetoComMelhoresResultados.getMenorErro());
			listaProjetos.get(j).setMelhorAlgoritmo(projetoComMelhoresResultados.getMelhorAlgoritmo());

		}

		if(tipoValidacao == TipoValidacao.HOLD_OUT){
			File arquivoTreinamentoClassificador = new File(Constantes.CAMINHO_PADRAO + Utilidade.getCaminhoTreinamentoClassificacao(padrao, 3, Constantes.TIPO_METRICA_AVALIACAO, i));
			if(!arquivoTreinamentoClassificador.exists()){
				GeradorAleatorioDados.gerarTreinamentoClassificador(listaProjetos, nomeAlgoritmoModelo, padrao, i); 
			}
		}
		if(tipoValidacao == TipoValidacao.LEAVE_ONE_OUT){
			File arquivoTreinamentoClassificador = new File(Constantes.CAMINHO_PADRAO + Utilidade.getCaminhoTreinamentoClassificacaoLeaveOneOut(padrao, 3,  Constantes.TIPO_METRICA_AVALIACAO, i));
			if(!arquivoTreinamentoClassificador.exists()){
				GeradorAleatorioDados.gerarTreinamentoClassificadorLeaveOneOut(listaProjetos, nomeAlgoritmoModelo, padrao, i); 
			}
		}

		if(Constantes.IMPRIMIR_MELHOR_ALGORITMO){
			imprimirMelhorRegressor(padrao);
		}
	}

	protected void adicionarCamposListaProjetosAvaliados(Padrao padrao, Resultado resultado1, Resultado resultado2,
			Resultado resultado3, Resultado resultado4, String nomeAlgoritmoModelo, TipoValidacao tipoValidacao, int i) {


		int tamanhoListaProjetos = this.listaProjetos.size();

		
		for (int j = 0; j < tamanhoListaProjetos; j++) {
			
			List<Resultado> resultados = new ArrayList<Resultado>();
			resultados.add(resultado1);
			resultados.add(resultado2);
			resultados.add(resultado3);
			resultados.add(resultado4);
			
			Projeto projetoComMelhoresResultados = menorResultado(resultados, j);
			//Projeto projetoComMelhoresResultados = menorEstimativa(resultados, j);
			

			//projetos que vieram no parâmetro recebe qual foi o menor erro obtido e o melhor algoritmo para o projeto
			listaProjetos.get(j).setMenorErro(projetoComMelhoresResultados.getMenorErro());
			listaProjetos.get(j).setMelhorAlgoritmo(projetoComMelhoresResultados.getMelhorAlgoritmo());

		}

		if(tipoValidacao == TipoValidacao.HOLD_OUT){
			File arquivoTreinamentoClassificador = new File(Constantes.CAMINHO_PADRAO + Utilidade.getCaminhoTreinamentoClassificacao(padrao, 4, Constantes.TIPO_METRICA_AVALIACAO, i));
			if(!arquivoTreinamentoClassificador.exists()){
				GeradorAleatorioDados.gerarTreinamentoClassificador(listaProjetos, nomeAlgoritmoModelo, padrao, i); 
			}
		}
		if(tipoValidacao == TipoValidacao.LEAVE_ONE_OUT){
			File arquivoTreinamentoClassificador = new File(Constantes.CAMINHO_PADRAO + Utilidade.getCaminhoTreinamentoClassificacaoLeaveOneOut(padrao, 4, Constantes.TIPO_METRICA_AVALIACAO, i));
			if(!arquivoTreinamentoClassificador.exists()){
				GeradorAleatorioDados.gerarTreinamentoClassificadorLeaveOneOut(listaProjetos, nomeAlgoritmoModelo, padrao, i); 
			}
		}

		if(Constantes.IMPRIMIR_MELHOR_ALGORITMO){
			imprimirMelhorRegressor(padrao);
		}
	}

	protected void adicionarCamposListaProjetosAvaliados(Padrao padrao, Resultado resultado1, Resultado resultado2,
			Resultado resultado3, Resultado resultado4, Resultado resultado5, String nomeAlgoritmoModelo, TipoValidacao tipoValidacao, int i) {


		int tamanhoListaProjetos = this.listaProjetos.size();

		
		for (int j = 0; j < tamanhoListaProjetos; j++) {
			
			List<Resultado> resultados = new ArrayList<Resultado>();
			resultados.add(resultado1);
			resultados.add(resultado2);
			resultados.add(resultado3);
			resultados.add(resultado4);
			resultados.add(resultado5);
			
			Projeto projetoComMelhoresResultados = menorResultado(resultados, j);
			//Projeto projetoComMelhoresResultados = menorEstimativa(resultados, j);
			

			//projetos que vieram no parâmetro recebe qual foi o menor erro obtido e o melhor algoritmo para o projeto
			listaProjetos.get(j).setMenorErro(projetoComMelhoresResultados.getMenorErro());
			listaProjetos.get(j).setMelhorAlgoritmo(projetoComMelhoresResultados.getMelhorAlgoritmo());

		}

		if(tipoValidacao == TipoValidacao.HOLD_OUT){
			File arquivoTreinamentoClassificador = new File(Constantes.CAMINHO_PADRAO + Utilidade.getCaminhoTreinamentoClassificacao(padrao, 5, Constantes.TIPO_METRICA_AVALIACAO, i));
			if(!arquivoTreinamentoClassificador.exists()){
				GeradorAleatorioDados.gerarTreinamentoClassificador(listaProjetos, nomeAlgoritmoModelo, padrao, i); 
			}
		}
		if(tipoValidacao == TipoValidacao.LEAVE_ONE_OUT){
			File arquivoTreinamentoClassificador = new File(Constantes.CAMINHO_PADRAO + Utilidade.getCaminhoTreinamentoClassificacaoLeaveOneOut(padrao, 5, Constantes.TIPO_METRICA_AVALIACAO, i));
			if(!arquivoTreinamentoClassificador.exists()){
				GeradorAleatorioDados.gerarTreinamentoClassificadorLeaveOneOut(listaProjetos, nomeAlgoritmoModelo, padrao, i); 
			}
		}

		if(Constantes.IMPRIMIR_MELHOR_ALGORITMO){
			imprimirMelhorRegressor(padrao);
		}
	}

	protected void adicionarCamposListaProjetosAvaliados(Padrao padrao, Resultado resultado1,
			Resultado resultado2, Resultado resultado3, Resultado resultado4,
			Resultado resultado5, Resultado resultado6, String nomeAlgoritmoModelo, TipoValidacao tipoValidacao, int i) throws IOException {
		

		int tamanhoListaProjetos = this.listaProjetos.size();

		
		for (int j = 0; j < tamanhoListaProjetos; j++) {
			
			List<Resultado> resultados = new ArrayList<Resultado>();
			resultados.add(resultado1);
			resultados.add(resultado2);
			resultados.add(resultado3);
			resultados.add(resultado4);
			resultados.add(resultado5);
			resultados.add(resultado6);
			
			Projeto projetoComMelhoresResultados = menorResultado(resultados, j);
			

			//projetos que vieram no parâmetro recebe qual foi o menor erro obtido e o melhor algoritmo para o projeto
			listaProjetos.get(j).setMenorErro(projetoComMelhoresResultados.getMenorErro());
			listaProjetos.get(j).setMelhorAlgoritmo(projetoComMelhoresResultados.getMelhorAlgoritmo());
			
		}
		if(tipoValidacao == TipoValidacao.HOLD_OUT){
			File arquivoTreinamentoClassificador = new File(Constantes.CAMINHO_PADRAO + Utilidade.getCaminhoTreinamentoClassificacao(padrao, 6, Constantes.TIPO_METRICA_AVALIACAO, i));
			if(!arquivoTreinamentoClassificador.exists()){
				GeradorAleatorioDados.gerarTreinamentoClassificador(listaProjetos, nomeAlgoritmoModelo, padrao, i); 
			}
		}
		if(tipoValidacao == TipoValidacao.LEAVE_ONE_OUT){
			File arquivoTreinamentoClassificador = new File(Constantes.CAMINHO_PADRAO + Utilidade.getCaminhoTreinamentoClassificacaoLeaveOneOut(padrao, 6, Constantes.TIPO_METRICA_AVALIACAO, i));
			if(!arquivoTreinamentoClassificador.exists()){
				GeradorAleatorioDados.gerarTreinamentoClassificadorLeaveOneOut(listaProjetos, nomeAlgoritmoModelo, padrao, i); 
			}
		}

		
		if(Constantes.IMPRIMIR_MELHOR_ALGORITMO){
			imprimirMelhorRegressor(padrao);
		}
	}

	protected void adicionarCamposListaProjetosAvaliados(Padrao padrao, Resultado resultado1,
			Resultado resultado2, Resultado resultado3, Resultado resultado4,
			Resultado resultado5, Resultado resultado6, Resultado resultado7, Resultado resultado8, Resultado resultado9, Resultado resultado10, String nomeAlgoritmoModelo, TipoValidacao tipoValidacao, int i) {
		

		int tamanhoListaProjetos = this.listaProjetos.size();

		
		for (int j = 0; j < tamanhoListaProjetos; j++) {
			
			List<Resultado> resultados = new ArrayList<Resultado>();
			resultados.add(resultado1);
			resultados.add(resultado2);
			resultados.add(resultado3);
			resultados.add(resultado4);
			resultados.add(resultado5);
			resultados.add(resultado6);
			resultados.add(resultado7);
			resultados.add(resultado8);
			resultados.add(resultado9);
			resultados.add(resultado10);
			
			
			
			Projeto projetoComMelhoresResultados = menorResultado(resultados, j);
			

			//projetos que vieram no parâmetro recebe qual foi o menor erro obtido e o melhor algoritmo para o projeto
			listaProjetos.get(j).setMenorErro(projetoComMelhoresResultados.getMenorErro());
			listaProjetos.get(j).setMelhorAlgoritmo(projetoComMelhoresResultados.getMelhorAlgoritmo());

		}

		if(tipoValidacao == TipoValidacao.HOLD_OUT){
			File arquivoTreinamentoClassificador = new File(Constantes.CAMINHO_PADRAO + Utilidade.getCaminhoTreinamentoClassificacao(padrao, 7, Constantes.TIPO_METRICA_AVALIACAO, i));
			if(!arquivoTreinamentoClassificador.exists()){
				GeradorAleatorioDados.gerarTreinamentoClassificador(listaProjetos, nomeAlgoritmoModelo, padrao, i); 
			}
		}
		if(tipoValidacao == TipoValidacao.LEAVE_ONE_OUT){
			File arquivoTreinamentoClassificador = new File(Constantes.CAMINHO_PADRAO + Utilidade.getCaminhoTreinamentoClassificacaoLeaveOneOut(padrao, 7, Constantes.TIPO_METRICA_AVALIACAO, i));
			if(!arquivoTreinamentoClassificador.exists()){
				GeradorAleatorioDados.gerarTreinamentoClassificadorLeaveOneOut(listaProjetos, nomeAlgoritmoModelo, padrao, i); 
			}
		}

		if(Constantes.IMPRIMIR_MELHOR_ALGORITMO){
			imprimirMelhorRegressor(padrao);
		}
	}

	
	private Projeto menorResultado(List<Resultado> resultados, int i) {
		double menorErro = 10000000;
		String melhorAlgoritimo = "";
		Projeto projeto = new Projeto();
		
		TipoMetricaAvaliacao tipoMetricaAvaliacao = Constantes.TIPO_METRICA_AVALIACAO;
		
		
		if(tipoMetricaAvaliacao == TipoMetricaAvaliacao.MAR){
			// faz uma iteração na lista dos resultados que foram passados
			for (int j = 0; j < resultados.size(); j++) {
				if(resultados.get(j).getErrosAbsolutos().get(i) < menorErro){
					menorErro = resultados.get(j).getErrosAbsolutos().get(i);
					melhorAlgoritimo = "REGRESSOR" + (j+1);
				}
			}
		}

		if(tipoMetricaAvaliacao == TipoMetricaAvaliacao.MARLOG){
			// faz uma iteração na lista dos resultados que foram passados
			for (int j = 0; j < resultados.size(); j++) {
				if(resultados.get(j).getMarlogs().get(i) < menorErro){
					menorErro = resultados.get(j).getMarlogs().get(i);
					melhorAlgoritimo = "REGRESSOR" + (j+1);
				}
			}
		}

		if(tipoMetricaAvaliacao == TipoMetricaAvaliacao.MREAJUSTADO){
			// faz uma iteração na lista dos resultados que foram passados
			for (int j=0; j < resultados.size(); j++) {
				if(resultados.get(j).getMresAjustados().get(i) < menorErro){
					menorErro = resultados.get(j).getMresAjustados().get(i);
					melhorAlgoritimo = "REGRESSOR" + (j+1);
				}
			}
		}

		projeto.setMenorErro(menorErro);
		projeto.setMelhorAlgoritmo(melhorAlgoritimo);
		
		return projeto;
	}
	protected Classifier getClassificadorTreinado(Integer tipoClassificador) throws Exception {
		//treino o classificador no arquivo com as instancias e o melhor método encontrado
//		we = new WekaExperiment();
//		we.setTrainingData(instanciasClassificadas);
//		we.KFoldCrossValidationStratified(Constantes.FOLD, 1, tipoClassificador); 
		
		// retorna o classificador gerado
		Classifier classificador = new WekaExperiment().createClassifier(tipoClassificador);
		classificador.buildClassifier(instanciasClassificadas);
		return classificador;
	}

	protected void configurarDadosClassificacao(String dadosTreino, String dadosValidacao)
			throws FileNotFoundException, IOException {
		
		// Usaremos a base definida no parâmetro caminho, é o mesmo conjunto de dados mas agora com rotulo de classificação
		String nomeArquivoComClassificador = dadosTreino;
		caminhoArquivo = Constantes.CAMINHO_PADRAO + nomeArquivoComClassificador;
		reader = new FileReader(caminhoArquivo);
		this.instanciasClassificadas = new Instances(reader);
		this.instanciasClassificadas.setClassIndex(instanciasClassificadas.numAttributes() - 1);

		
		String nomeArquivoSemClassificador = dadosValidacao;
		caminhoArquivo = Constantes.CAMINHO_PADRAO + nomeArquivoSemClassificador;
		reader = new FileReader(caminhoArquivo);
		this.instanciasSemClassificacao = new Instances(reader);
		this.instanciasSemClassificacao.setClassIndex(instanciasSemClassificacao.numAttributes() - 1);
	}
	
	private void imprimirMelhorRegressor(Padrao padrao) {
		// impressão dos valores dos dados do projeto
		padrao.imprimirProjetos(listaProjetos);
		
	}
	
	protected double calcularMedianaLista(List<Double> valoresEstimados) {
		double valorEstimado;		
		int esq=0;  
		int dir= valoresEstimados.size()-1;  
		int meio;		
		Collections.sort(valoresEstimados);
		if((esq+dir)%2 == 0){
			meio=(esq+dir)/2;  
			valorEstimado = valoresEstimados.get(meio);
		}else{
			double esquerda = 0;
			double direita = valoresEstimados.size()-1;		
			double metade = (esquerda+direita)/2;
			esq = (int)metade;
			dir = (int)Math.ceil(metade);
			valorEstimado = (valoresEstimados.get(esq) + valoresEstimados.get(dir))/2;
		}
		valoresEstimados = new ArrayList<Double>();
		return valorEstimado;
	}

	protected double calcularMediaLista(List<Double> valoresEstimados) {
		double soma = 0;
		double valorEstimado = 0;
		
		for (Double valor : valoresEstimados) {
			soma += valor;
		}
		
		valorEstimado = soma/valoresEstimados.size();
		
		return valorEstimado;
		
	}
	
	protected double calcularMediaPontas(List<Double> valoresEstimados) {

		double valorEstimado = 0;
		double minimo;
		double maximo;
		
		minimo = calcularMenorValor(valoresEstimados);
		maximo = calcularMaiorValor(valoresEstimados);
		
		valorEstimado = (minimo + maximo)/2;
		
		return valorEstimado;
		
	}

	
	protected double calcularMediaPonderadaNormalLista(List<Double> valoresEstimados) {
		double soma = 0;
		double valorEstimado = 0;
		
		//for (int i=0; i < valoresEstimados.size(); i++) {
		if(valoresEstimados.size() == 3){
			soma += valoresEstimados.get(0)*0.25;
			soma += valoresEstimados.get(1)*0.25;
			soma += valoresEstimados.get(2)*0.50;
		}
		if(valoresEstimados.size() == 5){
			soma += valoresEstimados.get(0)*0.15;
			soma += valoresEstimados.get(1)*0.15;
			soma += valoresEstimados.get(2)*0.2;
			soma += valoresEstimados.get(3)*0.25;
			soma += valoresEstimados.get(4)*0.25;
		}
		//}
		
		//valorEstimado = soma/valoresEstimados.size();
			valorEstimado = soma;
		
		return valorEstimado;
		
	}
	
	protected static double calcularModaLista(List<Double> valoresEstimados) {

		int nVezes = 1, v = valoresEstimados.size(), i = 0;
        double moda = 0;
        int comparaV = 0;
        double M[] = new double[v];
        
        
        for (Double valor : valoresEstimados) {
			M[i] = valor.doubleValue();
			i++;
		}

        for (int p = 0; p < M.length; p++) {
            nVezes = 1;

            for (int k = p + 1; k < M.length; k++) {
                if (M[p] == M[k]) {
                    ++nVezes;
                }
            }
            if (nVezes > comparaV) {
                moda = M[p];
                comparaV = nVezes;
            }
        }

        return moda;
	}
	
	
	protected double calcularMenorValor(List<Double> valoresEstimados) {
		
		double menor = valoresEstimados.get(0);
		
		for (Double estimativa: valoresEstimados) {
			if (estimativa < menor){
				menor = estimativa;
			}			
		}
		return menor;
	}

	protected double calcularMaiorValor(List<Double> valoresEstimados) {
		
		double maior = valoresEstimados.get(0);
		
		for (Double estimativa: valoresEstimados) {
			if (estimativa > maior){
				maior = estimativa;
			}			
		}
		return maior;
	}

	protected double calcularUnicoValor(List<Double> valoresEstimados) {
		
		double unico = valoresEstimados.get(0);
		
		return unico;
	}

	protected double getEstimativaEnsemble(TipoMetodoCombinacao tipoSelecaoDinamica, List<Double> valoresEstimados){
		
		double valorEstimado = 0;
		
		if(tipoSelecaoDinamica == TipoMetodoCombinacao.MEDIA){
			// escolher uma estimativa ideal da lista criada
			valorEstimado = calcularMediaLista(valoresEstimados);
			if(Constantes.IMPRIMIR_ESTIMATIVAS_DINAMICAS){
			System.out.println(""+valoresEstimados.get(0)+
					"\t"+valoresEstimados.get(1)+
					"\t"+valoresEstimados.get(2)+
					"\t"+valoresEstimados.get(3)+
					"\t"+valoresEstimados.get(4)+
					"\t"+valorEstimado+
					"");
			}
		}
		if(tipoSelecaoDinamica == TipoMetodoCombinacao.MEDIANA){
			// escolher uma estimativa ideal da lista criada
			valorEstimado = calcularMedianaLista(valoresEstimados);  						
			if(Constantes.IMPRIMIR_ESTIMATIVAS_DINAMICAS){
			System.out.println(""+valoresEstimados.get(0)+
					"\t"+valoresEstimados.get(1)+
					"\t"+valoresEstimados.get(2)+
					"\t"+valoresEstimados.get(3)+
					"\t"+valoresEstimados.get(4)+
					"\t"+valorEstimado+
					"");
			}
		}
	
		if(tipoSelecaoDinamica == TipoMetodoCombinacao.MODA){
			// escolher uma estimativa ideal da lista criada
			valorEstimado = calcularModaLista(valoresEstimados);
			if(Constantes.IMPRIMIR_ESTIMATIVAS_DINAMICAS){
				System.out.println(""+valoresEstimados.get(0)+
						"\t"+valoresEstimados.get(1)+
						"\t"+valoresEstimados.get(2)+
						"\t"+valoresEstimados.get(3)+
						"\t"+valoresEstimados.get(4)+
						"\t"+valorEstimado+
						"");
			}
		}
		if(tipoSelecaoDinamica == TipoMetodoCombinacao.MINIMO){
			// escolher uma estimativa ideal da lista criada
			valorEstimado = calcularMenorValor(valoresEstimados);
			if(Constantes.IMPRIMIR_ESTIMATIVAS_DINAMICAS){
			System.out.println(""+valoresEstimados.get(0)+
					"\t"+valoresEstimados.get(1)+
					"\t"+valoresEstimados.get(2)+
					"\t"+valoresEstimados.get(3)+
					"\t"+valoresEstimados.get(4)+
					"\t"+valorEstimado+
					"");		
				}
		}

		if(tipoSelecaoDinamica == TipoMetodoCombinacao.MAXIMO){
			// escolher uma estimativa ideal da lista criada
			valorEstimado = calcularMaiorValor(valoresEstimados);
			if(Constantes.IMPRIMIR_ESTIMATIVAS_DINAMICAS){
			System.out.println(""+valoresEstimados.get(0)+
					"\t"+valoresEstimados.get(1)+
					"\t"+valoresEstimados.get(2)+
					"\t"+valoresEstimados.get(3)+
					"\t"+valoresEstimados.get(4)+
					"\t"+valorEstimado+
					"");		
				}
		}
		
		if(tipoSelecaoDinamica == TipoMetodoCombinacao.MEDIA_DAS_PONTAS){
			// escolher uma estimativa ideal da lista criada
			valorEstimado = calcularMediaPontas(valoresEstimados);
			if(Constantes.IMPRIMIR_ESTIMATIVAS_DINAMICAS){
			System.out.println(""+valoresEstimados.get(0)+
					"\t"+valoresEstimados.get(1)+
					"\t"+valoresEstimados.get(2)+
					"\t"+valoresEstimados.get(3)+
					"\t"+valoresEstimados.get(4)+
					"\t"+valorEstimado+
					"");
			}
		}
		
		if(tipoSelecaoDinamica == TipoMetodoCombinacao.SD){
			// escolher uma estimativa ideal da lista criada
			valorEstimado = calcularUnicoValor(valoresEstimados);
			if(Constantes.IMPRIMIR_ESTIMATIVAS_DINAMICAS){
			System.out.println(""+valoresEstimados.get(0)+
					"\t"+valoresEstimados.get(1)+
					"\t"+valoresEstimados.get(2)+
					"\t"+valoresEstimados.get(3)+
					"\t"+valoresEstimados.get(4)+
					"\t"+valorEstimado+
					"");
			}
		}		

	
		
		return valorEstimado;

	}
	
	public List<Projeto> getListaProjetos() {
		return listaProjetos;
	}

	public void setListaProjetos(List<Projeto> listaProjetos) {
		this.listaProjetos = listaProjetos;
	}
	
	public String getNomeModeloCriado() {
		return nomeModeloCriado;
	}

	public void setNomeModeloCriado(String nomeModeloCriado) {
		this.nomeModeloCriado = nomeModeloCriado;
	}

	public String getCaminhoArquivo() {
		return caminhoArquivo;
	}

	public void setCaminhoArquivo(String caminhoArquivo) {
		this.caminhoArquivo = caminhoArquivo;
	}

	public FileReader getReader() {
		return reader;
	}

	public void setReader(FileReader reader) {
		this.reader = reader;
	}

	public Instances getInstancias() {
		return instancias;
	}

	public void setInstancias(Instances instancias) {
		this.instancias = instancias;
	}

	public WekaExperiment getWe() {
		return we;
	}

	public void setWe(WekaExperiment we) {
		this.we = we;
	}

	public Resultado getResultado() {
		return resultado;
	}

	public void setResultado(Resultado resultado) {
		this.resultado = resultado;
	}
	
	public List<Projeto> getListaProjetosTeste() {
		return listaProjetosTeste;
	}

	public void setListaProjetosTeste(List<Projeto> listaProjetosTeste) {
		this.listaProjetosTeste = listaProjetosTeste;
	}

	public abstract void run(Padrao padrao, TipoValidacao tipoValidacao);
	public abstract void run(Padrao padrao);

	public void run(Padrao padrao, int tamanhoLeaveOneOut) {
		
	}

	public void run(Padrao padrao, Classifier metaclassificador) {
		// TODO Auto-generated method stub
		
	}

	public void run(Padrao padrao, TipoValidacao tipoValidacao,
			Classifier metaClassificador) {
		// TODO Auto-generated method stub
		
	}

	public List<Projeto> getListaProjetosValidacao() {
		return listaProjetosValidacao;
	}

	public void setListaProjetosValidacao(List<Projeto> listaProjetosValidacao) {
		this.listaProjetosValidacao = listaProjetosValidacao;
	}

	



	
	
	

}

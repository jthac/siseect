package model;

import java.util.ArrayList;
import java.util.List;

import util.Constantes;
import util.Utilidade;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

public class Resultado {

	private String nomeModelo;
	String relatorio = "";
	List<Double> errosAbsolutos = new ArrayList<Double>();
	private double erroAbsoluto;
	private double erroMarLog;
	private double taxaDeAcertoDoClassificador;
	private boolean acertouAlgoritmo;
	private int acertosClassificador;
	private int errosClassificador;
	private List<Double> estimativas = new ArrayList<Double>();
	List<Double> marlogs = new ArrayList<Double>();
	List<Double> bres = new ArrayList<Double>();
	private List<Double> mresAjustados = new ArrayList<Double>();
	private double mresAjustadoMedia;
	
	public Resultado() {
		super();
	}

	public Resultado(String nomeModelo, ArrayList<String> atributosSelecionados) {
		super();
		this.nomeModelo = nomeModelo;
	}




	public String getNomeModelo() {
		return nomeModelo;
	}

	public void setNomeModelo(String nomeModelo) {
		this.nomeModelo = nomeModelo;
	}

	public List<Double> getErrosAbsolutos() {
		return errosAbsolutos;
	}

	public String getRelatorio() {
		return relatorio;
	}	
	
	
	public List<Double> getMarlogs() {
		return marlogs;
	}

	public void setMarlogs(List<Double> marlogs) {
		this.marlogs = marlogs;
	}

	public List<Double> getMresAjustados() {
		return mresAjustados;
	}

	public void setMresAjustados(List<Double> mresAjustados) {
		this.mresAjustados = mresAjustados;
	}

	public double getErroAbsoluto() {
		return erroAbsoluto;
	}

	public void calculaErroAbsoluto() {
		double somaErroAbsoluto = 0;

		for (Double erro : errosAbsolutos) {
			somaErroAbsoluto += erro;
		}

		this.erroAbsoluto = somaErroAbsoluto / errosAbsolutos.size();
		if(Utilidade.ADICIONAR_RESULTADOS_INDIVIDUAIS){
			Utilidade.RESULTADOS_METRICA_MAR.add((""+erroAbsoluto).replace(".", ","));
			Utilidade.AUXILIAR_METRICA_MAR.add((""+erroAbsoluto).replace(".", ","));
		}
	}
	
	public void calculaMarLog() {
		double somaMarLog = 0;

		for (Double erro : marlogs) {
			if(erro != 0){
				somaMarLog += erro;				
			}
		}

		this.erroMarLog = somaMarLog / marlogs.size();
		if(Utilidade.ADICIONAR_RESULTADOS_INDIVIDUAIS){
			Utilidade.RESULTADOS_METRICA_MARLOG.add((""+erroMarLog).replace(".", ","));
			Utilidade.AUXILIAR_METRICA_MARLOG.add((""+erroMarLog).replace(".", ","));
		}
	}


	public void calculaMREAjustado() {

		double somaMreAjustado = 0;

		for (double erro : mresAjustados) {
			somaMreAjustado += erro;
		}

		this.mresAjustadoMedia = somaMreAjustado / mresAjustados.size();
		if(Utilidade.ADICIONAR_RESULTADOS_INDIVIDUAIS){
			Utilidade.RESULTADOS_METRICA_MREAJUSTADO.add((""+mresAjustadoMedia).replace(".", ","));
			Utilidade.AUXILIAR_METRICA_MREAJUSTADO.add((""+mresAjustadoMedia).replace(".", ","));
		}

	}


	public double getTaxaDeAcertoDoClassificador() {
		return taxaDeAcertoDoClassificador;
	}
	
	public void calculaTaxaDeAcertoDoClassificador(){
		taxaDeAcertoDoClassificador = acertosClassificador*100 / (acertosClassificador + errosClassificador);
		
	}

	public boolean isAcertouAlgoritmo() {
		return acertouAlgoritmo;
	}

	public void setAcertouAlgoritmo(boolean acertouAlgoritmo) {
		this.acertouAlgoritmo = acertouAlgoritmo;
	}
	
	public void addAcerto() {
		this.acertosClassificador++;
	}

	public void addErro(){
		this.errosClassificador++;
	}

	

	public List<Double> getEstimativas() {
		return estimativas;
	}

	public void setEstimativas(List<Double> estimativas) {
		this.estimativas = estimativas;
	}

	public void avaliarModelo(Classifier classificador, Instances instancias, String nomeModeloCriado) throws Exception {
		
		// Agora vamos classificar cada dado original com esta rede
		double erroAbsoluto;
		Double valorEstimado, valorReal;
		this.nomeModelo = nomeModeloCriado;
		double menor = 0;
		double marlog;
		double mreAjustado;
		
		for (int a = 0; a < instancias.numInstances(); a++) {

			// Recuperamos cada uma das instâncias
			Instance instancia = instancias.instance(a);

			// Classificamos esta instância
			valorEstimado = (double) Math.abs(classificador.classifyInstance(instancia));
			valorReal = (double) instancia.classValue();
			
			erroAbsoluto = Math
					.abs(valorReal - (valorEstimado));
			
			marlog = Math.log10(erroAbsoluto); 
					
			
			if (valorReal < valorEstimado){
				menor = valorReal;
			}else{
				menor = valorEstimado;
			}
			
			
			mreAjustado = ((erroAbsoluto / valorReal) + (erroAbsoluto / valorEstimado)/2);
			
			errosAbsolutos.add(erroAbsoluto);
			marlogs.add(marlog);
			mresAjustados.add(mreAjustado);
			estimativas.add(valorEstimado);
			
			
	}
		
		calculaErroAbsoluto();
		calculaMarLog();
		calculaMREAjustado();
		
	
		if(Constantes.IMPRIMIR_ESTIMATIVAS){
			System.out.println(toStringEstimativas());
		}
	}
	

	private StringBuilder toStringEstimativas() {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		for (Double estimativa: estimativas){
			sb.append(estimativa.toString());
			//sb.append("\n");
		}
		return sb;
	}

	public String toStringSimples() {
	
		relatorio = "";

		relatorio += "Erro Absoluto Médio: " + getErroAbsoluto() + "\n";
		relatorio += "Taxa de Acerto do Classificador: " + getTaxaDeAcertoDoClassificador() + "%" + "\n";
	
		relatorio += nomeModelo;
		return relatorio;
	}
	@Override
	public String toString() {

	
		relatorio = "";

	

		relatorio += "Erro Absoluto Médio: " + getErroAbsoluto() + "\n";
		relatorio += "Taxa de Acerto do Classificador: " + getTaxaDeAcertoDoClassificador() + "%" + "\n";
	

		relatorio += nomeModelo;
		return relatorio;
	}
	
	
	
}

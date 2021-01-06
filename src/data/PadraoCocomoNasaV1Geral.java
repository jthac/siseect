package data;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import util.TipoValidacao;
import weka.core.Instances;
import model.Projeto;

/**Padrão CocomoNasa V1 normal com todos atributos
 * */
public class PadraoCocomoNasaV1Geral extends Padrao {

	private static final int INDICE_ROTULO = 16;
	private int numeroEnsemble = 0;
	private static final int QTDE_PROJETOS = 60;

	public static double KNORA_MEDIA_MAR = 271;
	public static double KNORA_MEDIA_MARLOG = 1.95;
	public static double KNORA_MEDIA_MRE = 1.76;	
	public static double KNORA_MEDIA_BRE = 2.36;
	public static double KNORA_MEDIA_MREAJUSTADO = 2.31;	

	public PadraoCocomoNasaV1Geral() {
		super();
		knoraMediaMar = KNORA_MEDIA_MAR;
		knoraMediaMarlog = KNORA_MEDIA_MARLOG;
		knoraMediaMre = KNORA_MEDIA_MRE;
		knoraMediaBre = KNORA_MEDIA_BRE;
		knoraMediaMreajustado = KNORA_MEDIA_BRE;
	}

	public PadraoCocomoNasaV1Geral(int numeroEnsemble) {
		this.numeroEnsemble = numeroEnsemble;
		if(this.numeroEnsemble == 1){
			classificadores = "{KNN3,MLP1,LR}";
		}
		if(this.numeroEnsemble == 2){
			classificadores = "{LMS,MLP2,SVMR}";
		}
		if(this.numeroEnsemble == 3){
			classificadores = "{LMS,M5R,SVMR}";
		}
		if(this.numeroEnsemble == 4){
			classificadores = "{M5R,MLP2,SVMR}";
		}
		if(this.numeroEnsemble == 5){
			classificadores = "{LMS,M5R,MLP2}";
		}
		if(this.numeroEnsemble == 7){
			classificadores = "{KNN3,KNN7,MLP2,LR,AR,GP,SVR,BA,M5R,M5P}";
		}
	}


	@Override
	protected List<Projeto> getProjetos(String caminhoArquivo)
			throws FileNotFoundException, IOException {
		List<Projeto> listaProjetos = new ArrayList<Projeto>();
		FileReader reader = new FileReader(caminhoArquivo);

		Instances instancias = new Instances(reader);

		int indice = 0;
		for (int contador = 0; contador < instancias.numInstances(); contador++, indice = 0) {

			Projeto projeto = new Projeto();
			projeto.setRelyString(instancias.instance(contador).stringValue(indice++));
			projeto.setDataString(instancias.instance(contador).stringValue(indice++));
			projeto.setCplxString(instancias.instance(contador).stringValue(indice++));
			projeto.setTimeString(instancias.instance(contador).stringValue(indice++));
			projeto.setStorString(instancias.instance(contador).stringValue(indice++));
			projeto.setVirtString(instancias.instance(contador).stringValue(indice++));
			projeto.setTurnString(instancias.instance(contador).stringValue(indice++));
			projeto.setAcapString(instancias.instance(contador).stringValue(indice++));
			projeto.setAexpString(instancias.instance(contador).stringValue(indice++));
			projeto.setPcapString(instancias.instance(contador).stringValue(indice++));
			projeto.setVexpString(instancias.instance(contador).stringValue(indice++));
			projeto.setLexpString(instancias.instance(contador).stringValue(indice++));
			projeto.setModpString(instancias.instance(contador).stringValue(indice++));
			projeto.setToolString(instancias.instance(contador).stringValue(indice++));
			projeto.setScedString(instancias.instance(contador).stringValue(indice++));
			projeto.setLoc(instancias.instance(contador).value(indice++));
			projeto.setActEffort(instancias.instance(contador).value(indice++));
			
			listaProjetos.add(projeto);

		}
		return listaProjetos;
	}

	@Override
	public void imprimirProjetos(List<Projeto> listaProjetos) {
		
		for (Projeto projeto : listaProjetos) {

			System.out.print(projeto.getRelyString() + "\t");
			System.out.print(projeto.getDataString() + "\t");
			System.out.print(projeto.getCplxString() + "\t");
			System.out.print(projeto.getTimeString() + "\t");
			System.out.print(projeto.getStorString() + "\t");
			System.out.print(projeto.getVirtString() + "\t");
			System.out.print(projeto.getTurnString() + "\t");
			System.out.print(projeto.getAcapString() + "\t");
			System.out.print(projeto.getAexpString() + "\t");
			System.out.print(projeto.getPcapString() + "\t");
			System.out.print(projeto.getVexpString() + "\t");
			System.out.print(projeto.getLexpString() + "\t");
			System.out.print(projeto.getModpString() + "\t");
			System.out.print(projeto.getToolString() + "\t");
			System.out.print(projeto.getScedString() + "\t");
			System.out.print(projeto.getLoc() + "\t");
			System.out.print(projeto.getActEffort() + "\t");
			System.out.print(projeto.getMenorErro() + "\t");
			System.out.println(projeto.getMelhorAlgoritmo());
		}
		
	}

	@Override
	public String toString() {
		return "COCOMONASAV1_GERAL/COCOMONASAV1";
	}


	@Override
	public int getIndiceRotuloClassificador() {
		return INDICE_ROTULO;
	}

	@Override
	public String getNomeArquivo() {
		// TODO Auto-generated method stub
		return "COCOMONASAV1_GERAL/COCOMONASAV1.ARFF";
	}

	@Override
	public String getCabecalho() {
		// TODO Auto-generated method stub
		return "@relation cocomonasav1.csv \n\n" +

		"@attribute rely {Nominal,Very_High,High,Low} \n" + 
		"@attribute data {Nominal,Very_High,High,Low} \n" +
		"@attribute cplx {Very_High,High,Nominal,Extra_High,Low} \n" +
		"@attribute time {Nominal,Very_High,High,Extra_High} \n" +
		"@attribute stor {Nominal,Very_High,High,Extra_High} \n" +
		"@attribute virt {Low,Nominal,High} \n" +
		"@attribute turn {Nominal,High,Low} \n" +
		"@attribute acap {High,Very_High,Nominal} \n" +
		"@attribute aexp {Nominal,Very_High,High} \n" +
		"@attribute pcap {Very_High,High,Nominal} \n" +
		"@attribute vexp {Low,Nominal,High} \n" +
		"@attribute lexp {Nominal,High,Very_Low,Low} \n" +
		"@attribute modp {High,Nominal,Very_High,Low} \n" +
		"@attribute tool {Nominal,High,Very_High,Very_Low,Low} \n" +
		"@attribute sced {Low,Nominal,High} \n" +
		"@attribute loc numeric \n" +
		"@attribute act_effort numeric \n" +
		"@data \n\n";

	}
	
	
	@Override
	public String getCabecalhoClassificacao() {
		// TODO Auto-generated method stub
		return "@relation cocomonasav1.csv \n\n" +

		"@attribute rely {Nominal,Very_High,High,Low} \n" + 
		"@attribute data {Nominal,Very_High,High,Low} \n" +
		"@attribute cplx {Very_High,High,Nominal,Extra_High,Low} \n" +
		"@attribute time {Nominal,Very_High,High,Extra_High} \n" +
		"@attribute stor {Nominal,Very_High,High,Extra_High} \n" +
		"@attribute virt {Low,Nominal,High} \n" +
		"@attribute turn {Nominal,High,Low} \n" +
		"@attribute acap {High,Very_High,Nominal} \n" +
		"@attribute aexp {Nominal,Very_High,High} \n" +
		"@attribute pcap {Very_High,High,Nominal} \n" +
		"@attribute vexp {Low,Nominal,High} \n" +
		"@attribute lexp {Nominal,High,Very_Low,Low} \n" +
		"@attribute modp {High,Nominal,Very_High,Low} \n" +
		"@attribute tool {Nominal,High,Very_High,Very_Low,Low} \n" +
		"@attribute sced {Low,Nominal,High} \n" +
		"@attribute loc numeric \n" +
		"@attribute winner" + classificadores + "\n\n" +
		"@data \n\n";
	}

	@Override
	public String getLinhaClassificacao(Projeto projeto, boolean isTreinamento) {
		
		StringBuilder sb = new StringBuilder();

		sb.append(projeto.getRelyString() + ",\t");
		sb.append(projeto.getDataString() + ",\t");
		sb.append(projeto.getCplxString() + ",\t");
		sb.append(projeto.getTimeString() + ",\t");
		sb.append(projeto.getStorString() + ",\t");
		sb.append(projeto.getVirtString() + ",\t");
		sb.append(projeto.getTurnString() + ",\t");
		sb.append(projeto.getAcapString() + ",\t");
		sb.append(projeto.getAexpString() + ",\t");
		sb.append(projeto.getPcapString() + ",\t");
		sb.append(projeto.getVexpString() + ",\t");
		sb.append(projeto.getLexpString() + ",\t");
		sb.append(projeto.getModpString() + ",\t");
		sb.append(projeto.getToolString() + ",\t");
		sb.append(projeto.getScedString() + ",\t");
		sb.append(projeto.getLoc() + ",\t");

		if (isTreinamento) {
			sb.append(projeto.getMelhorAlgoritmo());
		} else {
			sb.append("?");
		}

		return sb.toString();
		 
	}

	@Override
	public int getTamanhoBase() {
		// TODO Auto-generated method stub
		return QTDE_PROJETOS;
	}

	@Override
	public TipoValidacao getTipoValidacao() {
		// TODO Auto-generated method stub
		return null;
	}

}

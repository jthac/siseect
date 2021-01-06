package data;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import util.TipoValidacao;
import weka.core.Instances;
import model.Projeto;

/**Padrão CocomoNasa V2 normal com todos atributos
 * */
public class PadraoCocomoNasaV2Geral extends Padrao {

	private static final int INDICE_ROTULO = 22;
	private int numeroEnsemble = 0;
	private static final int QTDE_PROJETOS = 93;

	public static double KNORA_MEDIA_MAR = 566;
	public static double KNORA_MEDIA_MARLOG = 2.34;
	public static double KNORA_MEDIA_MRE = 3.72;	
	public static double KNORA_MEDIA_BRE = 4.72;
	public static double KNORA_MEDIA_MREAJUSTADO = 4.66;	

	public PadraoCocomoNasaV2Geral() {
		super();
		knoraMediaMar = KNORA_MEDIA_MAR;
		knoraMediaMarlog = KNORA_MEDIA_MARLOG;
		knoraMediaMre = KNORA_MEDIA_MRE;
		knoraMediaBre = KNORA_MEDIA_BRE;
		knoraMediaMreajustado = KNORA_MEDIA_BRE;
	}

	public PadraoCocomoNasaV2Geral(int numeroEnsemble) {
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
		if(this.numeroEnsemble == 6){
			classificadores = "{LMS,M5R,MLP2,SVMR}";
		}		if(this.numeroEnsemble == 7){
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
			projeto.setProjectName(instancias.instance(contador).stringValue(indice++));
			projeto.setCat2(instancias.instance(contador).stringValue(indice++));
			projeto.setForg((instancias.instance(contador).stringValue(indice++)).charAt(0));
			projeto.setCenter(instancias.instance(contador).stringValue(indice++));
			projeto.setYear(instancias.instance(contador).value(indice++));
			projeto.setMode(instancias.instance(contador).stringValue(indice++));
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
			projeto.setEquivPhyskLoc(instancias.instance(contador).value(indice++));
			projeto.setActEffort(instancias.instance(contador).value(indice++));
			
			listaProjetos.add(projeto);

		}
		return listaProjetos;
	}

	@Override
	public void imprimirProjetos(List<Projeto> listaProjetos) {
		
		for (Projeto projeto : listaProjetos) {

			System.out.print(projeto.getProjectName() + "\t");
			System.out.print(projeto.getCat2() + "\t");
			System.out.print(projeto.getForg() + "\t");
			System.out.print(projeto.getCenter() + "\t");
			System.out.print(projeto.getYear() + "\t");
			System.out.print(projeto.getMode() + "\t");
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
			System.out.print(projeto.getEquivPhyskLoc() + "\t");
			System.out.print(projeto.getActEffort() + "\t");
			System.out.print(projeto.getMenorErro() + "\t");
			System.out.println(projeto.getMelhorAlgoritmo());
		}
		
	}

	@Override
	public String toString() {
		return "COCOMONASAV2_GERAL/COCOMONASAV2";
	}


	@Override
	public int getIndiceRotuloClassificador() {
		return INDICE_ROTULO;
	}

	@Override
	public String getNomeArquivo() {
		// TODO Auto-generated method stub
		return "COCOMONASAV2_GERAL/COCOMONASAV2.ARFF";
	}


	@Override
	public String getCabecalho() {
		// TODO Auto-generated method stub
		return "@relation cocomonasa_2-weka.filters.unsupervised.attribute.Remove-R1-2-weka.filters.unsupervised.attribute.Remove-R1-4 \n\n" +

		"@attribute projectname {de,erb,gal,X,hst,slp,spl,Y}  \n" +
		"@attribute cat2 {Avionics, application_ground, avionicsmonitoring, batchdataprocessing, communications, datacapture, launchprocessing, missionplanning, monitor_control, operatingsystem, realdataprocessing, science, simulation, utility} \n" +
		"@attribute forg {f,g} \n" +
		"@attribute center {1,2,3,4,5,6}  \n" +
		"@attribute year numeric \n" +
		"@attribute mode {embedded,organic,semidetached} \n" +
		"@attribute rely {vl,l,n,h,vh,xh} \n" + 
		"@attribute data {vl,l,n,h,vh,xh} \n" +
		"@attribute cplx {vl,l,n,h,vh,xh} \n" +
		"@attribute time {vl,l,n,h,vh,xh} \n" +
		"@attribute stor {vl,l,n,h,vh,xh} \n" +
		"@attribute virt {vl,l,n,h,vh,xh} \n" +
		"@attribute turn {vl,l,n,h,vh,xh} \n" +
		"@attribute acap {vl,l,n,h,vh,xh} \n" +
		"@attribute aexp {vl,l,n,h,vh,xh} \n" +
		"@attribute pcap {vl,l,n,h,vh,xh} \n" +
		"@attribute vexp {vl,l,n,h,vh,xh} \n" +
		"@attribute lexp {vl,l,n,h,vh,xh} \n" +
		"@attribute modp {vl,l,n,h,vh,xh} \n" +
		"@attribute tool {vl,l,n,h,vh,xh} \n" +
		"@attribute sced {vl,l,n,h,vh,xh} \n" +
		"@attribute equivphyskloc numeric \n" +
		"@attribute act_effort numeric \n" +
		"@data \n\n";

	}
	
	
	@Override
	public String getCabecalhoClassificacao() {
		// TODO Auto-generated method stub
		return "@relation cocomonasa_2-weka.filters.unsupervised.attribute.Remove-R1-2-weka.filters.unsupervised.attribute.Remove-R1-4 \n\n" +

		"@attribute projectname {de,erb,gal,X,hst,slp,spl,Y}  \n" +
		"@attribute cat2 {Avionics, application_ground, avionicsmonitoring, batchdataprocessing, communications, datacapture, launchprocessing, missionplanning, monitor_control, operatingsystem, realdataprocessing, science, simulation, utility} \n" +
		"@attribute forg {f,g} \n" +
		"@attribute center {1,2,3,4,5,6}  \n" +
		"@attribute year numeric \n" +
		"@attribute mode {embedded,organic,semidetached} \n" +
		"@attribute rely {vl,l,n,h,vh,xh} \n" + 
		"@attribute data {vl,l,n,h,vh,xh} \n" +
		"@attribute cplx {vl,l,n,h,vh,xh} \n" +
		"@attribute time {vl,l,n,h,vh,xh} \n" +
		"@attribute stor {vl,l,n,h,vh,xh} \n" +
		"@attribute virt {vl,l,n,h,vh,xh} \n" +
		"@attribute turn {vl,l,n,h,vh,xh} \n" +
		"@attribute acap {vl,l,n,h,vh,xh} \n" +
		"@attribute aexp {vl,l,n,h,vh,xh} \n" +
		"@attribute pcap {vl,l,n,h,vh,xh} \n" +
		"@attribute vexp {vl,l,n,h,vh,xh} \n" +
		"@attribute lexp {vl,l,n,h,vh,xh} \n" +
		"@attribute modp {vl,l,n,h,vh,xh} \n" +
		"@attribute tool {vl,l,n,h,vh,xh} \n" +
		"@attribute sced {vl,l,n,h,vh,xh} \n" +
		"@attribute equivphyskloc numeric \n" +
		"@attribute winner" + classificadores + "\n\n" +
		"@data \n\n";
	}

	@Override
	public String getLinhaClassificacao(Projeto projeto, boolean isTreinamento) {
		
		StringBuilder sb = new StringBuilder();

		sb.append(projeto.getProjectName() + ",\t");
		sb.append(projeto.getCat2() + ",\t");
		sb.append(projeto.getForg() + ",\t");
		sb.append(projeto.getCenter() + ",\t");
		sb.append(projeto.getYear() + ",\t");
		sb.append(projeto.getMode() + ",\t");
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
		sb.append(projeto.getEquivPhyskLoc() + ",\t");

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

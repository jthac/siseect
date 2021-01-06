package util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.GaussianProcesses;
import weka.classifiers.functions.LeastMedSq;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.RBFNetwork;
import weka.classifiers.functions.SMOreg;
import weka.classifiers.lazy.IBk;
import weka.classifiers.lazy.KStar;
import weka.classifiers.lazy.LWL;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.AdditiveRegression;
import weka.classifiers.meta.Bagging;
import weka.classifiers.meta.MultiClassClassifier;
import weka.classifiers.meta.Stacking;
import weka.classifiers.rules.ConjunctiveRule;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.rules.JRip;
import weka.classifiers.rules.M5Rules;
import weka.classifiers.rules.OneR;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.BFTree;
import weka.classifiers.trees.DecisionStump;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.LADTree;
import weka.classifiers.trees.LMT;
import weka.classifiers.trees.M5P;
import weka.classifiers.trees.REPTree;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

/**
 * Classe que realiza experimento de treino/teste automaticamente, utilizando a
 * API Java do Weka. Trabalha, por enquanto, com classifica��o bin�ria.
 * 
 * @author Alex
 * @version 1.3: metodos createClassifier(), novos classificadores [1.2]
 *          Alteracoes no m�todo removeAttribute(), acrescimo do m�todo
 *          saveDecisionTree(). [1.1] metodo loadARFFFile() sobrecarregado.
 *          Data: 20/08/2013
 */
public class WekaExperiment {
	// The training set object
	private Instances trainingData;
	// The training set object copy, to recover when necessary
	// private Instances trainingCopy;
	// The test set object
	private Instances testData;
	// Classifier
	private Classifier classifier;
	// Avaliador do treino ou teste
	public Evaluation eval;
	// N�mero de folds (para cross-validation)
	private int folds;
	// N�mero de seed (para cross-validation);
	private int seed;
	// Sinaliza se a �ltima avalia��o foi no arquivo de treino (true) ou de
	// teste (false)
	private boolean isTrainEvaluated = false;

	// Accuracy Matrix
	double accuracy[][];

	//
	private boolean trainingFileLoaded = false;
	private boolean testFileLoaded = false;
	public Classifier metaClassificador = new REPTree();

	public static final int TRAINING_FILE = 0;
	public static final int TEST_FILE = 1;

	public static final int J48 = 0;
	public static final int SVM = 1;
	public static final int NAIVE_BAYES = 2;
	public static final int RANDOM_FOREST = 3;
	public static final int ADABOOST = 4;
	public static final int LOGISTIC_REGRESSION = 5;
	public static final int ONER = 6;
	public static final int KNN1 = 7;
	public static final int KNN3 = 8;
	public static final int KNN5 = 9;
	public static final int KNN7 = 10;
	public static final int KNN9 = 11;
	public static final int MLP1 = 12;
	public static final int MLP2 = 13;
	public static final int MLP3 = 14;
	public static final int MLP4 = 15;
	public static final int MLP5 = 16;
	public static final int ZEROR = 17;
	public static final int STACKING = 18;
	public static final int LINEAR_REGRESSION = 19;
	public static final int GP = 20;
	public static final int M5R = 21;
	public static final int KSTAR = 22;
	public static final int ADDITIVE_REGRESSION = 23;
	public static final int SUPPORT_VECTOR_REGRESSION = 24;
	public static final int DECISION_TABLE = 25;
	public static final int REP_TREE = 26;
	public static final int CONJUNCTIVE_RULES = 27;
	public static final int RBF_NETWORK = 28;
	public static final int BAGGING = 29;
	public static final int M5P = 30;
	public static final int DECISION_STUMP = 31;
	public static final int LEAST_MED_SQ = 32;
	public static final int LOCALLY_WEIGHTED_LEARNING = 33;
	public static final int BEST_FIRST_TREE = 34;
	public static final int BAYES_NET = 35;
	public static final int J_RIP = 36;
	public static final int LMT = 37;
	public static final int LAD_TREE = 38;



	public WekaExperiment() {
		// Create an empty training set with a initial set capacity of 10
		trainingData = null;
		classifier = null;
		eval = null;
		accuracy = new double[2][6];
	}


	
	/**
	 * Treina o classificador de acordo com as inst�ncias de treinamento
	 * carregadas.
	 * 
	 * @param theClassifier
	 *            O classificador utilizado para constru��o do modelo
	 * @throws Exception
	 */
	public void buildClassifier(int theClassifier) throws Exception {
		if (!trainingFileLoaded)
			return;
		// classifier = (Classifier)new J48();
		if (!(classifier instanceof MultiClassClassifier))
			classifier = createClassifier(theClassifier);
		classifier.buildClassifier(trainingData);

	}

	/**
	 * Treina o classificador para as inst�ncias de treinamento carregadas. Este
	 * m�todo s� deve ser chamado se o classificador for MultiClasse.
	 * 
	 * @throws Exception
	 */
	public void buildClassifier() throws Exception {
		if (!(classifier instanceof MultiClassClassifier))
			System.err
					.println("O problema de classifica��o n�o foi definido como multiclasse.");
		else
			classifier.buildClassifier(trainingData);
	}

	/**
	 * Cria um classificador de acordo com o par�metro especificado. Se
	 * "theClassifier" n�o for um classificador v�lido, devolve o defaulta: J48
	 * 
	 * @param theClassifier
	 *            Constante de classe referente ao classificador desejado.
	 * @return Uma instancia do classificador.
	 */
	public Classifier createClassifier(int theClassifier) {

		if (theClassifier == J48) {
			return new J48();
		} else if (theClassifier == NAIVE_BAYES) {
			return new NaiveBayes();
		} else if (theClassifier == RANDOM_FOREST) {
			return new RandomForest();
		} else if (theClassifier == ADABOOST) {
			AdaBoostM1 adaboost = new AdaBoostM1();
			adaboost.setClassifier(new REPTree());
			return adaboost;
		} else if (theClassifier == LOGISTIC_REGRESSION) {
			return new Logistic();
		} else if (theClassifier == ONER){
			return new OneR();
		} else if (theClassifier == SVM) {
			LibSVM libSVM = new LibSVM();
			libSVM.setNormalize(true);
			return new LibSVM();
		} else if (theClassifier == KNN1) {
			return new IBk(1);
		} else if (theClassifier == KNN3) {
			return new IBk(3);
		} else if (theClassifier == KNN5) {
			return new IBk(15);
		}else if (theClassifier == KNN7) {
			return new IBk(7);
		}else if (theClassifier == KNN9) {
			return new IBk(9);
		}else if (theClassifier == MLP1) {
			MultilayerPerceptron mlp = new MultilayerPerceptron();
			mlp.setAutoBuild(true);
			mlp.setLearningRate(0.01);
			mlp.setMomentum(0.02);
			mlp.setTrainingTime(1000);
			mlp.setHiddenLayers("1");
			return mlp;
		}else if (theClassifier == MLP2) {
			MultilayerPerceptron mlp = new MultilayerPerceptron();
			mlp.setAutoBuild(true);
			mlp.setLearningRate(0.01);
			mlp.setMomentum(0.02);
			mlp.setTrainingTime(1000);
			mlp.setHiddenLayers("2");
			return mlp;
		} else if (theClassifier == MLP3) {
			MultilayerPerceptron mlp = new MultilayerPerceptron();
			mlp.setAutoBuild(true);
			mlp.setLearningRate(0.01);
			mlp.setMomentum(0.02);
			mlp.setTrainingTime(1000);
			mlp.setHiddenLayers("3");
			return mlp;
		} else if (theClassifier == MLP4) {
			MultilayerPerceptron mlp = new MultilayerPerceptron();
			mlp.setAutoBuild(true);
			mlp.setLearningRate(0.01);
			mlp.setMomentum(0.02);
			mlp.setTrainingTime(1000);
			mlp.setHiddenLayers("4");
			return mlp;
		} else if (theClassifier == MLP5) {
			MultilayerPerceptron mlp = new MultilayerPerceptron();
			mlp.setAutoBuild(true);
			mlp.setLearningRate(0.01);
			mlp.setMomentum(0.02);
			mlp.setTrainingTime(1000);
			mlp.setHiddenLayers("5");
			return mlp;
		}else if (theClassifier == ZEROR) {
			ZeroR zeror = new ZeroR();
			return zeror;
		}else if (theClassifier == STACKING) {
			Stacking stacking = new Stacking();
			LeastMedSq lms = new LeastMedSq();
			MultilayerPerceptron mlp = new MultilayerPerceptron();
			mlp.setAutoBuild(true);
			mlp.setLearningRate(0.01);
			mlp.setMomentum(0.02);
			mlp.setTrainingTime(1000);
			mlp.setHiddenLayers("2");
			SMOreg smoReg = new SMOreg();
			Classifier[] classifiers = new Classifier[3];
			classifiers[0] = lms;
			classifiers[1] = mlp;
			classifiers[2] = smoReg;
			stacking.setClassifiers(classifiers);
			stacking.setMetaClassifier(metaClassificador);
			return stacking;
		}
		else if (theClassifier == LINEAR_REGRESSION) {
			LinearRegression lr = new LinearRegression();
			return lr;
		}  else if (theClassifier == GP) {
			GaussianProcesses gp = new GaussianProcesses();
			return gp;
		}  else if (theClassifier == M5R) {
			M5Rules m5R = new M5Rules();
			return m5R;
		}  else if (theClassifier == KSTAR) {
			KStar kStar = new KStar();
			SelectedTag newMode = new SelectedTag(KStar.M_DELETE, KStar.TAGS_MISSING);
			kStar.setMissingMode(newMode);
			return kStar;
		}  else if (theClassifier == ADDITIVE_REGRESSION) {
			AdditiveRegression additiveRegression = new AdditiveRegression();
			MultilayerPerceptron mlp = new MultilayerPerceptron();
			mlp.setAutoBuild(true);
			mlp.setLearningRate(0.01);
			mlp.setMomentum(0.02);
			mlp.setTrainingTime(1000);
			mlp.setHiddenLayers("2");			
			additiveRegression.setClassifier(mlp);
			return additiveRegression;
		}  	else if (theClassifier == SUPPORT_VECTOR_REGRESSION) {
			SMOreg supportVectorRegression = new SMOreg();
			return supportVectorRegression;
		}  	else if (theClassifier == DECISION_TABLE) {
			DecisionTable decisionTable = new DecisionTable();
			return decisionTable;
		}  else if (theClassifier == REP_TREE) {
			REPTree repTree = new REPTree();
			return repTree;
		}  else if (theClassifier == CONJUNCTIVE_RULES) {
			ConjunctiveRule conjunctiveRules = new ConjunctiveRule();
			return conjunctiveRules;
		}  else if (theClassifier == RBF_NETWORK) {
			RBFNetwork rbfNetwork = new RBFNetwork();
			return rbfNetwork;
		}  else if (theClassifier == BAGGING) {
			Bagging bagging = new Bagging();
			MultilayerPerceptron mlp = new MultilayerPerceptron();
			mlp.setAutoBuild(true);
			mlp.setLearningRate(0.01);
			mlp.setMomentum(0.02);
			mlp.setTrainingTime(1000);
			mlp.setHiddenLayers("2");			
			bagging.setClassifier(mlp);
			return bagging;
		}  else if (theClassifier == M5P) {
			M5P m5P = new M5P();
			return m5P;
		}  else if (theClassifier == DECISION_STUMP) {
			DecisionStump decisionStump = new DecisionStump();
			return decisionStump;
		}  else if (theClassifier == LEAST_MED_SQ) {
			LeastMedSq leastMedSq = new LeastMedSq();
			return leastMedSq;
		}  else if (theClassifier == LOCALLY_WEIGHTED_LEARNING) {
			LWL lwl = new LWL();
			return lwl;
		}  else if (theClassifier == BAYES_NET) {
			BayesNet bayesNet = new BayesNet();
			return bayesNet;
		}  else if (theClassifier == LMT) {
			LMT lmt = new LMT();
			return lmt;
		}  else if (theClassifier == LAD_TREE) {
			LADTree ladTree = new LADTree();
			return ladTree;
		} else if (theClassifier == J_RIP) {
			JRip jRip = new JRip();
			return jRip;
		}   else if (theClassifier == BEST_FIRST_TREE) {
			BFTree bestFirstTree = new BFTree();
			return bestFirstTree;
		}  
		
		
		else {
			return new J48();
		}
	}




	/**
	 * Devolve o n�mero de folds se o experimento est� usando valida��o cruzada
	 * k-fold.
	 * 
	 * @return O n�mero de folds configurado para o experimento.
	 */
	public int getFolds() {
		return folds;
	}


	
	/**
	 * Retorna um List com os atributos encontrados no arquivo de treinamento.
	 * 
	 * @return Um ArrayList com os atributos.
	 */
	public List<String> getAttributes() {
		List<String> list = new ArrayList<String>();

		Enumeration<?> e = trainingData.enumerateAttributes();
		while (e.hasMoreElements()) {
			list.add(((Attribute) e.nextElement()).name());
		}
		return list;
	}

	/**
	 * Retorna um String com a sequencia de atributos do arquivo de treino ou
	 * teste, separados por "," (virgula)
	 * 
	 * @param typeFile
	 *            A constante de classe TRAINING_FILE ou TEST_FILE
	 * @return Um String com a sequ�ncia de atributos.
	 */
	public String getAttributesAsString(int typeFile) {
		StringBuilder str = new StringBuilder();
		Instances dataset = null;

		if (typeFile == TRAINING_FILE)
			dataset = trainingData;
		else if (typeFile == TEST_FILE)
			dataset = testData;
		else
			dataset = trainingData;

		Enumeration<?> e = dataset.enumerateAttributes();
		while (e.hasMoreElements()) {
			if (str.length() > 0)
				str.append(", ");
			str.append(((Attribute) e.nextElement()).name());
		}
		return str.toString();
	}

	/**
	 * Retorna um List com o indice dos atributos encontrados no arquivo de
	 * treinamento.
	 * 
	 * @return Um ArrayList com os indices dos atributos.
	 */
	public List<String> getAttributeIndex() {
		List<String> list = new ArrayList<String>();

		Enumeration<?> e = trainingData.enumerateAttributes();
		while (e.hasMoreElements()) {
			Attribute att = (Attribute) e.nextElement();

			list.add(String.valueOf(att.index() + 1));
		}
		return list;
	}

	/**
	 * Obtem o n�mero de atributos de um arquivo de Treino ou Teste
	 * 
	 * @param typeFile
	 *            A constante de classe TRAINING_FILE ou TEST_FILE, que sinaliza
	 *            qual dos arquivos deseja-se obter o n�mero de atributos.
	 * @return O n�mero de atributos. O valor -1 indica que "typeFile" foi
	 *         informado incorretamente.
	 */
	public int getNumAttributes(int typeFile) {
		Instances dataset = null;

		if (typeFile == TRAINING_FILE)
			dataset = trainingData;
		else if (typeFile == TEST_FILE)
			dataset = testData;
		else
			return -1;

		return dataset.numAttributes();
	}

	
	
	/**
	 * Remove atributos do arquivo de treinamento.
	 * 
	 * @param listOfAttributes
	 *            Um string contendo a sequencia de atributos que ser�o
	 *            desconsiderados. Por exemplo: "1,2,3" ou com range "1-3,6,9"
	 * @param invertSelection
	 *            true, quando deve ser removido os atributos cujos �ndices n�o
	 *            est�o presentes em listOfAttributes, ou false, caso os
	 *            atributos a serem removidos sejam os que est�o impl�citos em
	 *            listOfAttributes.
	 * @param typeFile
	 *            A constante de classe TRAINING_FILE ou TESTE_FILE
	 * @throws Exception
	 *             Fonte: (1) - http://weka.wikispaces.com/Remove+Attributes
	 *             OBS: internamente, o atributo de classe � acrescentado.
	 */
	public void removeAttribute(String listOfAttributes,
			boolean invertSelection, int typeFile) throws Exception {
		/*
		 * Use este trecho para usar com o metodo setOption() String[] options =
		 * new String[2]; options[0] = "-R"; options[1] = "1,3"; // ou "1,3-5,7"
		 */

		Instances dataset = null;
		if (typeFile == TRAINING_FILE)
			dataset = trainingData;
		else if (typeFile == TEST_FILE)
			dataset = testData;
		else
			dataset = trainingData;

		Remove remove = new Remove();

		// remove.setOptions( options );
		// Adicionando o atributo de classe
		listOfAttributes += ("," + (dataset.classIndex() + 1));
		remove.setAttributeIndices(listOfAttributes);
		remove.setInvertSelection(new Boolean(invertSelection));
		remove.setInputFormat(dataset);
		// Filter gera um novo objeto com os atributos selecionados. Portanto, o
		// arquivo
		// de treino ou teste deve ser atualizado
		if (typeFile == TRAINING_FILE)
			trainingData = Filter.useFilter(dataset, remove);
		else
			testData = Filter.useFilter(dataset, remove);

		// System.out.println("\nAtributos originais");
		// System.out.println("------------------------------");
		// Enumeration<?> e = trainingData.enumerateAttributes();
		// while( e.hasMoreElements() ){
		// System.out.println( ((Attribute) e.nextElement()).name() );
		// }

		// System.out.println("\nAtributos remanescentes");
		// System.out.println("------------------------------");
		// e = newData.enumerateAttributes();
		// while( e.hasMoreElements() ){
		// System.out.println( ((Attribute) e.nextElement()).name() );
		// }
		// return null;
		// return newData;
	}

	
	/**
	 * Exibe na tela a lista de atributos encontrada no arquivo de instancias
	 * ARFF
	 * 
	 * @param data
	 *            O arquivo de instancias ARFF.
	 */
	public void showAttributes(Instances data) {
		Enumeration<?> e = data.enumerateAttributes();
		while (e.hasMoreElements()) {
			System.out.println(((Attribute) e.nextElement()).name());
		}

	}

	/**
	 * Cria e avalia um modelo de classifica��o utilizando o m�todo de
	 * reamostragem k-fold validation.
	 * 
	 * @param nFolds
	 *            O n�mero de folds (parti��es) desejado
	 * @param seed
	 *            O valor de seed para randomizar as inst�ncias. O padr�o � 1.
	 * @param theClassifier
	 *            A constante referente ao classificador desejado
	 * 
	 *            OBSERVA��O: - Internamente j� s�o chamados os m�todos
	 *            buildClassifier() e evaluateModel();
	 * 
	 *            O arquivo fornecido para valida��o cruzada � armazenado em
	 *            "trainingData/'.
	 * @throws Exception
	 *             Se houver problema na avalia��o da valida��o cruzada.
	 */
	public void KFoldCrossValidationStratified(int folds, int seed,
			int theClassifier) throws Exception {
		this.folds = folds;
		this.seed = seed;

		// O classificador � criado aqui de acordo com a constante que foi
		// passada
		Classifier chosenClassifier = createClassifier(theClassifier);
		isTrainEvaluated = true;

		// TODO aqui tenho que ajustar os atributos que ser�o utilizados nas
		// inst�ncias usando o trainingData

		// randomize data
		Random rand = new Random(seed);
		// Pega os dados que ser�o testados e coloca dentro do instances, o training data foi setado antes com as instancias
		Instances randData = new Instances(trainingData);

		randData.randomize(rand);
		if (randData.classAttribute().isNominal())
			// estratifica um conjunto de instancias de acordo com seu valor de
			// classe se
			// se o atributo de classe � nominal (para que depois um
			// cross-validation estratificado
			// possa ser realizado.
			randData.stratify(folds);

		// perform cross-validation
		eval = new Evaluation(randData);

		for (int n = 0; n < folds; n++) {
			Instances train = randData.trainCV(folds, n); // Creates the
															// training set for
															// one fold of a
															// cross-validation
															// on the dataset.
			Instances test = randData.testCV(folds, n); // Creates the test set
														// for one fold of a
														// cross-validation on
														// the dataset.
			// the above code is used by the StratifiedRemoveFolds filter, the
			// code below by the Explorer/Experimenter:
			// Instances train = randData.trainCV(folds, n, rand);
			// Pega um fold espec�fico para cross-validation.

			// build and evaluate classifier
			classifier = Classifier.makeCopy(chosenClassifier);
			classifier.buildClassifier(train);

			
			eval.evaluateModel(classifier, test);
		}

		classifier.buildClassifier(trainingData);
		// eval.evaluateModel(classifier, trainingData);
		// System.out.println( eval.toSummaryString());
		// System.out.println( getDecisionTree());

	}

	/**
	 * Exibe na tela o resultado da avalia��o do classificador.
	 */
	public void showOutputEvaluation() {
		System.out.println(toRunInformation());
		//System.out.println(toClassDetailsString());
		//System.out.println(toConfusionMatrix());
	}

	/**
	 * Retorna um String com os dados de entrada para avalia��o do classificador
	 * quando for usado o m�todo de reamostragem k-fold cross-validation.
	 * 
	 * @return A String com o resultado da avalia��o.
	 */
	public String toRunInformation() {
		StringBuilder output = new StringBuilder();

		Instances data = null;
		if (isTrainEvaluated)
			data = trainingData;
		else
			data = testData;

		// output evaluation
		output.append("\n");
		output.append("=== Setup ===\n");
		output.append("Classifier:\t" + this.classifier.getClass().getName()
				+ "\n");
		output.append("Dataset:\t" + data.relationName() + "\n");
		output.append("Instances:\t" + data.numInstances() + "\n");
		output.append("Attributes:\t" + data.numAttributes() + "\n");
		output.append("Folds: " + this.folds + "\n");
		output.append("Seed: " + this.seed + "\n\n");
		// O m�todo toSummaryString() equivale a 1a informa��o de sa�da da
		// avalia��o no Weka
		if (folds < 0)
			output.append(eval.toSummaryString("=== Summary ===", false));
		else
			output.append(eval.toSummaryString("=== " + folds
					+ "-fold Cross-validation ===", false));
		return output.toString();
	}

	/**
	 * Retorna um String contendo a matriz de confus�o da avalia��o.
	 * 
	 * @return A matriz de confus�o como um String.
	 */
	public String toConfusionMatrix() {
		if (eval == null)
			return null;

		try {
			// O m�todo toMatrixString() equivale a 1a informa��o de sa�da da
			// avalia��o no Weka
			return eval.toMatrixString();
		} catch (Exception e) {
			System.err.println("Erro no m�todo assDetailsString()");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retorna um String contendo a acur�cia detalhada por classe.
	 * 
	 * @return Os detalhes da acur�cia como um String.
	 */
	public String toClassDetailsString() {
		if (eval == null)
			return null;
		try {
			// O m�todo toClassDetailsString() equivale a 2a informa��o de sa�da
			// da avalia��o no Weka
			return eval.toClassDetailsString();
		} catch (Exception e) {
			System.err.println("Erro no m�todo classDetailsString()");
			e.printStackTrace();
			return null;
		}
	}

	

	public Classifier getClassifier() {
		return classifier;
	}

	public void setTrainingData(Instances trainingData) {
		this.trainingData = trainingData;
		trainingFileLoaded = true;

	}

	public void setTestData(Instances testData) {
		this.testData = testData;
		testFileLoaded = true;

	}
}

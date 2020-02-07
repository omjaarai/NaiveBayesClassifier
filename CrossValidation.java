import java.util.ArrayList;
import java.util.List;

public class CrossValidation {
    /*
     * Returns the k-fold cross validation score of classifier clf on training data.
     */
    public static double kFoldScore(Classifier clf, List<Instance> trainData, int k, int v) {
    
       int size = trainData.size()/k;
       int c = 0;
       double acc = 0;
       while (c < k) {
    	   List<Instance> duplicatetrainData = new ArrayList<>();  	      	   
    	   List<Instance> testData = new ArrayList<>();
    	   double corr = 0;
    	   testData.addAll(trainData.subList(c * size, (c * size) + size));
    	   duplicatetrainData.addAll(trainData.subList(0, c*size));
    	   duplicatetrainData.addAll(trainData.subList(c * size + size, trainData.size()));
   
    	   clf.train(duplicatetrainData, v);    	  
    	   for(Instance i : testData) {
    		   if(clf.classify(i.words).label == i.label) {
    			   corr ++;    			  
    		   }    		  
    	   }
    	   acc = acc + (double)corr/testData.size();
    	   c = c + 1;
       }
       return acc/k;
    }
}

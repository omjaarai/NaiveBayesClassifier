import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Your implementation of a naive bayes classifier. Please implement all four methods.
 */

public class NaiveBayesClassifier implements Classifier {
	Map<Label, Integer> wordsPerLabel;
	Map <Label, Integer> documentsPerLabel;
	Map <String, Integer> positiveWordCounts;
	Integer positiveVocabCount = 0;
	Map <String, Integer> negativeWordCounts;
	Integer negativeVocabCount= 0;
	int documentCount = 0;
	int vocabSize = 0;
	

    /**
     * Trains the classifier with the provided training data and vocabulary size
     */
    @Override
    public void train(List<Instance> trainData, int v) {
        // TODO : Implement
        // Hint: First, calculate the documents and words counts per label and store them. 
        // Then, for all the words in the documents of each label, count the number of occurrences of each word.
        // Save these information as you will need them to calculate the log probabilities later.
        //
        // e.g.
        // Assume m_map is the map that stores the occurrences per word for positive documents
        // m_map.get("catch") should return the number of "catch" es, in the documents labeled positive
        // m_map.get("asdasd") would return null, when the word has not appeared before.
        // Use m_map.put(word,1) to put the first count in.
        // Use m_map.replace(word, count+1) to update the value
    	
    	documentsPerLabel = getDocumentsCountPerLabel(trainData);
    	wordsPerLabel = getWordsCountPerLabel(trainData);
    	positiveVocabCount = wordsPerLabel.get(Label.POSITIVE);
    	negativeVocabCount = wordsPerLabel.get(Label.NEGATIVE);
    	documentCount = trainData.size();
    	vocabSize = v;
    	negativeWordCounts = new HashMap<>();
    	positiveWordCounts = new HashMap<>();
    	
    	for(Instance i: trainData) {
    		if (i.label == Label.POSITIVE) {
    			for(String w: i.words) {
    				if (positiveWordCounts.get(w) == null) {
    					positiveWordCounts.put(w, 1);
    				}
    				else {
    					positiveWordCounts.replace(w, positiveWordCounts.get(w) + 1);
    				}
    			}
    		}
    		else {
    			for(String w: i.words) {
    				if (negativeWordCounts.get(w) == null) {
    					negativeWordCounts.put(w, 1);
    				}
    				else {
    					negativeWordCounts.replace(w, negativeWordCounts.get(w) + 1);
    				}
    			}
    		}
    	} 
    	
    }

    /*
     * Counts the number of words for each label
     */
    @Override
    public Map<Label, Integer> getWordsCountPerLabel(List<Instance> trainData) {
        Map<Label, Integer> hash = new HashMap<>();
        for(Instance i: trainData) {
        	Integer c = hash.get(i.label);
        	if (c == null) {
    			hash.put(i.label, i.words.size());
    		}
    		else {
    			hash.replace(i.label, c + i.words.size());
    		}
        }
        return hash;
    }


    /*
     * Counts the total number of documents for each label
     */
    @Override
    public Map<Label, Integer> getDocumentsCountPerLabel(List<Instance> trainData) {
        Map<Label, Integer> hash =  new HashMap<>();
        for(Instance i : trainData) {
        	Integer c = hash.get(i.label);
        		if (c == null) {
        			hash.put(i.label, 1);
        		}
        		else {
        			hash.replace(i.label, c + 1);
        		}
        }
        
        return hash;
    }


    /**
     * Returns the prior probability of the label parameter, i.e. P(POSITIVE) or P(NEGATIVE)
     */
    private double p_l(Label label) {
        // TODO : Implement
        // Calculate the probability for the label. No smoothing here.
        // Just the number of label counts divided by the number of documents.
        double ret = documentsPerLabel.get(label)/(double)documentCount;
        return ret;
    }

    /**
     * Returns the smoothed conditional probability of the word given the label, i.e. P(word|POSITIVE) or
     * P(word|NEGATIVE)
     */
    private double p_w_given_l(String word, Label label) {
        // TODO : Implement
        // Calculate the probability with Laplace smoothing for word in class(label)
    	double c = 0;
    	double vCount = 0;
    	if (label == Label.POSITIVE) {
    	    if(positiveWordCounts.get(word) == null)
    	        c=0;    	    
    	    else 
    	        c= positiveWordCounts.get(word);
    		vCount = positiveVocabCount;
    	}
    	else {    	    
    	    if(negativeWordCounts.get(word) == null)
    	        c=0;
    	    else
    	        c= negativeWordCounts.get(word);
    		vCount = negativeVocabCount;
    	}
        return (double)(c + 1) / (vocabSize + vCount);
    }

    /**
     * Classifies an array of words as either POSITIVE or NEGATIVE.
     */
    @Override
    public ClassifyResult classify(List<String> words) {
        // TODO : Implement
        // Sum up the log probabilities for each word in the input data, and the probability of the label
        // Set the label to the class with larger log probability
    	ClassifyResult ret = new ClassifyResult();
    	double pos = java.lang.Math.log(p_l(Label.POSITIVE));
    	double neg = java.lang.Math.log(p_l(Label.NEGATIVE));
    	for(String w : words) {
    		pos = pos + java.lang.Math.log(p_w_given_l(w, Label.POSITIVE));
    		neg = neg + java.lang.Math.log(p_w_given_l(w, Label.NEGATIVE));
    	}
    	ret.logProbPerLabel = new HashMap<Label, Double>();
    	ret.logProbPerLabel.put(Label.POSITIVE, pos);
    	ret.logProbPerLabel.put(Label.NEGATIVE, neg);
    	if(ret.logProbPerLabel.get(Label.NEGATIVE)> ret.logProbPerLabel.get(Label.POSITIVE))
    	    ret.label=Label.NEGATIVE;
    	else
    	    ret.label=Label.POSITIVE;
    	return ret;
        
    }


}

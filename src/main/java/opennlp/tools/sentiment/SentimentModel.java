package opennlp.tools.sentiment;

import java.util.Map;

import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.util.model.BaseModel;

public class SentimentModel extends BaseModel {
	
	private static final String COMPONENT_NAME = "SentimentME";
	//private MaxentModel model;
	private static final String SENTIMENT_MODEL_ENTRY_NAME = "sentiment.model";

	public SentimentModel(String languageCode, MaxentModel sentimentModel,
		      Map<String, String> manifestInfoEntries, SentimentFactory factory) {
		    super(COMPONENT_NAME, languageCode, manifestInfoEntries, factory);
		    artifactMap.put(SENTIMENT_MODEL_ENTRY_NAME, sentimentModel);
		    checkArtifactMap();
	}
	

}

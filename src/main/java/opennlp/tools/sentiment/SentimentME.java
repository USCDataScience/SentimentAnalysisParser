package opennlp.tools.sentiment;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import opennlp.tools.ml.EventTrainer;
import opennlp.tools.ml.TrainerFactory;
import opennlp.tools.ml.TrainerFactory.TrainerType;
import opennlp.tools.ml.model.Event;
import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.ml.model.SequenceClassificationModel;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.TrainingParameters;

public class SentimentME {
	
	public static final String OTHER = "other";
	public static final String START = "start";
	public static final String CONTINUE = "cont";

	public static SentimentModel train(String languageCode, ObjectStream<SentimentSample> samples,
			TrainingParameters trainParams, SentimentFactory factory) throws IOException {
		/*String beamSizeString = trainParams.getSettings().get(BeamSearch.BEAM_SIZE_PARAMETER);

		int beamSize = SentimentME.DEFAULT_BEAM_SIZE;
		if (beamSizeString != null) {
			beamSize = Integer.parseInt(beamSizeString);
		}*/

		Map<String, String> entries = new HashMap<String, String>();

		MaxentModel sentimentModel = null;

		SequenceClassificationModel<String> seqModel = null;

		TrainerType trainerType = TrainerFactory.getTrainerType(trainParams.getSettings());

		//!!!!!!! ObjectStream<Event> eventStream = //new SentimentSampleStream(); //new NameFinderEventStream(samples, type, factory.createContextGenerator(),
		//factory.createSequenceCodec());
		ObjectStream<Event> eventStream = new SentimentEventStream(samples, factory.createContextGenerator(), factory.createSequenceCodec());

		EventTrainer trainer = TrainerFactory.getEventTrainer(trainParams.getSettings(), entries);
		sentimentModel = trainer.train(eventStream);
		
		Map<String, String> manifestInfoEntries = new HashMap<String, String>();
		
		return new SentimentModel(languageCode, sentimentModel, manifestInfoEntries, factory);
		
		/*if (TrainerType.EVENT_MODEL_TRAINER.equals(trainerType)) {
			ObjectStream<Event> eventStream = new SentimentSampleStream(); //new NameFinderEventStream(samples, type, factory.createContextGenerator(),
					//factory.createSequenceCodec());

			EventTrainer trainer = TrainerFactory.getEventTrainer(trainParams.getSettings(), entries);
			sentimentModel = trainer.train(eventStream);
		} 
		/else if (TrainerType.EVENT_MODEL_SEQUENCE_TRAINER.equals(trainerType)) {
			NameSampleSequenceStream ss = new NameSampleSequenceStream(samples, factory.createContextGenerator());

			EventModelSequenceTrainer trainer = TrainerFactory.getEventModelSequenceTrainer(trainParams.getSettings(),
					entries);
			sentimentModel = trainer.train(ss);
		} else if (TrainerType.SEQUENCE_TRAINER.equals(trainerType)) {
			SequenceTrainer trainer = TrainerFactory.getSequenceModelTrainer(trainParams.getSettings(),
					entries);

			//NameSampleSequenceStream ss = new NameSampleSequenceStream(samples, factory.createContextGenerator(),
					//false);
			seqModel = trainer.train(ss);
		} else {
			throw new IllegalStateException("Unexpected trainer type!");
		}*/

		/*if (seqModel != null) {
			return new TokenNameFinderModel(languageCode, seqModel, factory.getFeatureGenerator(),
					factory.getResources(), entries, factory.getSequenceCodec(), factory);
		} else {
			return new TokenNameFinderModel(languageCode, sentimentModel, beamSize, factory.getFeatureGenerator(),
					factory.getResources(), entries, factory.getSequenceCodec(), factory);
		}*/
	}

}

package opennlp.tools.sentiment;

import java.util.Iterator;

import opennlp.tools.ml.model.Event;
import opennlp.tools.util.AbstractEventStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.SequenceCodec;

public class SentimentEventStream extends AbstractEventStream<SentimentSample> {

	private SentimentContextGenerator contextGenerator;

	/*public SentimentEventStream(ObjectStream<SentimentSample> samples) {
		super(samples);
		contextGenerator = new SentimentContextGenerator;
	}*/

	public SentimentEventStream(ObjectStream<SentimentSample> samples,
			SentimentContextGenerator createContextGenerator, SequenceCodec<String> createSequenceCodec) {
		super(samples);
	}

	@Override
	protected Iterator<Event> createEvents(final SentimentSample sample) {

		return new Iterator<Event>(){

		      private boolean isVirgin = true;

		      public boolean hasNext() {
		        return isVirgin;
		      }

		      public Event next() {

		        isVirgin = false;

		        return new Event(sample.getSentiment(),
		           contextGenerator.getContext(sample.getSentence()));
		      }

		      public void remove() {
		        throw new UnsupportedOperationException();
		      }};
	}

}

package opennlp.tools.sentiment;

import java.util.Iterator;

import opennlp.tools.ml.model.Event;
import opennlp.tools.util.AbstractEventStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.SequenceCodec;

/**
 * Class for creating events for Sentiment Analysis that is later sent to
 * MaxEnt.
 */
public class SentimentEventStream extends AbstractEventStream<SentimentSample> {

  private SentimentContextGenerator contextGenerator;

  /**
   * Initializes the event stream.
   *
   * @param samples
   *          the sentiment samples to be used
   * @param createContextGenerator
   *          the context generator to be used
   */
  public SentimentEventStream(ObjectStream<SentimentSample> samples,
      SentimentContextGenerator createContextGenerator) {
    super(samples);
    contextGenerator = createContextGenerator;
  }

  /**
   * Creates events.
   *
   * @param sample
   *          the sentiment sample to be used
   * @return event iterator
   */
  @Override
  protected Iterator<Event> createEvents(final SentimentSample sample) {

    return new Iterator<Event>() {

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
      }
    };
  }

}

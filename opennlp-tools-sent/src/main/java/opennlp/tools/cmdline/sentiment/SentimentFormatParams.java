package opennlp.tools.cmdline.sentiment;

import opennlp.tools.cmdline.ArgumentParser.OptionalParameter;
import opennlp.tools.cmdline.ArgumentParser.ParameterDescription;
import opennlp.tools.cmdline.params.BasicFormatParams;

public interface SentimentFormatParams extends BasicFormatParams {

	@ParameterDescription(valueName = "jsonl", description = "use jsonl as data source.")
	@OptionalParameter(defaultValue = "false")
	Boolean getJsonl();

	@ParameterDescription(valueName = "label", description = "use json label path.")
	@OptionalParameter
	String getLabel();

	@ParameterDescription(valueName = "text", description = "use json text path.")
	@OptionalParameter
	String getText();
}

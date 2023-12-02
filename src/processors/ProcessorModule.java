package processors;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import processors.clients.ChatGPTClient;
import processors.impl.TextOnlyProcessor;

public class ProcessorModule extends AbstractModule {

	@Override
	protected void configure() {

		// clients
		bind(ChatGPTClient.class).in(Singleton.class);

		// processors
		bind(TextOnlyProcessor.class).in(Singleton.class);
	}

}

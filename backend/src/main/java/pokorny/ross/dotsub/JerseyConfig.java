package pokorny.ross.dotsub;

import java.util.logging.Logger;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(FileResource.class);
        register(MultiPartFeature.class);
        packages("pokorny.ross.dotsub", "pokorny.ross.dotsub.mappers",
                "pokorny.ross.dotsub.writers");

        //property("jersey.config.server.tracing.type", "ALL");
        //property("jersey.config.server.tracing.threshold", "VERBOSE");
    }
}

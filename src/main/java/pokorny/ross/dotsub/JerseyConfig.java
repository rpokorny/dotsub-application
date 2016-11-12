package pokorny.ross.dotsub;

import org.glassfish.jersey.server.ResourceConfig;

import org.springframework.stereotype.Component;

import org.glassfish.jersey.media.multipart.MultiPartFeature;

@Component
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        register(FileResource.class);
        register(MultiPartFeature.class);
    }
}

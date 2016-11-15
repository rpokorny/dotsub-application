package pokorny.ross.dotsub;

import java.util.logging.Logger;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import org.springframework.stereotype.Component;

import pokorny.ross.dotsub.writers.ThrowableWriter;
import pokorny.ross.dotsub.mappers.DataIntegrityViolationExceptionMapper;
import pokorny.ross.dotsub.mappers.IllegalArgumentExceptionMapper;
import pokorny.ross.dotsub.mappers.NoSuchElementExceptionMapper;
import pokorny.ross.dotsub.mappers.RuntimeExceptionMapper;
import pokorny.ross.dotsub.mappers.WebApplicationExceptionMapper;

@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(MultiPartFeature.class);

        //NOTE: Jersey auto-registration via package() doesn't work
        //right with Spring-boot self-executable jars
        register(FileResource.class);
        register(ThrowableWriter.class);
        register(DataIntegrityViolationExceptionMapper.class);
        register(IllegalArgumentExceptionMapper.class);
        register(NoSuchElementExceptionMapper.class);
        register(RuntimeExceptionMapper.class);
        register(WebApplicationExceptionMapper.class);

        //property("jersey.config.server.tracing.type", "ALL");
        //property("jersey.config.server.tracing.threshold", "VERBOSE");
    }
}

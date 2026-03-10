package io.jenkins.plugins.oras.java.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.logging.Level;
import land.oras.ContainerRef;
import land.oras.Index;
import land.oras.Registry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.jvnet.hudson.test.junit.jupiter.RealJenkinsExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Execution(ExecutionMode.SAME_THREAD)
class RealJenkinsTest {

    /**
     * Logger for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(RealJenkinsTest.class);

    @RegisterExtension
    private final RealJenkinsExtension extension = new RealJenkinsExtension()
            .withLogger("land.oras", Level.FINEST)
            .withLogger(RealJenkinsTest.class, Level.FINEST);

    @Test
    void pullIndexTest() throws Throwable {
        extension.then(r -> {
            Registry registry = Registry.builder().build();
            ContainerRef containerRef1 = ContainerRef.parse("ghcr.io/oras-project/oras:main");
            Index index = registry.getIndex(containerRef1);
            assertNotNull(index);
            LOG.info("Index found: {}", index);
        });
    }
}

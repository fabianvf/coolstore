// Write the updated file for Quarkus in this section
import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.spi.Eventual;
import kotlin.jvm.functions.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.redhat.coolstore.utils.StartupListener;

import javax.inject.Inject;
import java.util.logging.Logger;

public class StartupListener extends Eventual implements ApplicationLifecycleListener {

    @Inject
    private static final Logger log = LoggerFactory.getLogger(StartupListener.class);

    @Override
    public void postStart(Event<Void> evt) {
        log.info("AppListener(postStart)");
    }

    @Override
    public void preStop(Event<Void> evt) {
        log.info("AppListener(preStop)");
    }
}

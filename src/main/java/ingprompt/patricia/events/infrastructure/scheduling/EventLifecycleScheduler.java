package ingprompt.patricia.events.infrastructure.scheduling;

import ingprompt.patricia.events.application.port.in.EventLifecycleCase;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EventLifecycleScheduler {
    private final EventLifecycleCase eventLifecycleCase;

    @Scheduled(fixedDelayString = "${events.start-scheduler.delay-ms:60000}")
    public void startTick() {
        eventLifecycleCase.startDueEvents();
    }

    @Scheduled(fixedDelayString = "${events.end-scheduler.delay-ms:60000}")
    public void endTick() {
        eventLifecycleCase.endDueEvents();
    }
}

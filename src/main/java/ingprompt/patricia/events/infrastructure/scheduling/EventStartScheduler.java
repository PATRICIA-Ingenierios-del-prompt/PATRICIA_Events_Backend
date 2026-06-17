package ingprompt.patricia.events.infrastructure.scheduling;

import ingprompt.patricia.events.application.port.in.EventLifecycleCase;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Drives the {@code event.started} lifecycle by polling once a minute.
 * Single-instance assumption: if Events MS is ever scaled to multiple replicas,
 * add a distributed lock (e.g. ShedLock) so only one node fires per tick.
 */
@Component
@AllArgsConstructor
public class EventStartScheduler {

    private final EventLifecycleCase eventLifecycleCase;

    @Scheduled(fixedDelayString = "${events.start-scheduler.delay-ms:60000}")
    public void tick() {
        eventLifecycleCase.startDueEvents();
    }
}

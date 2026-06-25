package ingprompt.patricia.events.infrastructure.scheduling;

import ingprompt.patricia.events.application.port.in.EventLifecycleCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventLifecycleSchedulerTest {

    @Mock
    private EventLifecycleCase eventLifecycleCase;
    @InjectMocks
    private EventLifecycleScheduler scheduler;

    @Test
    void startTick_triggersStartDueEvents() {
        scheduler.startTick();
        verify(eventLifecycleCase).startDueEvents();
    }

    @Test
    void endTick_triggersEndDueEvents() {
        scheduler.endTick();
        verify(eventLifecycleCase).endDueEvents();
    }
}

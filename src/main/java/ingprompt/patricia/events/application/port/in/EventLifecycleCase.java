package ingprompt.patricia.events.application.port.in;

public interface EventLifecycleCase {
    void startDueEvents();
    void endDueEvents();
}

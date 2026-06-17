package ingprompt.patricia.events.application.service;

import ingprompt.patricia.events.application.port.in.ManageReportCase;
import ingprompt.patricia.events.application.port.out.EventPublisherOut;
import ingprompt.patricia.events.application.port.out.EventRepositoryOutPort;
import ingprompt.patricia.events.application.port.out.ReportRepositoryOutPort;
import ingprompt.patricia.events.domain.enums.ReportType;
import ingprompt.patricia.events.domain.exception.EventNotFoundException;
import ingprompt.patricia.events.domain.exception.NotEventParticipantException;
import ingprompt.patricia.events.domain.model.Event;
import ingprompt.patricia.events.domain.model.Report;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ReportService implements ManageReportCase {
    private final EventRepositoryOutPort eventRepository;
    private final ReportRepositoryOutPort reportRepository;
    private final EventPublisherOut eventPublisher;

    @Override
    @Transactional
    public Report createReport(UUID eventId, UUID reporterId, ReportType reportType, String description) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
        if (!event.hasParticipant(reporterId)) {
            throw new NotEventParticipantException(reporterId, eventId);
        }

        Report report = Report.create(eventId, reporterId, reportType, description);
        reportRepository.save(report);
        eventPublisher.publishIncidentReported(eventId, report.getReportId(), reporterId);
        return report;
    }
}

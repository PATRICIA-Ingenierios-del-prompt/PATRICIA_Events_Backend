package ingprompt.patricia.events.application.service;

import ingprompt.patricia.events.application.port.out.EventPublisherOut;
import ingprompt.patricia.events.application.port.out.EventRepositoryOutPort;
import ingprompt.patricia.events.application.port.out.ReportRepositoryOutPort;
import ingprompt.patricia.events.domain.enums.Category;
import ingprompt.patricia.events.domain.enums.ReportType;
import ingprompt.patricia.events.domain.exception.EventNotFoundException;
import ingprompt.patricia.events.domain.exception.NotEventParticipantException;
import ingprompt.patricia.events.domain.model.Event;
import ingprompt.patricia.events.domain.model.Report;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private EventRepositoryOutPort eventRepository;
    @Mock
    private ReportRepositoryOutPort reportRepository;
    @Mock
    private EventPublisherOut publisher;

    @InjectMocks
    private ReportService service;

    private UUID ownerId;
    private UUID eventId;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();
        eventId = UUID.randomUUID();
    }

    private Event event() {
        return new Event(eventId, "Hike", "desc", Category.SPORT, 10, ownerId,
                LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0));
    }

    @Test
    void createReport_byParticipant_savesAndPublishes() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event()));

        Report report = service.createReport(eventId, ownerId, ReportType.ACCIDENT, "fell down");

        assertThat(report.getReporterId()).isEqualTo(ownerId);
        assertThat(report.getReportType()).isEqualTo(ReportType.ACCIDENT);
        verify(reportRepository).save(any(Report.class));
        verify(publisher).publishIncidentReported(eq(eventId), eq(report.getReportId()), eq(ownerId));
    }

    @Test
    void createReport_whenEventMissing_throws() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createReport(eventId, ownerId, ReportType.OTHER, "x"))
                .isInstanceOf(EventNotFoundException.class);
        verify(reportRepository, never()).save(any());
    }

    @Test
    void createReport_byNonParticipant_throws() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event()));

        assertThatThrownBy(() -> service.createReport(eventId, UUID.randomUUID(), ReportType.THEFT, "stolen"))
                .isInstanceOf(NotEventParticipantException.class);
        verify(publisher, never()).publishIncidentReported(any(), any(), any());
    }
}

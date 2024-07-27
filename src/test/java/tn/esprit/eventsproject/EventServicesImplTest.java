package tn.esprit.eventsproject;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import tn.esprit.eventsproject.entities.Event;
import tn.esprit.eventsproject.entities.Logistics;
import tn.esprit.eventsproject.entities.Participant;
import tn.esprit.eventsproject.entities.Tache;
import tn.esprit.eventsproject.repositories.EventRepository;
import tn.esprit.eventsproject.repositories.LogisticsRepository;
import tn.esprit.eventsproject.repositories.ParticipantRepository;
import tn.esprit.eventsproject.services.EventServicesImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class EventServicesImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private LogisticsRepository logisticsRepository;

    @InjectMocks
    private EventServicesImpl eventServices;

    @BeforeEach
    void setUp() {
        // MockitoAnnotations.openMocks(this) is not needed with @ExtendWith(MockitoExtension.class)
    }

    @Test
    void testAddParticipant() {
        Participant participant = new Participant();
        when(participantRepository.save(participant)).thenReturn(participant);

        Participant result = eventServices.addParticipant(participant);

        assertNotNull(result);
        verify(participantRepository, times(1)).save(participant);
    }

    @Test
    void testAddAffectEvenParticipantWithId() {
        Event event = new Event();
        Participant participant = new Participant();
        participant.setEvents(new HashSet<>());
        when(participantRepository.findById(1)).thenReturn(Optional.of(participant));
        when(eventRepository.save(event)).thenReturn(event);

        Event result = eventServices.addAffectEvenParticipant(event, 1);

        assertNotNull(result);
        assertTrue(participant.getEvents().contains(event));
        verify(participantRepository, times(1)).findById(1);
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void testAddAffectEvenParticipantWithoutId() {
        Event event = new Event();
        Participant participant = new Participant();
        participant.setIdPart(1);  // Ensure the participant has an ID
        participant.setEvents(new HashSet<>());
        Set<Participant> participants = new HashSet<>();
        participants.add(participant);
        event.setParticipants(participants);
        when(participantRepository.findById(participant.getIdPart())).thenReturn(Optional.of(participant));
        when(eventRepository.save(event)).thenReturn(event);

        Event result = eventServices.addAffectEvenParticipant(event);

        assertNotNull(result);
        assertTrue(participant.getEvents().contains(event));
        verify(participantRepository, times(1)).findById(participant.getIdPart());
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void testAddAffectLog() {
        Event event = new Event();
        Logistics logistics = new Logistics();
        logistics.setReserve(true);
        when(eventRepository.findByDescription("eventDesc")).thenReturn(event);
        when(eventRepository.save(event)).thenReturn(event);
        when(logisticsRepository.save(logistics)).thenReturn(logistics);

        Logistics result = eventServices.addAffectLog(logistics, "eventDesc");

        assertNotNull(result);
        verify(eventRepository, times(1)).findByDescription("eventDesc");
        verify(logisticsRepository, times(1)).save(logistics);
    }

    @Test
    void testGetLogisticsDates() {
        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now();
        Event event = new Event();
        Set<Logistics> logisticsSet = new HashSet<>();
        Logistics logistics = new Logistics();
        logistics.setReserve(true);
        logisticsSet.add(logistics);
        event.setLogistics(logisticsSet);
        when(eventRepository.findByDateDebutBetween(startDate, endDate)).thenReturn(Arrays.asList(event));

        List<Logistics> result = eventServices.getLogisticsDates(startDate, endDate);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(logistics));
        verify(eventRepository, times(1)).findByDateDebutBetween(startDate, endDate);
    }

    @Test
    void testCalculCout() {
        Event event = new Event();
        event.setLogistics(new HashSet<>());
        Logistics logistics = new Logistics();
        logistics.setReserve(true);
        logistics.setPrixUnit(10.0f);
        logistics.setQuantite(5);
        event.getLogistics().add(logistics);
        List<Event> events = Arrays.asList(event);
        when(eventRepository.findByParticipants_NomAndParticipants_PrenomAndParticipants_Tache("Tounsi", "Ahmed", Tache.ORGANISATEUR)).thenReturn(events);
        when(eventRepository.save(event)).thenReturn(event);

        // Assuming `calculCout` method updates the event with the cost
        eventServices.calculCout();

        verify(eventRepository, times(1)).findByParticipants_NomAndParticipants_PrenomAndParticipants_Tache("Tounsi", "Ahmed", Tache.ORGANISATEUR);
        verify(eventRepository, times(1)).save(event);
    }
}

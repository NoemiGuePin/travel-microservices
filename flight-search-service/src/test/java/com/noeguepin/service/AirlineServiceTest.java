package com.noeguepin.service;

import com.noeguepin.dto.AirlineRequest;
import com.noeguepin.dto.AirlineResponse;
import com.noeguepin.exception.ResourceAlreadyExistsException;
import com.noeguepin.exception.ResourceNotFoundException;
import com.noeguepin.model.Airline;
import com.noeguepin.repository.AirlineRepository;
import com.noeguepin.testdata.AirlineTestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AirlineServiceTest {

    @Mock
    private AirlineRepository airlineRepository;

    @InjectMocks
    private AirlineService airlineService;

    @Test
    void testGetAirlineByFilters() {
        when(airlineRepository.findAirlinesByFilters("air")).thenReturn(List.of(AirlineTestData.airFrance(), AirlineTestData.airEuropa()));

        List<AirlineResponse> airlineAirMatches = airlineService.getAirlineByFilters("air");
        Set<String> codesIATA = airlineAirMatches
                .stream()
                .map(AirlineResponse::codeIATA)
                .collect(Collectors.toSet());

        assertEquals(Set.of("AF", "AE"), codesIATA);
        verify(airlineRepository, only()).findAirlinesByFilters("air");
    }

    @Test
    void testGetAirlineByFiltersEmptyList() {
        when(airlineRepository.findAirlinesByFilters("air")).thenReturn(List.of());

        List<AirlineResponse> airlineAirMatches = airlineService.getAirlineByFilters("air");

        assertTrue(airlineAirMatches.isEmpty());
        verify(airlineRepository, only()).findAirlinesByFilters("air");
    }

    @Test
    void testGetAirlineByCodeIATA() {
        when(airlineRepository.findByCodeIATA("IB")).thenReturn(Optional.of(AirlineTestData.iberia()));

        AirlineResponse airlineIberia = airlineService.getAirlineByCodeIATA("IB");

        assertEquals("Iberia", airlineIberia.name());
        assertEquals("IB", airlineIberia.codeIATA());
        verify(airlineRepository, only()).findByCodeIATA("IB");
    }

    @Test
    void testGetAirlineByCodeIATANotFound() {
        when(airlineRepository.findByCodeIATA("VY")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> airlineService.getAirlineByCodeIATA("VY"));
        verify(airlineRepository, only()).findByCodeIATA("VY");
    }

    @Test
    void testSaveNewAirline() {
        AirlineRequest airlineRequest= new AirlineRequest("VY", "Vueling");
        when(airlineRepository.findByCodeIATA("VY")).thenReturn(Optional.empty());
        when(airlineRepository.save(any(Airline.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AirlineResponse airlineResponse = airlineService.saveNewAirline(airlineRequest);

        ArgumentCaptor<Airline> captor = ArgumentCaptor.forClass(Airline.class);
        verify(airlineRepository).findByCodeIATA("VY");
        verify(airlineRepository).save(captor.capture());
        Airline airlineSaved = captor.getValue();
        assertEquals("VY", airlineSaved.getCodeIATA());
        assertEquals("Vueling", airlineSaved.getName());
        assertEquals("VY", airlineResponse.codeIATA());
        assertEquals("Vueling", airlineResponse.name());
        verifyNoMoreInteractions(airlineRepository);
    }

    @Test
    void testSaveNewAirlineAlreadyExistingIATACode() {
        AirlineRequest airlineRequest= new AirlineRequest("IB", "Iberia");
        when(airlineRepository.findByCodeIATA("IB")).thenReturn(Optional.of(AirlineTestData.iberia()));

        assertThrows(ResourceAlreadyExistsException.class, () -> airlineService.saveNewAirline(airlineRequest));
        verify(airlineRepository, only()).findByCodeIATA("IB");
        verify(airlineRepository, never()).save(any(Airline.class));
    }

    @Test
    void testUpdateAirline() {
        AirlineRequest airlineRequest= new AirlineRequest("IB", "Iberia S.A");
        when(airlineRepository.findByCodeIATA("IB")).thenReturn(Optional.of(AirlineTestData.iberia()));
        when(airlineRepository.save(any(Airline.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AirlineResponse airlineResponse = airlineService.updateAirline("IB", airlineRequest);

        ArgumentCaptor<Airline> captor = ArgumentCaptor.forClass(Airline.class);
        verify(airlineRepository).findByCodeIATA("IB");
        verify(airlineRepository).save(captor.capture());
        Airline airlineSaved = captor.getValue();
        assertEquals("IB", airlineResponse.codeIATA());
        assertEquals("Iberia S.A", airlineResponse.name());
        assertEquals("IB", airlineSaved.getCodeIATA());
        assertEquals("Iberia S.A", airlineSaved.getName());
        verifyNoMoreInteractions(airlineRepository);
    }

    @Test
    void testUpdateAirlineDifferentIATACodeInRequest() {
        AirlineRequest airlineRequest= new AirlineRequest("VL", "Iberia S.A");
        when(airlineRepository.findByCodeIATA("IB")).thenReturn(Optional.of(AirlineTestData.iberia()));
        when(airlineRepository.save(any(Airline.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AirlineResponse airlineResponse = airlineService.updateAirline("IB", airlineRequest);

        ArgumentCaptor<Airline> captor = ArgumentCaptor.forClass(Airline.class);
        verify(airlineRepository).findByCodeIATA("IB");
        verify(airlineRepository).save(captor.capture());
        Airline airlineSaved = captor.getValue();
        assertEquals("IB", airlineResponse.codeIATA());
        assertEquals("Iberia S.A", airlineResponse.name());
        assertEquals("IB", airlineSaved.getCodeIATA());
        assertEquals("Iberia S.A", airlineSaved.getName());
        verifyNoMoreInteractions(airlineRepository);
    }

    @Test
    void testUpdateAirlineNotFound() {
        AirlineRequest airlineRequest= new AirlineRequest("IB", "Iberia S.A");
        when(airlineRepository.findByCodeIATA("IB")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> airlineService.updateAirline("IB", airlineRequest));
        verify(airlineRepository, only()).findByCodeIATA("IB");
        verify(airlineRepository, never()).save(any(Airline.class));
    }

    @Test
    void testDeleteAirlineByCodeIATA() {
        Airline iberia = AirlineTestData.iberia();
        when(airlineRepository.findByCodeIATA("IB")).thenReturn(Optional.of(iberia));

        airlineService.deleteAirlineByCodeIATA("IB");

        verify(airlineRepository, times(1)).findByCodeIATA("IB");
        verify(airlineRepository, times(1)).delete(iberia);
        verifyNoMoreInteractions(airlineRepository);
    }

    @Test
    void testDeleteAirlineByCodeIATANotFound() {
        when(airlineRepository.findByCodeIATA("VL")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> airlineService.deleteAirlineByCodeIATA("VL"));
        verify(airlineRepository, only()).findByCodeIATA("VL");
        verify(airlineRepository, never()).delete(any());
    }
}
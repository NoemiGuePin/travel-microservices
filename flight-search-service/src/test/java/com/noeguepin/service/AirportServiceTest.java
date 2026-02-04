package com.noeguepin.service;

import com.noeguepin.dto.AirportRequest;
import com.noeguepin.dto.AirportResponse;
import com.noeguepin.exception.ResourceAlreadyExistsException;
import com.noeguepin.exception.ResourceNotFoundException;
import com.noeguepin.model.Airport;
import com.noeguepin.repository.AirportRepository;
import com.noeguepin.testdata.AirportTestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import static org.mockito.Mockito.*;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AirportServiceTest {

    @Mock
    private AirportRepository airportRepository;

    @InjectMocks
    private AirportService airportService;

    @Test
    void testGetAirportByCodeIATA() {
        when(airportRepository.findByCodeIATA("MAD")).thenReturn(Optional.of(AirportTestData.madridBarajas()));

        AirportResponse airportResponse = airportService.getAirportByCodeIATA("MAD");

        assertNotNull(airportResponse);
        assertEquals("MAD", airportResponse.codeIATA());
        assertEquals("Adolfo Suárez Madrid-Barajas", airportResponse.name());
        verify(airportRepository, only()).findByCodeIATA("MAD");
    }

    @Test
    void testGetAirportByCodeIATAEmpty() {
        when(airportRepository.findByCodeIATA("MA")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> airportService.getAirportByCodeIATA("MA"));
        verify(airportRepository, only()).findByCodeIATA("MA");
    }

    @Test
    void testGetAirportsByFilters() {
        when(airportRepository.findAirportsByFilters("Barcelona", "España")).
                thenReturn(List.of(AirportTestData.barcelonaElPrat()));

        List<AirportResponse> airportsResponse = airportService.getAirportsByFilters("Barcelona", "España");

        assertEquals(1, airportsResponse.size());
        AirportResponse airportResponse = airportsResponse.get(0);
        assertEquals("BCN", airportResponse.codeIATA());
        assertEquals("Barcelona-El Prat", airportResponse.name());
        assertEquals("Barcelona", airportResponse.city());
        assertEquals("España", airportResponse.country());
        verify(airportRepository, only()).findAirportsByFilters("Barcelona", "España");
    }

    @Test
    void testGetAirportsByFiltersNoResults() {
        when(airportRepository.findAirportsByFilters("Roma", "España")).thenReturn(List.of());

        List<AirportResponse> airportsResponse =  airportService.getAirportsByFilters("Roma", "España");

        assertTrue(airportsResponse.isEmpty());
        verify(airportRepository, only()).findAirportsByFilters("Roma", "España");
    }

    @Test
    void testSaveNewAirport() {
        AirportRequest airportRequest= new AirportRequest("JFK", "John F. Kennedy", "New York", "EEUU");
        when(airportRepository.findByCodeIATA("JFK")).thenReturn(Optional.empty());
        when(airportRepository.save(any(Airport.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AirportResponse airportResponse = airportService.saveNewAirport(airportRequest);

        ArgumentCaptor<Airport> captor = ArgumentCaptor.forClass(Airport.class);
        verify(airportRepository, times(1)).findByCodeIATA("JFK");
        verify(airportRepository).save(captor.capture());
        Airport airportSaved = captor.getValue();
        assertEquals("JFK", airportSaved.getCodeIATA());
        assertEquals("John F. Kennedy", airportSaved.getName());
        assertEquals("New York", airportSaved.getCity());
        assertEquals("EEUU", airportSaved.getCountry());
        assertEquals("JFK", airportResponse.codeIATA());
        assertEquals("John F. Kennedy", airportResponse.name());
        assertEquals("New York", airportResponse.city());
        assertEquals("EEUU", airportResponse.country());
        verifyNoMoreInteractions(airportRepository);
    }

    @Test
    void testSaveNewAirportAlreadyExistingIATACode() {
        AirportRequest airportRequest= new AirportRequest("MAD", "Adolfo Suárez Madrid-Barajas", "Madrid", "España");
        when(airportRepository.findByCodeIATA("MAD")).thenReturn(Optional.of(AirportTestData.madridBarajas()));

        assertThrows(ResourceAlreadyExistsException.class, () -> airportService.saveNewAirport(airportRequest));
        verify(airportRepository, only()).findByCodeIATA("MAD");
        verify(airportRepository, never()).save(any(Airport.class));
    }

    @Test
    void testUpdateAirport() {
        AirportRequest airportRequest= new AirportRequest("MAD", "Adolfo Suárez", "Paris", "Francia");
        when(airportRepository.findByCodeIATA("MAD")).thenReturn(Optional.of(AirportTestData.madridBarajas()));
        when(airportRepository.save(any(Airport.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AirportResponse airportResponse = airportService.updateAirport("MAD", airportRequest);

        ArgumentCaptor<Airport> captor = ArgumentCaptor.forClass(Airport.class);
        verify(airportRepository, times(1)).findByCodeIATA("MAD");
        verify(airportRepository).save(captor.capture());
        Airport airportSaved = captor.getValue();
        assertEquals("MAD", airportSaved.getCodeIATA());
        assertEquals("Adolfo Suárez", airportSaved.getName());
        assertEquals("Paris", airportSaved.getCity());
        assertEquals("Francia", airportSaved.getCountry());
        assertEquals("MAD", airportResponse.codeIATA());
        assertEquals("Adolfo Suárez", airportResponse.name());
        assertEquals("Paris", airportResponse.city());
        assertEquals("Francia", airportResponse.country());
        verify(airportRepository, times(1)).save(any(Airport.class));
        verifyNoMoreInteractions(airportRepository);
    }

    @Test
    void testUpdateAirportNotFound() {
        AirportRequest airportRequest= new AirportRequest("MAD", "Adolfo Suárez", "Paris", "Francia");
        when(airportRepository.findByCodeIATA("MAD")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> airportService.updateAirport("MAD", airportRequest));
        verify(airportRepository, only()).findByCodeIATA("MAD");
        verify(airportRepository, never()).save(any(Airport.class));
    }

    @Test
    void testDeleteAirportByCodeIATA() {
        Airport airport = AirportTestData.madridBarajas();
        when(airportRepository.findByCodeIATA("MAD")).thenReturn(Optional.of(airport));

        airportService.deleteAirportByCodeIATA("MAD");

        verify(airportRepository, times(1)).findByCodeIATA("MAD");
        verify(airportRepository, times(1)).delete(airport);
        verifyNoMoreInteractions(airportRepository);
    }

    @Test
    void testDeleteAirportByCodeIATANotFound() {
        when(airportRepository.findByCodeIATA("MAD")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> airportService.deleteAirportByCodeIATA("MAD"));
        verify(airportRepository, never()).delete(any());
        verifyNoMoreInteractions(airportRepository);
    }
}
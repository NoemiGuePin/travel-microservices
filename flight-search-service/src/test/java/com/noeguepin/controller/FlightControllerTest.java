package com.noeguepin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.noeguepin.dto.AirportRequest;
import com.noeguepin.dto.FlightRequest;
import com.noeguepin.exception.ResourceAlreadyExistsException;
import com.noeguepin.exception.ResourceNotFoundException;
import com.noeguepin.service.FlightService;
import com.noeguepin.testdata.AirportTestData;
import com.noeguepin.testdata.FlightTestData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(FlightController.class)
@AutoConfigureMockMvc(addFilters = false)
class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlightService flightService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetFlightsByFilters() {
    }

    @Test
    void testGetFlightByCodeFlight() {
    }

    @Test
    void testCreateFlight() throws Exception {
        when(flightService.saveNewFlight(any(FlightRequest.class)))
                .thenReturn(FlightTestData.af237Response());

        mockMvc.perform(post("/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(FlightTestData.af237Request())))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/flights/AF237"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.codeFlight").value("AF237"));

        ArgumentCaptor<FlightRequest> captor = ArgumentCaptor.forClass(FlightRequest.class);
        verify(flightService).saveNewFlight(captor.capture());
        String expectedJson = objectMapper.writeValueAsString(FlightTestData.af237Request());
        String actualJson = objectMapper.writeValueAsString(captor.getValue());
        Assertions.assertEquals(expectedJson, actualJson);
        verify(flightService).saveNewFlight(any(FlightRequest.class));

    }

    @Test
    void testCreateFlightCodeFlightAlreadyExists() throws Exception {
        doThrow(new ResourceAlreadyExistsException("Flights", "codeFlight", "AF237"))
                .when(flightService).saveNewFlight(any(FlightRequest.class));

        mockMvc.perform(post("/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(FlightTestData.af237Request())))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail").exists());
        verify(flightService).saveNewFlight(any(FlightRequest.class));
    }

    @Test
    void testCreateFlightInvalidBody() throws Exception {
        mockMvc.perform(post("/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(FlightTestData.af237RequestNoValid())))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(flightService);
    }

    @Test
    void testUpdateFlight() throws Exception {
        when(flightService.updateFlight(eq("AF237"), any(FlightRequest.class)))
                .thenReturn(FlightTestData.af237Response());

        mockMvc.perform(put("/flights/AF237")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(FlightTestData.af237Request())))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.codeFlight").value("AF237"));

        ArgumentCaptor<FlightRequest> captor = Argu

        mentCaptor.forClass(FlightRequest.class);
        verify(flightService).updateFlight(eq("AF237"), captor.capture());
        String expectedJson = objectMapper.writeValueAsString(FlightTestData.af237Request());
        String actualJson = objectMapper.writeValueAsString(captor.getValue());
        Assertions.assertEquals(expectedJson, actualJson);
        verify(flightService).updateFlight(eq("AF237"), any(FlightRequest.class));
    }

    @Test
    void testUpdateAirlineNotFoundException() throws Exception {
        doThrow(new ResourceNotFoundException("Flight", "codeFlight", "DR5674"))
                .when(flightService).updateFlight(eq("DR5674"), any(FlightRequest.class));

        mockMvc.perform(put("/flights/DR5674")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(FlightTestData.af237Request())))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.resource").value("Flight"));
        verify(flightService).updateFlight(eq("DR5674"), any(FlightRequest.class));
    }

    @Test
    void testRreserveSeats() {
    }

    @Test
    void testReleaseSeats() {
    }

    @Test
    void testDeleteFlight() throws Exception {
        doNothing().when(flightService).deleteFlightByCodeFlight("AF237");

        mockMvc.perform(delete("/flights/AF237"))
                .andExpect(status().isNoContent());
        verify(flightService).deleteFlightByCodeFlight("AF237");
    }

    @Test
    void testDeleteFlightNotFoundException() throws Exception {
        doThrow(new ResourceNotFoundException("Flight", "codeFlight", "AD587"))
                .when(flightService).deleteFlightByCodeFlight("AD587");

        mockMvc.perform(delete("/flights/AD587"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").exists());

        verify(flightService).deleteFlightByCodeFlight("AD587");
    }
}
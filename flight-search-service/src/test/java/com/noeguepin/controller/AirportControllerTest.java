package com.noeguepin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.noeguepin.dto.AirlineRequest;
import com.noeguepin.dto.AirportRequest;
import com.noeguepin.dto.AirportResponse;
import com.noeguepin.dto.FlightRequest;
import com.noeguepin.exception.ResourceAlreadyExistsException;
import com.noeguepin.exception.ResourceNotFoundException;
import com.noeguepin.service.AirportService;
import com.noeguepin.testdata.AirlineTestData;
import com.noeguepin.testdata.AirportTestData;
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
import static org.mockito.Mockito.doNothing;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AirportController.class)
@AutoConfigureMockMvc(addFilters = false)
class AirportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AirportService airportService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testgGetAirportsByFilters() {
    }

    @Test
    void testGetAirportByCodeIATA() {
    }

    @Test
    void testSaveNewAirport() throws Exception {
        when(airportService.saveNewAirport(any(AirportRequest.class)))
                .thenReturn(AirportTestData.madridBarajasResponse());

        mockMvc.perform(post("/airports")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(AirportTestData.madridBarajasRequest())))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/airports/MAD"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.codeIATA").value("MAD"));

        ArgumentCaptor<AirportRequest> captor = ArgumentCaptor.forClass(AirportRequest.class);
        verify(airportService).saveNewAirport(captor.capture());
        String expectedJson = objectMapper.writeValueAsString(AirportTestData.madridBarajasRequest());
        String actualJson = objectMapper.writeValueAsString(captor.getValue());
        Assertions.assertEquals(expectedJson, actualJson);
        verify(airportService).saveNewAirport(any(AirportRequest.class));
    }

    @Test
    void testSaveNewAirportIATACodeAlreadyExists() throws Exception {
        doThrow(new ResourceAlreadyExistsException("Airport", "codeIATA", "MAD"))
                .when(airportService).saveNewAirport(any(AirportRequest.class));

        mockMvc.perform(post("/airports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(AirportTestData.madridBarajasRequest())))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail").exists());
        verify(airportService).saveNewAirport(any(AirportRequest.class));
    }

    @Test
    void testSaveNewAirportInvalidBody() throws Exception {
        mockMvc.perform(post("/airports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(AirportTestData.madridBarajasRequestNotValid())))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(airportService);
    }

    @Test
    void testUpdateAirport() throws Exception {
        when(airportService.updateAirport(eq("MAD"), any(AirportRequest.class)))
                .thenReturn(AirportTestData.madridBarajasResponse());

        mockMvc.perform(put("/airports/MAD")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(AirportTestData.madridBarajasRequest())))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.codeIATA").value("MAD"))
                .andExpect(jsonPath("$.name").value("Adolfo Suárez Madrid-Barajas"));

        ArgumentCaptor<AirportRequest> captor = ArgumentCaptor.forClass(AirportRequest.class);
        verify(airportService).updateAirport(eq("MAD"), captor.capture());
        String expectedJson = objectMapper.writeValueAsString(AirportTestData.madridBarajasRequest());
        String actualJson = objectMapper.writeValueAsString(captor.getValue());
        Assertions.assertEquals(expectedJson, actualJson);
        verify(airportService).updateAirport(eq("MAD"), any(AirportRequest.class));
    }

    @Test
    void testUpdateAirlineNotFoundException() throws Exception {
        doThrow(new ResourceNotFoundException("Airports", "codeIATA", "MAC"))
                .when(airportService).updateAirport(eq("MAC"), any(AirportRequest.class));

        mockMvc.perform(put("/airports/MAC")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(AirportTestData.madridBarajasRequest())))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.resource").value("Airports"));
        verify(airportService).updateAirport(eq("MAC"), any(AirportRequest.class));
    }

    @Test
    void testDeleteAirport() throws Exception {
        doNothing().when(airportService).deleteAirportByCodeIATA("MAD");

        mockMvc.perform(delete("/airports/MAD"))
                .andExpect(status().isNoContent());
        verify(airportService).deleteAirportByCodeIATA("MAD");
    }


    @Test
    void testDeleteAirportNotFoundException() throws Exception {
        doThrow(new ResourceNotFoundException("Airport", "codeIATA", "MAR"))
                .when(airportService).deleteAirportByCodeIATA("MAR");

        mockMvc.perform(delete("/airports/MAR"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").exists());

        verify(airportService).deleteAirportByCodeIATA("MAR");
    }
}
package com.noeguepin.controller;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.noeguepin.dto.AirlineRequest;
import com.noeguepin.dto.AirlineResponse;
import com.noeguepin.exception.ResourceAlreadyExistsException;
import com.noeguepin.exception.ResourceNotFoundException;
import com.noeguepin.service.AirlineService;
import com.noeguepin.testdata.AirlineTestData;
import com.noeguepin.testdata.AirportTestData;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Assertions;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AirlineController.class)
@AutoConfigureMockMvc(addFilters = false)
class AirlineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AirlineService airlineService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAirlineByFilterReturnsAll() throws Exception {
        when(airlineService.getAirlineByFilters(isNull() ))
                .thenReturn(List.of(
                        AirlineTestData.airFranceResponse(),
                        AirlineTestData.airEuropaResponse(),
                        AirlineTestData.iberiaResponse()
                ));

        mockMvc.perform(get("/airlines"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].codeIATA").value("AF"))
                .andExpect(jsonPath("$[1].codeIATA").value("AE"))
                .andExpect(jsonPath("$[2].codeIATA").value("IB"));
        verify(airlineService).getAirlineByFilters(null);
    }

    @Test
    void testGetAirlineByFilter() throws Exception {
        when(airlineService.getAirlineByFilters("air" ))
                .thenReturn(List.of(
                        AirlineTestData.airFranceResponse(),
                        AirlineTestData.airEuropaResponse()
                ));

        mockMvc.perform(get("/airlines").param("name" , "air"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].codeIATA").value("AF"))
                .andExpect(jsonPath("$[1].codeIATA").value("AE"));
        verify(airlineService).getAirlineByFilters("air");
    }

    @Test
    void testGetAirlineByFilterReturnsEmptyList() throws Exception {
        when(airlineService.getAirlineByFilters("NotFound"))
                .thenReturn(List.of());

        mockMvc.perform(get("/airlines").param("name", "NotFound"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
        verify(airlineService).getAirlineByFilters("NotFound");
    }

    @Test
    void testGetAirlineByCodeIATA() throws Exception {
        when(airlineService.getAirlineByCodeIATA("IB"))
                .thenReturn(AirlineTestData.iberiaResponse());

        mockMvc.perform(get("/airlines/IB"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codeIATA").value("IB"))
                .andExpect(jsonPath("$.name").value("Iberia"));
        verify(airlineService).getAirlineByCodeIATA("IB");
    }

    @Test
    void testGetAirlineByCodeIATANotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Airline", "codeIATA", "IB"))
                .when(airlineService).getAirlineByCodeIATA("VL");

        mockMvc.perform(get("/airlines/VL"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.status").value(404));
        verify(airlineService).getAirlineByCodeIATA("VL");
    }

    @Test
    void testSaveNewAirline() throws Exception {
        when(airlineService.saveNewAirline(any(AirlineRequest.class)))
                .thenReturn(AirlineTestData.iberiaResponse());

        mockMvc.perform(post("/airlines")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(AirlineTestData.iberiaRequest())))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/airlines/IB"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.codeIATA").value("IB"));

        ArgumentCaptor<AirlineRequest> captor = ArgumentCaptor.forClass(AirlineRequest.class);
        verify(airlineService).saveNewAirline(captor.capture());
        String expectedJson = objectMapper.writeValueAsString(AirlineTestData.iberiaRequest());
        String actualJson = objectMapper.writeValueAsString(captor.getValue());
        Assertions.assertEquals(expectedJson, actualJson);
        verify(airlineService).saveNewAirline(any(AirlineRequest.class));
    }

    @Test
    void testSaveNewAirlineIATACodeAlreadyExists() throws Exception {
        doThrow(new ResourceAlreadyExistsException("Airline", "codeIATA", "IB"))
                .when(airlineService).saveNewAirline(any(AirlineRequest.class));

        mockMvc.perform(post("/airlines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(AirlineTestData.iberiaRequest())))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail").exists());
        verify(airlineService).saveNewAirline(any(AirlineRequest.class));
    }

    @Test
    void testSaveNewAirlineInvalidBody() throws Exception {
        mockMvc.perform(post("/airlines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(AirlineTestData.iberiaRequestNoValid())))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(airlineService);
    }

    @Test
    void testUpdateAirline() throws Exception {
        when(airlineService.updateAirline(eq("IB"), any(AirlineRequest.class)))
                .thenReturn(AirlineTestData.iberiaResponse());

        mockMvc.perform(put("/airlines/IB")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(AirlineTestData.iberiaRequest())))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.codeIATA").value("IB"))
                .andExpect(jsonPath("$.name").value("Iberia"));

        ArgumentCaptor<AirlineRequest> captor = ArgumentCaptor.forClass(AirlineRequest.class);
        verify(airlineService).updateAirline(eq("IB"), captor.capture());
        String expectedJson = objectMapper.writeValueAsString(AirlineTestData.iberiaRequest());
        String actualJson = objectMapper.writeValueAsString(captor.getValue());
        Assertions.assertEquals(expectedJson, actualJson);
        verify(airlineService).updateAirline(eq("IB"), any(AirlineRequest.class));
    }

    @Test
    void testUpdateAirlineNotFoundException() throws Exception {
        doThrow(new ResourceNotFoundException("Airline", "codeIATA", "VL"))
                .when(airlineService).updateAirline(eq("VL"), any(AirlineRequest.class));

        mockMvc.perform(put("/airlines/VL")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(AirlineTestData.vuelingRequest())))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.resource").value("Airline"));
        verify(airlineService).updateAirline(eq("VL"), any(AirlineRequest.class));
    }

    @Test
    void testDeleteAirline() throws Exception {
        doNothing().when(airlineService).deleteAirlineByCodeIATA("IB");

        mockMvc.perform(delete("/airlines/IB"))
                .andExpect(status().isNoContent());
        verify(airlineService).deleteAirlineByCodeIATA("IB");
    }

    @Test
    void testDeleteAirlineNotFoundException() throws Exception {
        doThrow(new ResourceNotFoundException("Airline", "codeIATA", "IB"))
                .when(airlineService).deleteAirlineByCodeIATA("IB");

        mockMvc.perform(delete("/airlines/IB"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.resource").value("Airline"))
                .andExpect(jsonPath("$.field").value("codeIATA"))
                .andExpect(jsonPath("$.value").value("IB"));

        verify(airlineService).deleteAirlineByCodeIATA("IB");
    }


}
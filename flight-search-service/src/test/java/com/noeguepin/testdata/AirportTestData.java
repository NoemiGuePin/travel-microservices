package com.noeguepin.testdata;

import com.noeguepin.dto.AirportRequest;
import com.noeguepin.dto.AirportResponse;
import com.noeguepin.model.Airport;

public class AirportTestData {

    public static Airport madridBarajas() {
        Airport airport = new Airport();
        airport.setId(1L);
        airport.setCodeIATA("MAD");
        airport.setName("Adolfo Suárez Madrid-Barajas");
        airport.setCity("Madrid");
        airport.setCountry("España");
        return airport;
    }

    public static Airport barcelonaElPrat() {
        Airport airport = new Airport();
        airport.setId(2L);
        airport.setCodeIATA("BCN");
        airport.setName("Barcelona-El Prat");
        airport.setCity("Barcelona");
        airport.setCountry("España");
        return airport;
    }

    public static Airport charlesDeGaulle() {
        Airport airport = new Airport();
        airport.setId(3L);
        airport.setCodeIATA("CDG");
        airport.setName("Charles de Gaulle");
        airport.setCity("París");
        airport.setCountry("Francia");
        return airport;
    }

    public static AirportRequest madridBarajasRequest() {
        return new AirportRequest(
                "MAD",
                "Adolfo Suárez Madrid-Barajas",
                "Madrid",
                "España"
        );
    }

    public static AirportRequest madridBarajasRequestNotValid() {
        return new AirportRequest(
                "",
                "Adolfo Suárez Madrid-Barajas",
                "Madrid",
                "España"
        );
    }

    public static AirportResponse madridBarajasResponse() {
        return new AirportResponse(
                1L,
                "MAD",
                "Adolfo Suárez Madrid-Barajas",
                "Madrid",
                "España"
        );
    }
}

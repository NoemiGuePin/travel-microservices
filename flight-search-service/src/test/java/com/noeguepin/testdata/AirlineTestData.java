package com.noeguepin.testdata;

import com.noeguepin.dto.AirlineRequest;
import com.noeguepin.dto.AirlineResponse;
import com.noeguepin.model.Airline;

import java.util.List;

public class AirlineTestData {

    public static Airline iberia() {
        Airline iberia = new Airline();
        iberia.setId(1L);
        iberia.setCodeIATA("IB");
        iberia.setName("Iberia");
        return iberia;
    }

    public static Airline airEuropa() {
        Airline airEuropa = new Airline();
        airEuropa.setId(2L);
        airEuropa.setCodeIATA("AE");
        airEuropa.setName("Air Europa");
        return airEuropa;
    }

    public static Airline airFrance() {
        Airline airFrance = new Airline();
        airFrance.setId(3L);
        airFrance.setCodeIATA("AF");
        airFrance.setName("Air France");
        return airFrance;
    }

    public static AirlineRequest iberiaRequest(){
        return  new AirlineRequest(
                "IB",
                "Iberia");
    }

    public static AirlineRequest iberiaRequestNoValid(){
        return  new AirlineRequest(
                "",
                "Iberia");
    }

    public static AirlineRequest vuelingRequest(){
        return  new AirlineRequest(
                "VL",
                "Vueling");
    }

   public static AirlineResponse iberiaResponse(){
       return new AirlineResponse(
               1L,
               "IB",
               "Iberia"
       ) ;
   }

    public static AirlineResponse airEuropaResponse(){
        return new AirlineResponse(
                2L,
                "AE",
                "Air Europa"
        ) ;
    }

    public static AirlineResponse airFranceResponse(){
        return new AirlineResponse(
                3L,
                "AF",
                "Air France"
        ) ;
    }

}

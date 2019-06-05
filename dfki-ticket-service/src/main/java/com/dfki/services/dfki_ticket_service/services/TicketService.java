package com.dfki.services.dfki_ticket_service.services;

import be.ugent.rml.store.QuadStore;
import com.dfki.services.dfki_ticket_service.Utils;
import com.dfki.services.dfki_ticket_service.models.Ticket;
import com.dfki.services.dfki_ticket_service.repositories.TicketRepo;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.query.algebra.Str;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class TicketService {
    private TicketRepo ticketRepo;
    private final static String[] VDV_SERVICE_URIs = {"http://localhost:8802/vdv/ticket",
            "http://192.168.99.100:8802/vdv/ticket"};
    public final static String VDV_SERVICE_URI = VDV_SERVICE_URIs[0];
//    private final static String vdv_ticket_service_url_localhost = "http://localhost:8802/vdv/ticket";
//    private final static String vdv_ticket_service_url_docker = "http://192.168.99.100:8802/vdv/ticket";

    public TicketService() {
        ticketRepo = new TicketRepo();
    }

    public Ticket save(String rdfInput) throws IOException {
        Model model = Utils.turtleToRDFConverter(rdfInput);
        Map<String, String> prefixes = Utils.parsePrefixes(rdfInput);
        if (ticketRepo.save(model)) {
            Ticket ticket = Utils.getTicketFromDB(new Ticket(), ticketRepo);
            ticket.setPrefixes(prefixes);
            return ticket;
        }
        return null;
    }

    public String toJson(Ticket ticket) {
        return Utils.convertObjectToJson(ticket);
    }

    public String toXml(Ticket ticket) {
        return Utils.convertObjectToXML(ticket);
    }

    public void postToVdvService(Ticket ticket) throws IOException {

        Utils.sendXMLPostRequest(VDV_SERVICE_URI, toXml(ticket));
    }

    public String xmlToRdf(String xmlString) throws Exception {
        String mappingFile = "xml_mapping.ttl";
        String fileName = "C:/Workspaces/DFKI_Ticket_Service/src/main/resources/xml_text.xml";
        Utils.writeTextToFile(fileName, xmlString);
        String result = Utils.mapToRDF(mappingFile);
        return result;
    }

    public String jsonToRdf(String jsonString) throws Exception {
        String mappingFile = "json_mapping.ttl";
        String fileName = "C:/Workspaces/DFKI_Ticket_Service/src/main/resources/json_text.json";
        Utils.writeTextToFile(fileName, jsonString);
        String result = Utils.mapToRDF(mappingFile);
        return result;
    }
}
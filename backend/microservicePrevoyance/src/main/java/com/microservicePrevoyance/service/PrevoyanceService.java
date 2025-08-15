// src/main/java/com/medilabo/aggregator/service/PrevoyanceService.java
package com.microservicePrevoyance.service;

import com.microservicePrevoyance.client.NotesClient;
import com.microservicePrevoyance.model.PrevoyanceProps;
import com.microservicePrevoyance.model.NoteDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PrevoyanceService {

    private final NotesClient notesClient;
    private final KeywordRiskEngine engine;
    private final PrevoyanceProps props;

    public PrevoyanceService(NotesClient notesClient, KeywordRiskEngine engine, PrevoyanceProps props) {
        this.notesClient = notesClient;
        this.engine = engine;
        this.props = props;
    }

    public RiskResponse computeRisk(Integer patientId) {
        int page = 0, size = 100;
        List<NoteDto> buffer = new ArrayList<>();
        while (buffer.size() < props.getMaxNotes()) {
            List<NoteDto> chunk = notesClient.listNotes(patientId, page, size);
            if (chunk == null || chunk.isEmpty()) break;
            buffer.addAll(chunk);
            // on peut short-circuiter si 3 mots-clés déjà trouvés
            var interim = engine.evaluate(buffer);
            if (interim.matchedCount() >= 3) {
                return new RiskResponse(patientId, interim.status(), interim.matchedCount(), interim.matchedKeywords());
            }
            if (chunk.size() < size) break; // plus de pages
            page++;
        }
        var res = engine.evaluate(buffer);
        return new RiskResponse(patientId, res.status(), res.matchedCount(), res.matchedKeywords());
    }

    public record RiskResponse(Integer patientId, String status, int matchedCount, List<String> matchedKeywords) {}
}

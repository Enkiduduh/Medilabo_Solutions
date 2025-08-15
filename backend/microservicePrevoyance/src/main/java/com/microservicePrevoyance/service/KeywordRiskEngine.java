package com.microservicePrevoyance.service;

import com.microservicePrevoyance.model.NoteDto;
import com.microservicePrevoyance.model.PrevoyanceProps;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;
import java.text.Normalizer;

@Component
public class KeywordRiskEngine {

    private final Set<String> keywords;

    public KeywordRiskEngine(PrevoyanceProps props) {
        this.keywords = new HashSet<>();
        for (String k : props.getKeywords()) {
            this.keywords.add(normalize(k));
        }
    }

    public static String normalize(String s) {
        if (s == null) return "";
        String n = Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", ""); // retire les diacritiques
        return n.toLowerCase(Locale.FRENCH);
    }

    public Result evaluate(Collection<NoteDto> notes) {
        Set<String> matched = new HashSet<>();
        if (notes != null) {
            for (NoteDto n : notes) {
                String text = normalize(n.content());
                for (String k : keywords) {
                    // mot entier : \bkeyword\b
                    Pattern p = Pattern.compile("\\b" + Pattern.quote(k) + "\\b");
                    if (p.matcher(text).find()) {
                        matched.add(k);
                    }
                }
                if (matched.size() >= 3) break; // short-circuit
            }
        }
        int m = matched.size();
        String status = (m >= 8) ? "EarlyOnset" : (m == 6 || m == 7) ? "inDanger" : (m == 2 || m == 3 || m == 4 || m == 5) ? "early" : "none";
        return new Result(status, m, new ArrayList<>(matched));
    }

    public record Result(String status, int matchedCount, List<String> matchedKeywords) {
    }
}


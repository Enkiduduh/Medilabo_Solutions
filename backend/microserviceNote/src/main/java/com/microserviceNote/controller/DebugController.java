package com.microserviceNote.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @GetMapping("/session")
    public Map<String, Object> session(HttpSession session, Authentication auth) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("id", session.getId());
        out.put("creationTime", session.getCreationTime());      // long (epoch ms)
        out.put("lastAccessedTime", session.getLastAccessedTime());
        out.put("maxInactiveInterval", session.getMaxInactiveInterval()); // seconds
        List<String> attrNames = Collections.list(session.getAttributeNames());
        out.put("attrs", attrNames);
        out.put("hasSecurityContext", session.getAttribute("SPRING_SECURITY_CONTEXT") != null);
        out.put("principal", auth != null ? auth.getName() : null);
        return out;
    }
}

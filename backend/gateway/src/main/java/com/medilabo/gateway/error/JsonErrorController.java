package com.medilabo.gateway.error;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
class JsonErrorController implements org.springframework.boot.web.servlet.error.ErrorController {
    @RequestMapping("/error")
    public Map<String, Object> error(HttpServletRequest req) {
        var status = (Integer) req.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        return Map.of(
                "timestamp", Instant.now().toString(),
                "status", status == null ? 500 : status,
                "path", (String) req.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)
        );
    }
}
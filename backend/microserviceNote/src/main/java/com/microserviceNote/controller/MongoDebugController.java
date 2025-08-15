package com.microserviceNote.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.bson.Document;
import java.util.*;

@RestController
@RequestMapping("/api/debug")
public class MongoDebugController {
    private final MongoTemplate template;
    public MongoDebugController(MongoTemplate template) { this.template = template; }

    @GetMapping("/mongo")
    public Map<String,Object> mongo() {
        var out = new LinkedHashMap<String,Object>();
        out.put("db", template.getDb().getName());
        out.put("collections", template.getDb().listCollectionNames().into(new ArrayList<>()));
        out.put("notesCount", template.getCollection("notes").countDocuments());
        return out;
    }

    @GetMapping("/notes/sample")
    public List<Document> sample() {
        return template.getCollection("notes").find().limit(3).into(new ArrayList<>());
    }
}

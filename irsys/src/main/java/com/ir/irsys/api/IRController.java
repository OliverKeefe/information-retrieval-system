package com.ir.irsys.api;

import com.ir.irsys.application.DocumentScore;
import com.ir.irsys.application.Evaluator;
import com.ir.irsys.application.Ranker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.ir.irsys.application.Evaluator.calculateRecall;

@RestController
@RequestMapping("/api")
public class IRController {

    private final Ranker ranker;

    Evaluator evaluator;

    /**
     * Dependency injection in Spring Boot is best handled via
     * constructor instead of Jakarta @Inject like Quarkus.
     */
    public IRController(Ranker ranker) {
        this.ranker = ranker;
    }

    @GetMapping("/query")
    public List<DocumentScore> query(@RequestParam("q") String q) {
        return ranker.rankDocumentsForQuery(q);
    }

    //@GetMapping("/recall")
    //public Double recall() {
    //    return calculateRecall()
    //}
}

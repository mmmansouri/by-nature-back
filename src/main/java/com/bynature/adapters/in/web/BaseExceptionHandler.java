package com.bynature.adapters.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.net.URI;
import java.time.Instant;

public abstract class BaseExceptionHandler {

    protected ProblemDetail createProblemDetail(HttpStatus status, String title, String detail, URI type) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setType(type);
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

}

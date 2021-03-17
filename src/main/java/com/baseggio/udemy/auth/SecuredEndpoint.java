package com.baseggio.udemy.auth;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/secured")
public class SecuredEndpoint {

    @Get("/status")
    public List<Object> getStatus(final Principal principal) {
        Authentication auth = (Authentication) principal;
        return Arrays.asList(
            auth.getAttributes().get("hair_color"),
            auth.getAttributes().get("language"),
            auth.getName());

    }

}

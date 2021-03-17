package com.baseggio.udemy.auth;

import com.baseggio.udemy.auth.persistence.UserEntity;
import com.baseggio.udemy.auth.persistence.UserRepository;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.*;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

@Slf4j
@Singleton
public class JDBCAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;

    public JDBCAuthenticationProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Publisher<AuthenticationResponse> authenticate(
            @Nullable HttpRequest<?> httpRequest,
            AuthenticationRequest<?, ?> authenticationRequest) {

        return Flowable.create( emitter -> {
            final String identity = (String) authenticationRequest.getIdentity();
            log.debug("User {} tries to login.", identity);
            final Optional<UserEntity> maybeUser = userRepository.findByEmail(identity);

            if (maybeUser.isPresent()) {
                log.debug("Found user: {}", maybeUser.get().getEmail());
                final String secret = (String) authenticationRequest.getSecret();
                if(maybeUser.get().getPassword().equals(secret)) {
                    log.debug("User logged in.");
                    HashMap<String, Object> attr = new HashMap<>();
                    attr.put("hair_color", "brown");
                    attr.put("language", "english");
                    final UserDetails user = new UserDetails(
                            identity,
                            Collections.singletonList("ROLE_USER"),
                            attr);
                    emitter.onNext(user);
                    emitter.onComplete();
                    return;
                } else {
                    log.debug("Wrong password provided for user {}", identity);
                }
            } else {
                log.debug("No user found with email: {}", identity);
            }
            emitter.onError(new AuthenticationException(new AuthenticationFailed("Wrong username or password.")));
        }, BackpressureStrategy.ERROR);


    }
}

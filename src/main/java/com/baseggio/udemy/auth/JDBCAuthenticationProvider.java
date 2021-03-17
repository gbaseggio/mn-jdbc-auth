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
            final Optional<UserEntity> userEntityOptional = userRepository.findByEmail(identity);
            userEntityOptional.ifPresent(userEntity -> {
                log.debug("Found user: {}", userEntity.getEmail());
            });
            emitter.onError(new AuthenticationException(new AuthenticationFailed("Wrong username or password.")));
        }, BackpressureStrategy.ERROR);


    }
}

package com.baseggio.udemy.auth;

import com.baseggio.udemy.auth.persistence.UserEntity;
import com.baseggio.udemy.auth.persistence.UserRepository;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;

@Singleton
@Slf4j
public class TestDataProvider {

    private final UserRepository users;

    public TestDataProvider(UserRepository users) {
        this.users = users;
    }

    @EventListener
    public void init(StartupEvent event) {
        if(users.findByEmail("alice@example.com").isEmpty()) {
            log.debug("User alice added as test.");
            final UserEntity user = new UserEntity();
            user.setEmail("alice@example.com");
            user.setPassword("secret");
            users.save(user);
        }
    }
}

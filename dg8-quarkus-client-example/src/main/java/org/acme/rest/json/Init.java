package org.acme.rest.json;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.commons.configuration.XMLStringConfiguration;
import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class Init {

    public static final String GAME_CACHE = "games"; //First we specify a class level variable which is the name of our Cache.

    //We inject the cacheManager to our file. We only want to load the CacheManager once, and since its a heavy object, we want to do it at startup.
    @Inject
    RemoteCacheManager cacheManager; 

//we can also configure a cache with xml, we are exactly doing that here. 
// We could have also loaded this from a file META-INF but for a short demo this works okay.
    private static final String CACHE_CONFIG = 
            "<infinispan><cache-container>" +
                    "<distributed-cache name=\"%s\"></distributed-cache>" +
                    "</cache-container></infinispan>";

    void onStart(@Observes @Priority(value = 1) StartupEvent ev) {
        String xml = String.format(CACHE_CONFIG, "games"); 
        cacheManager.administration().getOrCreateCache(GAME_CACHE, new XMLStringConfiguration(xml)); 
    }
}

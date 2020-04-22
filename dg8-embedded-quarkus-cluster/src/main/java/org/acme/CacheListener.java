
/**
 * 
 * Red Hat Data Grid offers a listener API, where clients can register for and get notified when events take place. This annotation-driven API applies to 2 different levels: cache level events and cache manager level events.
Events trigger a notification which is dispatched to listeners. Listeners are simple POJO s annotated with @Listener and registered using the methods defined in the Listenable interface.
Both Cache and CacheManager implement Listenable, which means you can attach listeners to either a cache or a cache manager, to receive either cache-level or cache manager-level notifications.
 */
package org.acme;

import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;

//@Listener(clustered=true) // clustered listener, listen clusterwide on every node
@Listener(clustered=false) // no longer listen to events clusterwide, just on the present node
public class CacheListener {

    @CacheEntryCreated
    public void entryCreated(CacheEntryCreatedEvent<String, Score> event) {
        System.out.printf("-- Entry for %s created \n", event.getType());
    }

    @CacheEntryModified
    public void entryUpdated(CacheEntryModifiedEvent<String, Score> event){
        System.out.printf("-- Entry for %s modified\n", event.getType());
    }
}
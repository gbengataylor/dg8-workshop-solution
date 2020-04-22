package org.acme;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import io.quarkus.runtime.StartupEvent;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.StorageType;
import org.infinispan.distribution.group.Grouper;
import org.infinispan.eviction.EvictionType;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;

@ApplicationScoped
public class ScoreService {
    Cache<Object, Score> scoreCache; 

    Logger log = LoggerFactory.getLogger(ScoreService.class); 

    @Inject
    EmbeddedCacheManager cacheManager; 


    public List<Score> getAll() { 
        return new ArrayList<>(scoreCache.values());
    }

    public void save(Score entry) { 
        ///no expiration
        scoreCache.put(getKey(entry), entry);
        //add expiration, expire THIS entry after 5 seconds, see below for cache wide
        //scoreCache.put(getKey(entry), entry, 5, TimeUnit.SECONDS);
    }

    public void delete(Score entry) { 
        scoreCache.remove(getKey(entry));
    }

    public void getEntry(Score entry){ 
        scoreCache.get(getKey(entry));
    }
     public static String getKey(Score entry){
        return entry.getPlayerId()+","+entry.getCourse();
    }

    public Score findById(String key) {
        return scoreCache.get(key);
    }

    /*
    A CacheManager is a fairly heavy-weight component, and you will probably want to initialize it
     early on in your application lifecycle. For that reason we use the onStart method in this 
     Service to ensure that the CacheManager and Cache are both created at startup. 
     This also benefits us when we change this to clustering mode, more on that in our next lab.
    */
    void onStart(@Observes @Priority(value = 1) StartupEvent ev){
        // global config for cluster
        /*
        We define a global configuration, since we are going to be in a clustered mode, hence everytime a new instance of our app will be created it will have the global configuration parameters to talk to the other nodes if they are present.
        */
       GlobalConfigurationBuilder global = GlobalConfigurationBuilder.defaultClusteredBuilder();

       //	Infinispan nodes rely on a transport layer to join and leave clusters as well as to replicate data across the network. Infinispan uses JGroups technology to handle cluster transport. You configure cluster transport with JGroups stacks, which define properties for either UDP or TCP protocols. Here we have defined a unique clusterName for our app’s embedded cache
        global.transport().clusterName("ScoreCard");
        cacheManager = new DefaultCacheManager(global.build());
        ConfigurationBuilder config = new ConfigurationBuilder(); 

        // add 5 mins expiration across ScoreCard cluster
     //   config.expiration().lifespan(5, TimeUnit.MINUTES)
       //         .clustering().cacheMode(CacheMode.DIST_SYNC); // We setup a distributed cache, which means that not all our nodes will have all the keys.

                // replicated cache rather than distributed
        config.expiration().lifespan(5, TimeUnit.MINUTES)
                .clustering().cacheMode(CacheMode.REPL_SYNC);

        cacheManager.defineConfiguration("scoreboard", config.build()); 
        scoreCache = cacheManager.getCache("scoreboard"); 

        // register a cache listener to the cache
        scoreCache.addListener(new CacheListener());

        log.info("Cache initialized");

    }
}
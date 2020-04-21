package org.acme;

import org.infinispan.AdvancedCache;
import org.infinispan.functional.EntryView;
import org.infinispan.functional.FunctionalMap;
import org.infinispan.functional.MetaParam.MetaLifespan;
import org.infinispan.functional.Traversable;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.functional.impl.FunctionalMapImpl;
import org.infinispan.functional.impl.ReadOnlyMapImpl;
import org.infinispan.functional.impl.ReadWriteMapImpl;
import org.infinispan.functional.impl.WriteOnlyMapImpl;
import org.infinispan.manager.DefaultCacheManager;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

// Inifinispan Functional
public class Exercise3 {

    public static void main(String[] args) throws Exception {
    //* UNCOMMENT When starting this exercise

      /*
      Lets start by initializing our Cache with the DefaultCacheManager as we have done so in the 
      previous labs. However we will use the functional API and hence after getting the Cache our Map
       implementation will be different. How to use the Functional API? Using an asynchronous API, 
       all methods that return a single result, return a CompletableFuture which wraps the result. 
       To avoid blocking, it offers the possibility to receive callbacks when the CompletableFuture 
       has completed, or it can be chained or composes with other CompletableFuture instances.

*/
      DefaultCacheManager cacheManager = new DefaultCacheManager();
        cacheManager.defineConfiguration("local", new ConfigurationBuilder().build());
        AdvancedCache<String, String> cache = cacheManager.<String, String>getCache("local").getAdvancedCache();
        FunctionalMapImpl<String, String> functionalMap = FunctionalMapImpl.create(cache);
        FunctionalMap.WriteOnlyMap<String, String> writeOnlyMap = WriteOnlyMapImpl.create(functionalMap);
        FunctionalMap.ReadOnlyMap<String, String> readOnlyMap = ReadOnlyMapImpl.create(functionalMap);

        // TODO Execute two parallel write-only operation to store key/value pairs
        CompletableFuture<Void> writeFuture1 = writeOnlyMap.eval("key1", "value1",
                (v, writeView) -> writeView.set(v)); 
        CompletableFuture<Void> writeFuture2 = writeOnlyMap.eval("key2", "value2",
                (v, writeView) -> writeView.set(v));

        //TODO When each write-only operation completes, execute a read-only operation to retrieve the value
        CompletableFuture<String> readFuture1 =
                writeFuture1.thenCompose(r -> readOnlyMap.eval("key1", EntryView.ReadEntryView::get)); 
        CompletableFuture<String> readFuture2 =
                writeFuture2.thenCompose(r -> readOnlyMap.eval("key2", EntryView.ReadEntryView::get));

        // TODO When the read-only operation completes, print it out

        System.out.printf("Created entries: %n");
        CompletableFuture<Void> end = readFuture1.thenAcceptBoth(readFuture2, (v1, v2) ->
                System.out.printf("key1 = %s%nkey2 = %s%n", v1, v2));

        // Wait for this read/write combination to finish
        end.get();

        // Create a read-write map
        FunctionalMap.ReadWriteMap<String, String> readWriteMap = ReadWriteMapImpl.create(functionalMap);

        
        // Use read-write multi-key based operation to write new values
        // together with lifespan and return previous values
        //TODO Create a read-write map
        // Use read-write multi-key based operation to write new values
        // together with lifespan and return previous values
        Map<String, String> data = new HashMap<>();
        data.put("key1", "newValue1");
        data.put("key2", "newValue2");
        Traversable<String> previousValues = readWriteMap.evalMany(data, (v, readWriteView) -> {
            String prev = readWriteView.find().orElse(null);
            readWriteView.set(v, new MetaLifespan(Duration.ofHours(1).toMillis()));
            return prev;
        });

        cacheManager.stop();
        System.exit(0);

       // UNCOMMENT When starting this exercise */
    }

}
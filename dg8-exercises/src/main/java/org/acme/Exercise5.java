package org.acme;

import org.infinispan.Cache;
import org.infinispan.commons.api.CacheContainerAdmin;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.transaction.TransactionMode;

import javax.transaction.TransactionManager;

/*
Transactions are important in any business application. Usually transaction is used with dataset, and quite often related to a database, but thats not exactly true, if you have a distributed dataset, you will need transations for your business logic to prevail. Infinspan provides transations. You might have a scenario where the cluster adds a node, or where an entry has been written on another node. The infinispan transaction manager is aware of such events and handles them. You can read more about the design of transactions here: https://github.com/infinispan/infinispan-designs

*/

public class Exercise5 {


    public static void main(String[] args) throws Exception {

       // /* UNCOMMENT When starting this exercise

        // Construct a local cache manager
        DefaultCacheManager cacheManager = new DefaultCacheManager();
        // Create a transaction cache config
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.transaction().transactionMode(TransactionMode.TRANSACTIONAL);
        Configuration cacheConfig = builder.build();
        // Create a cache with the config
        Cache<String, String> cache = cacheManager.administration()
                .withFlags(CacheContainerAdmin.AdminFlag.VOLATILE)
                .getOrCreateCache("cache", cacheConfig);
        
        //TODO Obtain the transaction manager
        TransactionManager transactionManager = cache.getAdvancedCache().getTransactionManager();
            
        // TODO Perform some operations within a transaction and commit it
        transactionManager.begin();
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        transactionManager.commit();
        


        // Display the current cache contents
        System.out.printf("key1 = %s\nkey2 = %s\n", cache.get("key1"), cache.get("key2"));
        
        //TODO Perform some operations within a transaction and roll it back
        //TODO Perform some operations within a transaction and roll it back
        transactionManager.begin();
        cache.put("key1", "value3");
        cache.put("key2", "value4");
        transactionManager.rollback();
        
        // Display the current cache contents
        System.out.printf("key1 = %s\nkey2 = %s\n", cache.get("key1"), cache.get("key2"));
        
        // Stop the cache manager and release all resources
        cacheManager.stop();

        System.exit(0);
      //  UNCOMMENT When starting this exercise */
    }

}

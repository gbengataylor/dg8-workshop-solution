package org.acme.rest.json;

import io.quarkus.infinispan.client.Remote;
import io.reactivex.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.infinispan.client.hotrod.RemoteCache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.acme.rest.json.Init.GAME_CACHE;

@Path("/games")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GameResource {

    @Inject
    @Remote(GAME_CACHE)
    RemoteCache<String, Game> gameStore;

    @DELETE
    public Game delete(Game game) {
        //gameStore.removeAsync(removeIf(existingGame -> existingGame.name.contentEquals(game.name));
        return null;
    }

    @GET
    public Set<Game> list() {
        return new HashSet<>(gameStore.values());
    }

    @POST
    public Set<String> add(Game game) {
        //add method is using the Async api of infinispan/Red Hat Data Grid to add the entry into the cache
        gameStore.putAsync(game.getName(), game);
        return gameStore.keySet();
    }
}


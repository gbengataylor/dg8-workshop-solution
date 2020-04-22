package org.acme.rest.json;

import java.io.IOException;

import org.infinispan.protostream.MessageMarshaller;

public class GameMarshaller implements MessageMarshaller<Game>{

    //. In the following code we specify how we are going to read from our ProtoStream.
    // We could add any additional processing on the stream if we wanted to. 
    //For now we take a simplified read and return a Game Object.
    // Hence everytime a stream is read from the Cache, this method will be called.
    @Override
    public Game readFrom(MessageMarshaller.ProtoStreamReader reader) throws IOException {
        String name = reader.readString("name");
        String description = reader.readString("description");
        return new Game(name, description);
    }
    // writer method
    @Override
    public void writeTo(MessageMarshaller.ProtoStreamWriter writer, Game game) throws IOException {
        writer.writeString("name", game.getName());
        writer.writeString("description", game.getDescription());
    }
    @Override
    public Class<? extends Game> getJavaClass() {
        return Game.class;
    }

    @Override
    public String getTypeName() {
        return "quickstart.Game";
    }
}

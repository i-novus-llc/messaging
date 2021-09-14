package ru.inovus.messaging.mq.support.kafka;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import ru.inovus.messaging.api.model.Message;

import java.io.*;
import java.util.Map;

public class ObjectSerializer implements Serializer<Message>, Deserializer<Message> {

    @Override
    public Message deserialize(String topic, byte[] data) {
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(data);
            ObjectInputStream objectInputStream = new ObjectInputStream(stream);
            return (Message) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, Message data) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(stream);
            objectOutputStream.writeObject(data);
            objectOutputStream.close();
            return stream.toByteArray();
        } catch (IOException e) {
            return new byte[0];
        }
    }

    @Override
    public void close() {

    }
}

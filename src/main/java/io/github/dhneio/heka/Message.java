package io.github.dhneio.heka;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.protobuf.ByteString;
import io.github.dhneio.heka.client.Protobuf;
import io.github.dhneio.heka.json.Base64Serializer;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class Message {
    private final Protobuf.Message message;

    private Message(Protobuf.Message message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) { return false; }
        if (other == this) { return true; }
        if (!(other instanceof Message)) { return false; }

        Message otherMsg = (Message)other;

        return message.toByteString().equals(otherMsg.message.toByteString());
    }

    @Override
    public int hashCode() {
        return message.toByteString().hashCode();
    }

    @JsonIgnore
    public UUID getUuid() {
        ByteString bs = getUuidByteString();
        ByteBuffer buf = bs.asReadOnlyByteBuffer();
        return new UUID(buf.getLong(), buf.getLong());
    }

    @JsonProperty("uuid")
    @JsonSerialize(using = Base64Serializer.class)
    ByteString getUuidByteString() {
        return message.getUuid();
    }

    public long getTimestamp() {
        return message.getTimestamp();
    }

    public String getType() {
        return message.hasType() ? message.getType() : null;
    }

    public String getLogger() {
        return message.hasLogger() ? message.getLogger() : null;
    }

    public Integer getSeverity() {
        return message.hasSeverity() ? message.getSeverity() : null;
    }

    public String getPayload() {
        return message.hasPayload() ? message.getPayload() : null;
    }

    @JsonProperty("env_version")
    public String getEnvVersion() {
        return message.hasEnvVersion() ? message.getEnvVersion() : null;
    }

    public Integer getPid() {
        return message.hasPid() ? message.getPid() : null;
    }

    public String getHostname() {
        return message.hasHostname() ? message.getHostname() : null;
    }

    byte[] toByteArray() {
        return message.toByteArray();
    }

    @JsonProperty("fields")
    private List<Protobuf.Field> getFieldsList() {
        return message.getFieldsList();
    }

    public static class Builder {
        private MessageDefaultsProvider defaults;
        private final Protobuf.Message.Builder builder;

        public Builder() {
            this.builder = Protobuf.Message.newBuilder();
        }

        public Builder defaults(MessageDefaultsProvider defaults) {
            this.defaults = defaults;
            return this;
        }

        public Builder uuid(UUID uuid) {
            if (uuid == null) {
                builder.clearUuid();
            } else {
                ByteBuffer buf = ByteBuffer.allocate(16);
                buf.putLong(uuid.getMostSignificantBits());
                buf.putLong(uuid.getLeastSignificantBits());
                builder.setUuid(ByteString.copyFrom(buf.array()));
            }
            return this;
        }

        public Builder timestamp(Long ts) {
            if (ts == null) {
                builder.clearTimestamp();
            } else {
                builder.setTimestamp(ts);
            }
            return this;
        }

        public Builder type(String type) {
            if (type == null) {
                builder.clearType();
            } else {
                builder.setType(type);
            }
            return this;
        }

        public Builder logger(String logger) {
            if (logger == null) {
                builder.clearLogger();
            } else {
                builder.setLogger(logger);
            }
            return this;
        }

        public Builder severity(Integer severity) {
            if (severity == null) {
                builder.clearSeverity();
            } else {
                builder.setSeverity(severity);
            }
            return this;
        }

        public Builder payload(String payload) {
            if (payload == null) {
                builder.clearPayload();
            } else {
                builder.setPayload(payload);
            }
            return this;
        }

        public Builder envVersion(String envVersion) {
            if (envVersion == null) {
                builder.clearEnvVersion();
            } else {
                builder.setEnvVersion(envVersion);
            }
            return this;
        }

        public Builder pid(Integer pid) {
            if (pid == null) {
                builder.clearPid();
            } else {
                builder.setPid(pid);
            }
            return this;
        }

        public Builder hostname(String hostname) {
            if (hostname == null) {
                builder.clearHostname();
            } else {
                builder.setHostname(hostname);
            }
            return this;
        }

        public Builder field(Protobuf.Field field) {
            builder.addFields(field);
            return this;
        }

        public Builder field(String name, String value) {
            Protobuf.Field field = Protobuf.Field.newBuilder()
                    .setName(name)
                    .clearValueString()
                    .addValueString(value)
                    .build();
            return this.field(field);
        }

        public Builder field(String name, int value) {
            Protobuf.Field field = Protobuf.Field.newBuilder()
                    .setName(name)
                    .setValueType(Protobuf.Field.ValueType.INTEGER)
                    .clearValueInteger()
                    .addValueInteger(value)
                    .build();
            return this.field(field);
        }

        public Builder field(String name, double value) {
            Protobuf.Field field = Protobuf.Field.newBuilder()
                    .setName(name)
                    .setValueType(Protobuf.Field.ValueType.DOUBLE)
                    .clearValueDouble()
                    .addValueDouble(value)
                    .build();
            return this.field(field);
        }

        public Builder field(String name, boolean value) {
            Protobuf.Field field = Protobuf.Field.newBuilder()
                    .setName(name)
                    .setValueType(Protobuf.Field.ValueType.BOOL)
                    .clearValueBool()
                    .addValueBool(value)
                    .build();
            return this.field(field);
        }

        public Builder incr(int value) {
            return this.payload(Integer.toString(value));
        }

        public Builder rate(double value) {
            return this.field("rate", value);
        }

        private void applyDefaults() {
            if (defaults == null) {
                return;
            }

            if (!builder.hasUuid()) {
                uuid(defaults.getUuid());
            }
            if (!builder.hasTimestamp()) {
                timestamp(defaults.getTimestamp());
            }
            if (!builder.hasType()) {
                type(defaults.getType());
            }
            if (!builder.hasLogger()) {
                logger(defaults.getLogger());
            }
            if (!builder.hasSeverity()) {
                severity(defaults.getSeverity());
            }
            if (!builder.hasPayload()) {
                payload(defaults.getPayload());
            }
            if (!builder.hasEnvVersion()) {
                envVersion(defaults.getEnvVersion());
            }
            if (!builder.hasPid()) {
                pid(defaults.getPid());
            }
            if (!builder.hasHostname()) {
                hostname(defaults.getHostname());
            }
        }

        public Message build() {
            applyDefaults();
            return new Message(builder.build());
        }
    }
}

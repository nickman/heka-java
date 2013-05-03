package io.github.dhneio.heka.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.dhneio.heka.client.Protobuf.Field;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import static com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonAutoDetect(fieldVisibility = Visibility.NONE,
                getterVisibility = Visibility.NONE,
                setterVisibility = Visibility.NONE,
                isGetterVisibility = Visibility.NONE)
abstract class FieldMixIn {
    @JsonProperty("name")
    public abstract String getName();

    @JsonProperty("value_format")
    @JsonSerialize(using = FieldValueFormatSerializer.class)
    public abstract Field.ValueFormat getValueFormat();

    @JsonProperty("value_type")
    @JsonSerialize(using = FieldValueTypeSerializer.class)
    public abstract Field.ValueType getValueType();

    @JsonProperty("value_string")
    @JsonInclude(Include.NON_EMPTY)
    public abstract List<String> getValueStringList();

    @JsonProperty("value_integer")
    @JsonInclude(Include.NON_EMPTY)
    public abstract List<Integer> getValueIntegerList();

    @JsonProperty("value_double")
    @JsonInclude(Include.NON_EMPTY)
    public abstract List<Double> getValueDoubleList();

    @JsonProperty("value_bool")
    @JsonInclude(Include.NON_EMPTY)
    public abstract List<Boolean> getValueBoolList();
}

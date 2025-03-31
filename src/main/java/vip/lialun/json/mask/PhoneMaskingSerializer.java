package vip.lialun.json.mask;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import vip.lialun.string.MaskUtils;

import java.io.IOException;

public class PhoneMaskingSerializer extends JsonSerializer<String> implements ContextualSerializer {

    private final boolean maskPhone;

    public PhoneMaskingSerializer() {
        this(false);
    }

    public PhoneMaskingSerializer(boolean maskPhone) {
        this.maskPhone = maskPhone;
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else if (maskPhone) {
            gen.writeString(MaskUtils.maskPhoneNumber(value));
        } else {
            gen.writeString(value);
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {
        if (property != null) {
            MaskPhone annotation = property.getAnnotation(MaskPhone.class);
            if (annotation != null) {
                return new PhoneMaskingSerializer(true);
            }
        }
        return this;
    }
}
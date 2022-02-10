package common;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class KafkaHelper {

    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL)
                .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    public static Map<String, Object> correlateHeaders(Map<String, Object> incomeHeaders, String sourceInstanceId) {
        if (StringUtils.isBlank(sourceInstanceId)) throw new IllegalArgumentException("sourceInstanceId is empty");
        KafkaHeaderAccessor requestHeaders = KafkaHeaderAccessor.ofMap(incomeHeaders);
        if (StringUtils.isBlank(requestHeaders.replyTopic())) throw new IllegalArgumentException("replyTopic is empty");
        if (requestHeaders.replyTopic().contains("specific")) {
            requestHeaders.destinationInstance(requestHeaders.sourceInstance());
        }
        return requestHeaders.toMap();
    }

    public static UUID bytesToUuid(byte[] bytes) {
        if (bytes == null) return null;
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        long high = byteBuffer.getLong();
        long low = byteBuffer.getLong();
        return new UUID(high, low);
    }

    public static byte[] uuidToBytes(UUID uuid) {
        if (uuid == null) return null;
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        return byteBuffer.array();
    }

    public static Integer bytesToInt(byte[] bytes) {
        if (bytes == null) return null;
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        return byteBuffer.getInt();
    }

    public static byte[] intToBytes(Integer integer) {
        if (integer == null) return null;
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[4]);
        byteBuffer.putInt(integer);
        return byteBuffer.array();
    }

    public static Boolean bytesToBoolean(byte[] bytes) {
        if (bytes == null) return null;
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        return Objects.equals(byteBuffer.get(), (byte) 1);
    }

    public static byte[] booleanToBytes(Boolean bool) {
        if (bool == null) return null;
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[1]);
        byteBuffer.put(BooleanUtils.isTrue(bool) ? (byte) 1 : (byte) 0);
        return byteBuffer.array();
    }

    public static Long bytesToLong(byte[] bytes) {
        if (bytes == null) return null;
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        return byteBuffer.getLong();
    }

    public static byte[] longToBytes(Long l) {
        if (l == null) return null;
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[8]);
        byteBuffer.putLong(l);
        return byteBuffer.array();
    }

    public static String toJsonString(Object value) {
        if (ObjectUtils.isEmpty(value)) {
            return "";
        }
        String result = "";
        try {
            result = objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.error("Error converting object {} to string", value, e);
        }
        return result;
    }

    public static String bytesArrayToString(byte[] bytes) {
        return new String(bytes);
    }

    public static LocalDateTime toLocalDateTime(Long epochMills) {
        try {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMills), ZoneId.systemDefault());
        } catch (Exception e){
            return LocalDateTime.MIN;
        }
    }
}

package vip.lialun.http;

import org.apache.http.Header;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * Http Header
 *
 * @author lialun
 */
public class HttpHeader {
    private String name;
    private String value;

    public HttpHeader(Header header) {
        this.name = header.getName();
        this.value = header.getValue();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HttpHeader)) {
            return false;
        }
        HttpHeader that = (HttpHeader) o;
        return Objects.equals(getName(), that.getName()) &&
                Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getValue());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", HttpHeader.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("value='" + value + "'")
                .toString();
    }
}

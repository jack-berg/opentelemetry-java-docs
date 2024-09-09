package otel;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;

public class CreateAttributes {

  static final AttributeKey<String> SHOP_ID = AttributeKey.stringKey("com.acme.shop.name");
  static final AttributeKey<String> SHOP_NAME = AttributeKey.stringKey("com.acme.shop.id");
  static final AttributeKey<Long> CUSTOMER_ID = AttributeKey.longKey("com.acme.customer.id");
  static final AttributeKey<String> CUSTOMER_NAME =
      AttributeKey.stringKey("com.acme.customer.name");

  static Attributes createFromVarArgs() {
    // use a varargs initializer and pre-allocated attribute keys
    return Attributes.of(
        SHOP_ID,
        "abc123",
        SHOP_NAME,
        "opentelemetry-demo",
        CUSTOMER_ID,
        123L,
        CUSTOMER_NAME,
        "Jack");
  }

  static Attributes createFromBuilder() {
    // use a builder and pre-allocated attribute keys
    return Attributes.builder()
        .put(SHOP_ID, "abc123")
        .put(SHOP_NAME, "opentelemetry-demo")
        .put(CUSTOMER_ID, 123)
        .put(CUSTOMER_NAME, "Jack")
        .build();
  }

  static Attributes createKitchenSink() {
    // use a builder and attribute keys created on the fly
    return Attributes.builder()
        .put("com.acme.string-key", "value")
        .put("com.acme.bool-key", true)
        .put("come.acme.long-key", 1L)
        .put("come.acme.double-key", 1.1)
        .put("come.acme.string-array-key", "value1", "value2")
        .put("come.acme.bool-array-key", true, false)
        .put("come.acme.long-array-key", 1L, 2L)
        .put("come.acme.double-array-key", 1.1, 2.2)
        // or use AttributeKey equivalent
        .put(AttributeKey.stringKey("com.acme.string-key"), "value")
        .put(AttributeKey.booleanKey("com.acme.bool-key"), true)
        .put(AttributeKey.longKey("com.acme.long-key"), 1L)
        .put(AttributeKey.doubleKey("com.acme.double-key"), 1.1)
        .put(AttributeKey.stringArrayKey("com.acme.string-array-key"), "value1", "value2")
        .put(AttributeKey.booleanArrayKey("come.acme.bool-array-key"), true, false)
        .put(AttributeKey.longArrayKey("come.acme.long-array-key"), 1L, 2L)
        .put(AttributeKey.doubleArrayKey("come.acme.double-array-key"), 1.1, 2.2)
        .build();
  }
}

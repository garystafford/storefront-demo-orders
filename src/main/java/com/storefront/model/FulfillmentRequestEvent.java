package com.storefront.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FulfillmentRequestEvent {

    @NonNull
    private Long timestamp;

    @NonNull
    private Name name;

    @NonNull
    private Contact contact;

    @NonNull
    private Address address;

    @NonNull
    private Order order;

}

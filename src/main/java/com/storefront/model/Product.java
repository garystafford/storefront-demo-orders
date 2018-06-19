package com.storefront.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Product {

    @NonNull
    private String guid;

    @NonNull
    private String title;

    @NonNull
    private String description;

    @NonNull
    private BigDecimal price;
}

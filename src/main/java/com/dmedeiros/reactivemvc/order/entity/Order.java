package com.dmedeiros.reactivemvc.order.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    private String _id;
    private String orderId;
    private String delivery;


    public Order(String orderId, String delivery) {
        this.orderId = orderId;
        this.delivery = delivery;
    }


}

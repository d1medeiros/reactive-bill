package com.dmedeiros.reactivemvc.bill;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class Bill {

    @Id
    private String id;
    private String name;
    private Double price;
    private LocalDateTime payday;
    @CreatedDate
    private LocalDateTime dateCreated;
    @LastModifiedDate
    private LocalDateTime lastUpdate;


}

package com.dmedeiros.reactivemvc.bill;

import lombok.*;
import org.springframework.data.annotation.Id;
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
    private LocalDateTime dateCreated;
    private LocalDateTime lastUpdate;


}
//    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
//    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")

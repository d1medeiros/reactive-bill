package com.dmedeiros.reactivemvc.bill;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillUpdate {
    private String id;
    private String name;
    private Double price;
    private LocalDateTime payday;
}

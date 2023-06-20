package org.cedac.batchtest.entity.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class CustomerDTO {

    private int id;

    private String firstName;

    private String lastName;

    private String email;

}
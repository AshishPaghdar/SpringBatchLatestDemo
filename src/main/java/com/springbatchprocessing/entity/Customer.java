package com.springbatchprocessing.entity;

import com.springbatchprocessing.resources.GlobalResourceHandler;
import lombok.*;


@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer extends GlobalResourceHandler {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String profession;

}
package com.springbatchprocessing.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.batch.item.ResourceAware;
import org.springframework.core.io.Resource;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer implements ResourceAware {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String profession;

    private String resourceFileName;

    @Override
    public void setResource(@NonNull Resource resource) {
        this.resourceFileName = resource.getFilename();
    }
}
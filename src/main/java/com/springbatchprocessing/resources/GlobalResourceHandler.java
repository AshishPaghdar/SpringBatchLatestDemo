package com.springbatchprocessing.resources;

import lombok.Getter;
import org.springframework.batch.item.ResourceAware;
import org.springframework.core.io.Resource;

@Getter
public class GlobalResourceHandler implements ResourceAware {

    private String resourceFileName;

    @Override
    public void setResource(Resource resource) {
         this.resourceFileName = resource.getFilename();
    }
}

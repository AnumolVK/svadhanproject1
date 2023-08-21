package com.svadhan.collection.model.lender;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties
public class Api6Root {
    public Api6Response api6Response;
}

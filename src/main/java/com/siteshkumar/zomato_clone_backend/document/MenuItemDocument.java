package com.siteshkumar.zomato_clone_backend.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "menu_index")
public class MenuItemDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Double)
    private Double price;

    @Field(type = FieldType.Boolean)
    private boolean available;

    @Field(type = FieldType.Integer)
    private Integer stock;

    @Field(type = FieldType.Keyword)
    private String restaurantId;
}
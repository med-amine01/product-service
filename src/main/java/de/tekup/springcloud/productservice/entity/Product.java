package de.tekup.springcloud.productservice.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import java.math.BigDecimal;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Product extends AbstractEntity  {
    private String name;
    private String description;
    private BigDecimal price;
}
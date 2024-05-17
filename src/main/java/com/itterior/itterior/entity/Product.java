package com.itterior.itterior.entity;


import com.itterior.itterior.domain.ProductImage;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "tbl_product")
@EntityListeners(AuditingEntityListener.class)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pno;
    private String pname;
    private int price;
    private String pdesc;
    private boolean delFlag;
    private Long categoryNum;

    @CreatedDate
    private LocalDateTime date;

    @Builder.Default
    private Long viewCount= Long.valueOf(0);

    @ManyToOne(fetch = FetchType.LAZY)
    private User seller;

    @ElementCollection
    @Builder.Default
    private List<ProductImage> imageList = new ArrayList<>();

    public void changePrice(int price) {
        this.price = price;
    }

    public void changeDesc(String desc) {
        this.pdesc = desc;
    }

    public void changeName(String name) {
        this.pname = name;
    }

    public void addImage(ProductImage image) {
        image.setOrd(this.imageList.size());
        imageList.add(image);
    }

    public void addImageString(String fileName) {
        ProductImage productImage = ProductImage.builder()
                .fileName(fileName)
                .build();
        addImage(productImage);
    }

    public void clearList() {
        this.imageList.clear();
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public void setViewCount(long viewCount) {
        this.viewCount=viewCount;
    }
}
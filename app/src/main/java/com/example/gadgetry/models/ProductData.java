package com.example.gadgetry.models;

public class ProductData {
    private String id;
    private String name;
    private String price;
    private String description;
    private String type;
    private String image;
    private String quantity;

    public ProductData() {
    }

    public ProductData(String name, String price, String description, String type, String image) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.type = type;
        this.image = image;
    }
    public ProductData(String name, String price, String description, String type, String image,String quantity) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.type = type;
        this.image = image;
        this.quantity= quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getQuantity() {
        return quantity;
    }
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}

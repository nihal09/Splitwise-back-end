package com.nihal.split.models;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.List;

@Data
@Document
public class Group {
    private String _id;
    private String name;
    private List<String> users;
    private Boolean simplifyDebt;
    private List<HashMap<String,Double>> passbook;
}

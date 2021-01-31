package com.nihal.split.models;

import jdk.jfr.DataAmount;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.List;

@Data
@Document
public class Expense {
    private String _id;
    private String gid;
    private String name;
    private String paidBy;
    private Double amount;
    private List<String> peopleInvolved;
    private String type;
    private List<Double> value;

}

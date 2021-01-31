package com.nihal.split.controller;


import com.nihal.split.models.Expense;
import com.nihal.split.models.Group;
import com.nihal.split.models.User;
import com.nihal.split.models.UserPassbook;
import com.nihal.split.repository.ExpenseRepo;
import com.nihal.split.repository.GroupRepo;
import com.nihal.split.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/split")
public class Split {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private GroupRepo groupRepo;

    @Autowired
    private ExpenseRepo expenseRepo;

    @PostMapping("")
    public ResponseEntity<?> split(@RequestBody Expense expense) {
        String paidBy = expense.getPaidBy();
        Double amount = expense.getAmount();
        String gid = expense.getGid();
        String type = expense.getType();
        List<String> people = expense.getPeopleInvolved();
        List<Double> share = expense.getValue();
        Optional<Group> OptionalGroup = groupRepo.findById(gid);
        Group group = null;
        if (OptionalGroup.isPresent())
            group = OptionalGroup.get();
        List<HashMap<String, Double>> passBook = group.getPassbook();
        Double totalMoney;
        if (type.equals("EXACT")) {

            for (int i = 0; i < people.size(); i++) {
                String user = people.get(i);
                Double money = share.get(i);
                int index = group.getUsers().indexOf(user);
                HashMap<String, Double> map = group.getPassbook().get(index);
                if (user.equals(paidBy))
                    map.put(user, map.get(user) + amount - money);
                else {
                    map.put(paidBy, map.get(paidBy) + (share.get(i) * (-1)));
                }
                group.getPassbook().set(index, map);
            }
        }
        if (type.equals("EQUAL")) {

            for (int i = 0; i < people.size(); i++) {
                String user = people.get(i);
                Double money = amount / people.size();
                int index = group.getUsers().indexOf(user);
                HashMap<String, Double> map = group.getPassbook().get(index);
                if (user.equals(paidBy))
                    map.put(user, map.get(user) + amount - money);
                else {
                    map.put(paidBy, map.get(paidBy) + (share.get(i) * (-1)));
                }
                group.getPassbook().set(index, map);
            }
        }

        if (type.equals("SHARE")) {
            Double total = 0.0;
            for (int i = 0; i < share.size(); i++)
                total = total + share.get(i);
            for (int i = 0; i < people.size(); i++) {
                String user = people.get(i);
                Double money = amount / total * share.get(i);
                int index = group.getUsers().indexOf(user);
                HashMap<String, Double> map = group.getPassbook().get(index);
                if (user.equals(paidBy))
                    map.put(user, map.get(user) + amount - money);
                else {
                    map.put(paidBy, map.get(paidBy) + (share.get(i) * (-1)));
                }
                group.getPassbook().set(index, map);
            }
        }
        if (type.equals("PERCENTAGE")) {
            for (int i = 0; i < people.size(); i++) {
                String user = people.get(i);
                Double money = amount / 100.0 * share.get(i);
                int index = group.getUsers().indexOf(user);
                HashMap<String, Double> map = group.getPassbook().get(index);
                if (user.equals(paidBy))
                    map.put(user, map.get(user) + amount - money);
                else {
                    map.put(paidBy, map.get(paidBy) + (share.get(i) * (-1)));
                }
                group.getPassbook().set(index, map);
            }
        }
        group = groupRepo.save(group);
        expense = expenseRepo.save(expense);
        return ResponseEntity.ok(expense);
    }

    @PostMapping("/user")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        user = userRepo.save(user);
        return ResponseEntity.ok(user);

    }

    @PostMapping("/group")
    public ResponseEntity<?> createGroup(@RequestBody Group group) {
        List<HashMap<String, Double>> passBook = new ArrayList<HashMap<String, Double>>();
        for (int i = 0; i < group.getUsers().size(); i++) {
            HashMap<String, Double> book = new HashMap<String, Double>();
            for (int j = 0; j < group.getUsers().size(); j++) {
                book.put(group.getUsers().get(j), 0.0);
            }
            passBook.add(book);
        }
        group.setPassbook(passBook);
        group = groupRepo.save(group);
        return ResponseEntity.ok(group);

    }

    @GetMapping("/{gid}/{name}")
    public ResponseEntity<?> getExpense(@PathVariable("name") String name, @PathVariable("gid") String gid) {
        Optional<Group> OptionalGroup = Optional.ofNullable(groupRepo.findByGroupId(gid));
        Group group = null;
        if (OptionalGroup.isPresent())
            group = OptionalGroup.get();
        int index = group.getUsers().indexOf(name);
        UserPassbook userPassbook = new UserPassbook();
        userPassbook.setName(name);
        HashMap<String, Double> map = new HashMap<String, Double>();
        List<String> transaction = new ArrayList<String>();
        Double total = group.getPassbook().get(index).get(name);
        for (int j=0;j<group.getUsers().size();j++) {
            if (j== index){
                userPassbook.setTotalContribution(group.getPassbook().get(j).get(name));
                continue;}
            else {
                String otherUser = group.getUsers().get(j);
                int idx= group.getUsers().indexOf(otherUser);
                Double val = group.getPassbook().get(j).get(name);
                Double val1 = group.getPassbook().get(index).get(otherUser);
                if (val1-val < 0.0)
                    transaction.add("User " + name + " owes " + group.getUsers().get(j) + " Rs." + (val1-val) * (-1));
                else if(val1-val > 0.0)
                    transaction.add("User " + group.getUsers().get(j) + " owes " + name + " Rs." + (group.getPassbook().get(j).get(name) * (-1)));
                total = total+group.getPassbook().get(j).get(name);

            }

        }
        userPassbook.setTransaction(transaction);
        userPassbook.setGetsBack(total);
        return ResponseEntity.ok(userPassbook);

    }
}

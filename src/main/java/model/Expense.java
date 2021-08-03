package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class Expense {
    private String name;
    private ExpenseType expenseType;
    private Double amount;
    private User payer;
    private List<User> members;
}

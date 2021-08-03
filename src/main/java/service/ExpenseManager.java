package service;

import model.Command;
import model.Expense;
import model.ExpenseType;
import model.User;

import java.io.File;
import java.text.NumberFormat;
import java.util.*;
/*
   This is the main service class implementing all the logic from reading the input
   to creating expense and user object and adding expenses to user objects.
 */
public class ExpenseManager {
    private static ExpenseManager expenseManager;
    private final Map<String, User> userMap;
    private final Map<User,List<Expense>> expenseList;

    public static ExpenseManager getInstance() {
        if(expenseManager == null) {
            expenseManager = new ExpenseManager();
        }
        return expenseManager;
    }

    private ExpenseManager() {
        userMap = new LinkedHashMap<>();
        expenseList = new LinkedHashMap<>();
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
    }
    public void startProcess(File inputFile) throws Exception{
        Scanner scanner = new Scanner(inputFile);
        while(scanner.hasNext()) {
            String input = scanner.nextLine();
            String[] parts = input.split(" ");
            try {
                String command = parts[0];
                if (parts.length == 1 && Command.SHOW.name().equals(command)) {
                    showAllExpense();
                } else if (command.equals(Command.SHOW.name())) {
                    if (userMap.containsKey(parts[1])) {
                        showExpenseUser(userMap.get(parts[1]));
                    } else {
                        User user;
                        if (!userMap.containsKey(parts[1])) {
                            user = createNewUser(parts[1]);
                        } else {
                            user = userMap.get(parts[1]);
                        }
                        showExpenseUser(user);
                    }
                } else if (command.equals(Command.PRINT.name())) {
                    printPassbook(parts[1]);
                } else if (command.equals(Command.EXPENSE.name())) {
                    User payer;
                    if (!userMap.containsKey(parts[1])) {
                        payer = createNewUser(parts[1]);
                    } else {
                        payer = userMap.get(parts[1]);
                    }
                    Double amount = Double.parseDouble(parts[2]);
                    List<User> expenseMembers = new ArrayList<>();
                    Integer numberOfMembers = Integer.parseInt(parts[3]);
                    int i;
                    for (i = 4; i < 4 + numberOfMembers && i < parts.length; i++) {
                        if (parts[i].equals(payer.getUserId())) {
                            expenseMembers.add(payer);
                        } else {
                            User member;
                            if (!userMap.containsKey(parts[i])) {
                                member = createNewUser(parts[i]);
                            } else {
                                member = userMap.get(parts[i]);
                            }
                            expenseMembers.add(member);
                        }
                    }
                    ExpenseType expenseType = ExpenseType.valueOf(parts[i]);
                    List<Double> shares = new ArrayList<>();
                    i++;
                    if (!expenseType.equals(ExpenseType.EQUAL)) {
                        for (; i < parts.length - 1; i++) {
                            shares.add(Double.parseDouble(parts[i]));
                        }
                    }
                    Expense expense = new Expense(parts[i], expenseType, amount, payer, expenseMembers);
                    for (User member : expenseMembers) {
                        if (!payer.equals(member)) {
                            if (expenseList.containsKey(member)) {
                                expenseList.get(member).add(expense);
                            } else {
                                List<Expense> newList = new ArrayList<>();
                                newList.add(expense);
                                expenseList.put(member, newList);
                            }
                        }
                    }
                    if (expenseList.containsKey(payer)) {
                        expenseList.get(payer).add(expense);
                    } else {
                        List<Expense> newList = new ArrayList<>();
                        newList.add(expense);
                        expenseList.put(payer, newList);
                    }
                    createExpense(expense, shares);
                } else {
                    System.out.println("Unknown expense input");
                    return;
                }
            } catch (Exception e) {
                System.out.println("Input invalid as splitting resulted in throwing exception");
                return;
            }
        }
    }

    public void createExpense(Expense expense, List<Double> shares) {
        for(User user : expense.getMembers()) {
            userMap.put(user.getUserId(), user);
        }
        ExpenseSplitter splitter = new ExpenseSplitter(expense, expense.getPayer());
        ExpenseType type = expense.getExpenseType();
        try {
            if (type.equals(ExpenseType.EQUAL)) {
                splitter.splitEqually();
            } else if (type.equals(ExpenseType.EXACT)) {
                splitter.splitExactly(shares);
            } else if (type.equals(ExpenseType.PERCENT)) {
                splitter.splitByPercentage(shares);
            } else if (type.equals(ExpenseType.SHARE)) {
                splitter.splitByShares(shares);
            }
        } catch (Exception e) {
            System.out.println("Illegal expense, skipping the expense");
        }
    }

    public void showAllExpense() {
        if(userMap.isEmpty()) System.out.println("No balances");
        for(User user : userMap.values()) {
            showExpenseAllUsers(user);
        }
    }

    public void showExpenseUser(User user) {
        Map<User, Double> moneyOwed = user.getMoneyOwed();
        if(moneyOwed.isEmpty()) System.out.println("No balances");
        for(Map.Entry<User, Double> mapEntry : moneyOwed.entrySet()) {
            if(mapEntry.getValue() > 0) {
                System.out.println(user.getName() + " owes " + mapEntry.getKey().getName() + ": " + mapEntry.getValue());
            } else if(mapEntry.getValue() < 0) {
                System.out.println(mapEntry.getKey().getName() + " owes " + user.getName() + ": " + -1*mapEntry.getValue());
            }
        }
    }

    public void showExpenseAllUsers(User user) {
        Map<User, Double> moneyOwed = user.getMoneyOwed();
        if(moneyOwed.isEmpty()) System.out.println("No balances");
        for(Map.Entry<User, Double> mapEntry : moneyOwed.entrySet()) {
            if (mapEntry.getValue() > 0) {
                System.out.println(user.getName() + " owes " + mapEntry.getKey().getName() + ": " + mapEntry.getValue());
            }
        }
    }

    public User createNewUser(String userId) {
        User member = new User();
        member.setUserId(userId);
        char number = userId.charAt(1);
        member.setName("User" + number);
        member.setBalance(0.0);
        member.setEmailId("user" + number + "@test");
        member.setPhoneNumber("12345" + number);
        member.setMoneyOwed(new LinkedHashMap<>());
        return member;
    }

    public void printPassbook(String userId) {
        for(Expense expense : expenseList.get(userMap.get(userId))) {
            System.out.println(expense.getName());
        }
    }
}

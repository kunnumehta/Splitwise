package service;

import model.Expense;
import model.ExpenseType;
import model.User;

import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
/*
   This class implements functions to split the expense.
 */
public class ExpenseSplitter {

    private final NumberFormat nf;
    private final Expense expense;
    private final User payer;

    public ExpenseSplitter(Expense expense, User payer) {
        this.expense = expense;
        this.payer = payer;
        this.nf = NumberFormat.getInstance();
        this.nf.setMaximumFractionDigits(2);
    }

    public void splitEqually(){
        Double shareAmount = expense.getAmount()/expense.getMembers().size();
        if(shareAmount.isInfinite()) shareAmount = Double.parseDouble(nf.format(shareAmount));
        for(User member  : expense.getMembers()) {
            if(!member.getUserId().equals(payer.getUserId())) {
                updateUserBalances(payer, member, shareAmount);
            }
        }
    }

    public void splitExactly(List<Double> shares) throws Exception{
        if(checkSumEquals100(shares)) {
            for(int i =0;i<expense.getMembers().size();i++) {
                if(!expense.getMembers().get(i).equals(payer)) {
                    updateUserBalances(payer, expense.getMembers().get(i), shares.get(i));
                }
            }
        } else {
            throw new Exception("Input is invalid as the shares doesn't add up to the amount");
        }
    }

    public void splitByPercentage(List<Double> percentages) throws Exception{
        if(checkSumEquals100(percentages)) {
            for(int i =0;i<expense.getMembers().size();i++) {
                if(!expense.getMembers().get(i).equals(payer)) {
                    Double amount = percentages.get(i)*expense.getAmount()/(100.0);
                    updateUserBalances(payer, expense.getMembers().get(i), amount);
                }
            }
        } else {
            throw new Exception("Input is invalid as the percentage sum doesn't add up to 100");
        }
    }

    public void splitByShares(List<Double> shares) {
        Double sumShares = 0.0;
        for (Double share : shares) {
            sumShares += share;
        }
        for(int i =0;i<expense.getMembers().size();i++) {
            if(!expense.getMembers().get(i).equals(payer)) {
                Double amount = expense.getAmount()*shares.get(i)/sumShares;
                updateUserBalances(payer, expense.getMembers().get(i), amount);
            }
        }
    }

    public boolean checkSumEquals100( List<Double> shares) {
            Double sum = (double) 0;
            for(Double share : shares) {
                sum += share;
            }
            if(expense.getExpenseType().equals(ExpenseType.EXACT)) {
                return sum.equals(expense.getAmount());
            } else {
                return sum.equals(100.0);
            }
    }

    /*
       This function updates the user balances
     */
    public void updateUserBalances(User user1, User user2, Double amount) {
        Map<User, Double> moneyLent = user1.getMoneyOwed();
        Map<User, Double> moneyOwed = user2.getMoneyOwed();
        if(moneyLent.containsKey(user2)) {
            moneyLent.put(user2,moneyLent.get(user2) - amount);
        } else {
            moneyLent.put(user2, -1*amount);
        }
        if(moneyOwed.containsKey(user1)) {
            moneyOwed.put(user1,moneyOwed.get(user1) + amount);
        } else {
            moneyOwed.put(user1, amount);
        }
    }
}

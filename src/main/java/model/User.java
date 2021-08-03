package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String name;
    private String userId;
    private String emailId;
    private String phoneNumber;
    private Double balance;
    private Map<User, Double> moneyOwed;
}

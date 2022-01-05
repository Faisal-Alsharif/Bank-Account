package com.example.demo.conrollers;

import com.example.demo.model.BankUsers;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping(path = "bank/users")
public class BankController {

    ArrayList<BankUsers> bankUsers = new ArrayList<>();
    int bankBalance = 1000000;

    @GetMapping
    public ArrayList<BankUsers> getBankUsers(){
        return bankUsers;
    }

    @PostMapping
    public ResponseEntity<String> addbankUser(@RequestBody BankUsers bankUser){
        if(bankUser.getId() == null || bankUser.getEmail() == null || bankUser.getName() == null || bankUser.getPassword() == null ){
            return ResponseEntity.status(400).body("Please enter all information");
        }else if(bankUser.getPassword().length() < 6){
            return ResponseEntity.status(400).body("Password must be more than 6 digits");
        }
        bankUsers.add(bankUser);
        return ResponseEntity.status(200).body("Added successfully");
    }

    @PostMapping("deposit/{id}")
    public ResponseEntity<String> deposit(@PathVariable String id, @RequestBody Deposit deposit){
        for(int i=0; i<bankUsers.size(); i++){
            if(bankUsers.get(i).getId().equals(id)){
                if(bankUsers.get(i).getPassword().equals(deposit.getPassword())){
                    bankUsers.get(i).setBalance(bankUsers.get(i).getBalance() + deposit.getAmount());
                    return ResponseEntity.status(200).body("deposit "+ deposit.getAmount() + " successfully");
                }
                return ResponseEntity.status(400).body("password is not correct");
            }
        }
        return ResponseEntity.status(400).body("sorry! user account not found");
    }

    @PostMapping("withdraw/{id}")
    public ResponseEntity<String> withdraw(@PathVariable String id,@RequestBody Withdraw withdraw){
        for(int i=0; i<bankUsers.size(); i++){
            if(bankUsers.get(i).getId().equals(id)){
                if(bankUsers.get(i).getPassword().equals(withdraw.getPassword())){
                    if(bankUsers.get(i).getBalance() >= withdraw.getAmount()){
                        bankUsers.get(i).setBalance(bankUsers.get(i).getBalance() - withdraw.getAmount());
                        return ResponseEntity.status(200).body("withdraw "+ withdraw.getAmount() + " successfully");
                    }
                    return ResponseEntity.status(400).body("Balance is less than " + withdraw.getAmount());
                }
                return ResponseEntity.status(400).body("password is not correct");
            }
        }
        return ResponseEntity.status(400).body("sorry! user account not found");
    }

    @PostMapping("loan/{id}")
    public ResponseEntity<String> loanFromBank(@PathVariable String id, @RequestBody Loan loan){
        for(int i=0; i<bankUsers.size(); i++){
            if(bankUsers.get(i).getId().equals(id)){
                if(loan.getLoanAmount() > bankBalance){
                    return ResponseEntity.status(400).body("Sorry! Bank cannot give this loan");
                }
                bankUsers.get(i).setLoanAmount(bankUsers.get(i).getLoanAmount() + loan.getLoanAmount());
                bankBalance -= loan.getLoanAmount();
                return ResponseEntity.status(200).body("loan " + loan.getLoanAmount() + " successfully");
            }
        }
        return ResponseEntity.status(400).body("Sorry! User not found");
    }

    @PostMapping("loan/pay/{id}")
    public ResponseEntity<String> loanFromBank(@PathVariable String id, @RequestBody PayLoan payLoan){
        for(int i=0; i<bankUsers.size(); i++){
            if(bankUsers.get(i).getId().equals(id)){
                if(bankUsers.get(i).getBalance() < payLoan.getAmount()){
                    return ResponseEntity.status(400).body("Sorry! your balance is not enough");
                }
                bankUsers.get(i).setBalance(bankUsers.get(i).getBalance() - payLoan.getAmount());
                bankUsers.get(i).setLoanAmount(bankUsers.get(i).getLoanAmount() - payLoan.getAmount());
                bankBalance += payLoan.getAmount();
                return ResponseEntity.status(200).body("Pay loan " + payLoan.getAmount() + " successfully");
            }
        }
        return ResponseEntity.status(400).body("Sorry! User not found");
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteUserAccount(@PathVariable String id){
        for(int i=0; i<bankUsers.size(); i++){
            if(bankUsers.get(i).getId().equals(id)){
                if(bankUsers.get(i).getLoanAmount() != 0){
                    return ResponseEntity.status(400).body("Sorry! you must to pay tha loan Amount first");
                }
                bankBalance += bankUsers.get(i).getBalance();
                bankUsers.remove(i);
                break;
            }
        }
        return ResponseEntity.status(200).body("deleted successfully");
    }
}

class Deposit{
    private int amount;
    private String password;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

class Withdraw{
    private int amount;
    private String password;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
class Loan{
    private int loanAmount;

    public int getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(int loanAmount) {
        this.loanAmount = loanAmount;
    }
}
class PayLoan{
    private int amount;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
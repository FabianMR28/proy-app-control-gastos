package pe.edu.utp.control_gastos_app.service;

import org.springframework.stereotype.Service;
import pe.edu.utp.control_gastos_app.model.Account;
import pe.edu.utp.control_gastos_app.model.User;
import pe.edu.utp.control_gastos_app.repository.AccountRepository;
import pe.edu.utp.control_gastos_app.repository.UserRepository;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public List<Account> findByUserId(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    public Account findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));
    }

    public void save(Account account, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        account.setUser(user);
        accountRepository.save(account);
    }

    public void update(Long id, Account updated, Long userId) {
        if (!accountRepository.existsByIdAndUserId(id, userId)) {
            throw new RuntimeException("Account not found or does not belong to user");
        }
        Account account = findById(id);
        account.setName(updated.getName());
        account.setType(updated.getType());
        account.setCurrency(updated.getCurrency());
        accountRepository.save(account);
    }

    public void delete(Long id, Long userId) {
        if (!accountRepository.existsByIdAndUserId(id, userId)) {
            throw new RuntimeException("Account not found or does not belong to user");
        }
        accountRepository.deleteById(id);
    }
}
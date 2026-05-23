package pe.edu.utp.control_gastos_app.service;

import org.springframework.stereotype.Service;
import pe.edu.utp.control_gastos_app.enums.CategoryType;
import pe.edu.utp.control_gastos_app.model.Account;
import pe.edu.utp.control_gastos_app.model.Category;
import pe.edu.utp.control_gastos_app.model.Transaction;
import pe.edu.utp.control_gastos_app.repository.AccountRepository;
import pe.edu.utp.control_gastos_app.repository.CategoryRepository;
import pe.edu.utp.control_gastos_app.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository,
                              CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Transaction> findByUserId(Long userId) {
        return transactionRepository.findByAccountUserId(userId);
    }

    public List<Transaction> findByUserIdAndType(Long userId, CategoryType type) {
        return transactionRepository.findByAccountUserIdAndType(userId, type);
    }

    public List<Transaction> findByUserIdAndDateBetween(Long userId, LocalDate start, LocalDate end) {
        return transactionRepository.findByAccountUserIdAndDateBetween(userId, start, end);
    }

    public Transaction findById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
    }

    public void save(Transaction transaction, Long accountId, Long categoryId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + accountId));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));

        transaction.setAccount(account);
        transaction.setCategory(category);

        updateBalance(account, transaction.getAmount(), transaction.getType());

        transactionRepository.save(transaction);
    }

    public void delete(Long id) {
        Transaction transaction = findById(id);
        Account account = transaction.getAccount();

        CategoryType reverseType = transaction.getType() == CategoryType.INCOME
                ? CategoryType.EXPENSE
                : CategoryType.INCOME;

        updateBalance(account, transaction.getAmount(), reverseType);
        transactionRepository.deleteById(id);
    }

    private void updateBalance(Account account, BigDecimal amount, CategoryType type) {
        if (type == CategoryType.INCOME) {
            account.setBalance(account.getBalance().add(amount));
        } else {
            account.setBalance(account.getBalance().subtract(amount));
        }
        accountRepository.save(account);
    }
}
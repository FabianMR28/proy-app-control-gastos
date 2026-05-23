package pe.edu.utp.control_gastos_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.control_gastos_app.enums.CategoryType;
import pe.edu.utp.control_gastos_app.model.Transaction;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountId(Long accountId);
    List<Transaction> findByAccountUserId(Long userId);
    List<Transaction> findByAccountUserIdAndType(Long userId, CategoryType type);
    List<Transaction> findByAccountUserIdAndDateBetween(Long userId, LocalDate start, LocalDate end);
}

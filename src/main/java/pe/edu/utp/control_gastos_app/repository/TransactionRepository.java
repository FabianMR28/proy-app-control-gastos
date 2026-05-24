package pe.edu.utp.control_gastos_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.utp.control_gastos_app.dto.report.CategoryExpenseDTO;
import pe.edu.utp.control_gastos_app.enums.CategoryType;
import pe.edu.utp.control_gastos_app.model.Transaction;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccountId(Long accountId);

    List<Transaction> findByAccountUserId(Long userId);

    List<Transaction> findByAccountUserIdAndType(
            Long userId,
            CategoryType type
    );

    List<Transaction> findByAccountUserIdAndDateBetween(
            Long userId,
            LocalDate start,
            LocalDate end
    );

    // TOTAL INGRESOS
    @Query("""
    SELECT COALESCE(SUM(t.amount),0)
    FROM Transaction t
    WHERE t.category.type =
    pe.edu.utp.control_gastos_app.enums.CategoryType.INCOME
    """)
    Double getTotalIncome();

    // TOTAL GASTOS
    @Query("""
    SELECT COALESCE(SUM(t.amount),0)
    FROM Transaction t
    WHERE t.category.type =
    pe.edu.utp.control_gastos_app.enums.CategoryType.EXPENSE
    """)
    Double getTotalExpenses();

    // Gastos por categoría del usuario (para pie chart)
    @Query("""
    SELECT new pe.edu.utp.control_gastos_app.dto.report.CategoryExpenseDTO(
        t.category.name,
        CAST(SUM(t.amount) AS double)
    )
    FROM Transaction t
    WHERE t.account.user.id = :userId
      AND t.type = pe.edu.utp.control_gastos_app.enums.CategoryType.EXPENSE
    GROUP BY t.category.name
    ORDER BY SUM(t.amount) DESC
""")
    List<CategoryExpenseDTO> findExpensesByCategory(@Param("userId") Long userId);

    // Ingresos y gastos por mes (últimos 6 meses, para bar chart)
    @Query("""
        SELECT
            FUNCTION('YEAR', t.date)  AS yr,
            FUNCTION('MONTH', t.date) AS mo,
            t.type,
            CAST(SUM(t.amount) AS double)
        FROM Transaction t
        WHERE t.account.user.id = :userId
            AND t.date >= :from
        GROUP BY FUNCTION('YEAR', t.date), FUNCTION('MONTH', t.date), t.type
        ORDER BY FUNCTION('YEAR', t.date), FUNCTION('MONTH', t.date)
    """)
    List<Object[]> findMonthlyTotalsRaw(
            @Param("userId") Long userId,
            @Param("from") LocalDate from
    );

    // Transacciones ordenadas por fecha para balance acumulado (line chart)
    @Query("""
    SELECT t.date, t.type, t.amount
    FROM Transaction t
    WHERE t.account.user.id = :userId
    ORDER BY t.date ASC
""")
    List<Object[]> findAllForBalanceChart(@Param("userId") Long userId);

    @Query("""
    SELECT new pe.edu.utp.control_gastos_app.dto.report.CategoryExpenseDTO(
        t.category.name,
        CAST(SUM(t.amount) AS double)
    )
    FROM Transaction t
    WHERE t.account.user.id = :userId
      AND t.type = pe.edu.utp.control_gastos_app.enums.CategoryType.EXPENSE
      AND t.date BETWEEN :start AND :end
    GROUP BY t.category.name
""")
    List<CategoryExpenseDTO> findExpensesByCategoryAndPeriod(
            @Param("userId") Long userId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );
}
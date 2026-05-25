package pe.edu.utp.control_gastos_app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pe.edu.utp.control_gastos_app.enums.BudgetPeriod;
import pe.edu.utp.control_gastos_app.enums.CategoryType;
import pe.edu.utp.control_gastos_app.model.Budget;
import pe.edu.utp.control_gastos_app.model.Category;
import pe.edu.utp.control_gastos_app.model.User;
import pe.edu.utp.control_gastos_app.repository.BudgetRepository;
import pe.edu.utp.control_gastos_app.repository.CategoryRepository;
import pe.edu.utp.control_gastos_app.repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private BudgetService budgetService;

    private User user;
    private Category category;
    private Budget budget;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);

        category = new Category();
        category.setId(1L);
        category.setName("Comida");
        category.setType(CategoryType.EXPENSE);
        category.setUser(user);

        budget = new Budget();
        budget.setId(1L);
        budget.setUser(user);
        budget.setCategory(category);
        budget.setLimitAmount(BigDecimal.valueOf(1000));
        budget.setPeriod(BudgetPeriod.MONTHLY);
        budget.setMonth((byte) 5);
        budget.setYear((short) 2026);
    }

    @Test
    void shouldFindBudgetsByUserId() {

        when(budgetRepository.findByUserId(1L))
                .thenReturn(List.of(budget));

        List<Budget> budgets =
                budgetService.findByUserId(1L);

        assertNotNull(budgets);
        assertEquals(1, budgets.size());
        assertEquals(
                BigDecimal.valueOf(1000),
                budgets.get(0).getLimitAmount()
        );

        verify(budgetRepository, times(1))
                .findByUserId(1L);
    }

    @Test
    void shouldFindBudgetsByUserIdMonthAndYear() {

        when(budgetRepository
                .findByUserIdAndMonthAndYear(
                        1L, 5, 2026))
                .thenReturn(List.of(budget));

        List<Budget> budgets =
                budgetService
                        .findByUserIdAndMonthAndYear(
                                1L, 5, 2026);

        assertNotNull(budgets);
        assertEquals(1, budgets.size());

        verify(budgetRepository, times(1))
                .findByUserIdAndMonthAndYear(
                        1L, 5, 2026);
    }

    @Test
    void shouldFindBudgetById() {

        when(budgetRepository.findById(1L))
                .thenReturn(Optional.of(budget));

        Budget result =
                budgetService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(
                BudgetPeriod.MONTHLY,
                result.getPeriod()
        );

        verify(budgetRepository, times(1))
                .findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenBudgetNotFound() {

        when(budgetRepository.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> budgetService.findById(1L)
                );

        assertEquals(
                "Budget not found with id: 1",
                exception.getMessage()
        );

        verify(budgetRepository, times(1))
                .findById(1L);
    }

    @Test
    void shouldSaveBudget() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(budgetRepository
                .findByUserIdAndCategoryIdAndMonthAndYear(
                        1L,
                        1L,
                        budget.getMonth(),
                        budget.getYear()
                ))
                .thenReturn(Optional.empty());

        budgetService.save(budget, 1L, 1L);

        assertEquals(user, budget.getUser());
        assertEquals(category, budget.getCategory());

        verify(userRepository, times(1))
                .findById(1L);

        verify(categoryRepository, times(1))
                .findById(1L);

        verify(budgetRepository, times(1))
                .save(budget);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundOnSave() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> budgetService.save(
                                budget, 1L, 1L)
                );

        assertEquals(
                "User not found with id: 1",
                exception.getMessage()
        );

        verify(budgetRepository, never())
                .save(any(Budget.class));
    }

    @Test
    void shouldThrowExceptionWhenCategoryNotFoundOnSave() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> budgetService.save(
                                budget, 1L, 1L)
                );

        assertEquals(
                "Category not found with id: 1",
                exception.getMessage()
        );

        verify(budgetRepository, never())
                .save(any(Budget.class));
    }

    @Test
    void shouldThrowExceptionWhenBudgetAlreadyExists() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(budgetRepository
                .findByUserIdAndCategoryIdAndMonthAndYear(
                        1L,
                        1L,
                        budget.getMonth(),
                        budget.getYear()
                ))
                .thenReturn(Optional.of(budget));

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> budgetService.save(
                                budget, 1L, 1L)
                );

        assertEquals(
                "A budget already exists for this category and period",
                exception.getMessage()
        );

        verify(budgetRepository, never())
                .save(any(Budget.class));
    }

    @Test
    void shouldUpdateBudget() {

        Budget updated = new Budget();
        updated.setLimitAmount(
                BigDecimal.valueOf(1500));
        updated.setPeriod(
                BudgetPeriod.ANNUAL);
        updated.setMonth((byte) 12);
        updated.setYear((short) 2027);

        when(budgetRepository.findById(1L))
                .thenReturn(Optional.of(budget));

        budgetService.update(1L, updated);

        assertEquals(
                BigDecimal.valueOf(1500),
                budget.getLimitAmount()
        );

        assertEquals(
                BudgetPeriod.ANNUAL,
                budget.getPeriod()
        );

        assertEquals(
                Byte.valueOf((byte) 12),
                budget.getMonth()
        );

        assertEquals(
                Short.valueOf((short) 2027),
                budget.getYear()
        );

        verify(budgetRepository, times(1))
                .save(budget);
    }

    @Test
    void shouldDeleteBudget() {

        budgetService.delete(1L);

        verify(budgetRepository, times(1))
                .deleteById(1L);
    }
}

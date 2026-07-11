package pe.edu.utp.control_gastos_app.service;

import org.springframework.stereotype.Service;
import pe.edu.utp.control_gastos_app.model.Budget;
import pe.edu.utp.control_gastos_app.model.Category;
import pe.edu.utp.control_gastos_app.model.User;
import pe.edu.utp.control_gastos_app.repository.BudgetRepository;
import pe.edu.utp.control_gastos_app.repository.CategoryRepository;
import pe.edu.utp.control_gastos_app.repository.UserRepository;

import java.util.List;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public BudgetService(BudgetRepository budgetRepository,
                         UserRepository userRepository,
                         CategoryRepository categoryRepository) {
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Budget> findByUserId(Long userId) {
        return budgetRepository.findByUserId(userId);
    }

    public List<Budget> findByUserIdAndMonthAndYear(Long userId, Integer month, Integer year) {
        return budgetRepository.findByUserIdAndMonthAndYear(userId, month, year);
    }

    public Budget findById(Long id) {
        return budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found with id: " + id));
    }

    public void save(Budget budget, Long userId, Long categoryId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));

        boolean exists = budgetRepository
                .findByUserIdAndCategoryIdAndMonthAndYear(userId, categoryId, budget.getMonth(), budget.getYear())
                .isPresent();

        if (exists) {
            throw new RuntimeException("A budget already exists for this category and period");
        }

        budget.setUser(user);
        budget.setCategory(category);
        budgetRepository.save(budget);
    }

    public void update(Long id, Budget updated) {
        Budget budget = findById(id);
        budget.setLimitAmount(updated.getLimitAmount());
        budget.setPeriod(updated.getPeriod());
        budget.setMonth(updated.getMonth());
        budget.setYear(updated.getYear());
        budgetRepository.save(budget);
    }

    public void delete(Long id) {
        budgetRepository.deleteById(id);
    }
}
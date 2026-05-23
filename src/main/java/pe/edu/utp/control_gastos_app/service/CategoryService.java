package pe.edu.utp.control_gastos_app.service;

import org.springframework.stereotype.Service;
import pe.edu.utp.control_gastos_app.model.Category;
import pe.edu.utp.control_gastos_app.model.User;
import pe.edu.utp.control_gastos_app.repository.CategoryRepository;
import pe.edu.utp.control_gastos_app.repository.UserRepository;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public List<Category> findAllByUserId(Long userId) {
        return categoryRepository.findByUserIdOrUserIsNull(userId);
    }

    public List<Category> findGlobal() {
        return categoryRepository.findByUserIsNull();
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    public void save(Category category, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        category.setUser(user);
        categoryRepository.save(category);
    }

    public void saveGlobal(Category category) {
        category.setUser(null);
        categoryRepository.save(category);
    }

    public void update(Long id, Category updated) {
        Category category = findById(id);
        category.setName(updated.getName());
        category.setType(updated.getType());
        categoryRepository.save(category);
    }

    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }
}
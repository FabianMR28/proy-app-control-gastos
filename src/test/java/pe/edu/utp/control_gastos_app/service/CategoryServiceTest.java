package pe.edu.utp.control_gastos_app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pe.edu.utp.control_gastos_app.enums.CategoryType;
import pe.edu.utp.control_gastos_app.model.Category;
import pe.edu.utp.control_gastos_app.model.User;
import pe.edu.utp.control_gastos_app.repository.CategoryRepository;
import pe.edu.utp.control_gastos_app.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CategoryService categoryService;

    private User user;
    private Category category;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);

        category = new Category();
        category.setId(1L);
        category.setName("Alimentación");
        category.setType(CategoryType.EXPENSE);
        category.setUser(user);
    }

    @Test
    void shouldFindAllCategoriesByUserId() {

        when(categoryRepository
                .findByUserIdOrUserIsNull(1L))
                .thenReturn(List.of(category));

        List<Category> categories =
                categoryService.findAllByUserId(1L);

        assertNotNull(categories);
        assertEquals(1, categories.size());
        assertEquals(
                "Alimentación",
                categories.get(0).getName()
        );

        verify(categoryRepository, times(1))
                .findByUserIdOrUserIsNull(1L);
    }

    @Test
    void shouldFindGlobalCategories() {

        when(categoryRepository.findByUserIsNull())
                .thenReturn(List.of(category));

        List<Category> categories =
                categoryService.findGlobal();

        assertNotNull(categories);
        assertEquals(1, categories.size());

        verify(categoryRepository, times(1))
                .findByUserIsNull();
    }

    @Test
    void shouldFindCategoryById() {

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        Category result =
                categoryService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(
                "Alimentación",
                result.getName()
        );

        assertEquals(
                CategoryType.EXPENSE,
                result.getType()
        );

        verify(categoryRepository, times(1))
                .findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenCategoryNotFound() {

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> categoryService.findById(1L)
                );

        assertEquals(
                "Category not found with id: 1",
                exception.getMessage()
        );

        verify(categoryRepository, times(1))
                .findById(1L);
    }

    @Test
    void shouldSaveCategory() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        categoryService.save(category, 1L);

        assertNotNull(category.getUser());
        assertEquals(user, category.getUser());

        verify(userRepository, times(1))
                .findById(1L);

        verify(categoryRepository, times(1))
                .save(category);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundOnSave() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> categoryService.save(
                                category, 1L)
                );

        assertEquals(
                "User not found with id: 1",
                exception.getMessage()
        );

        verify(userRepository, times(1))
                .findById(1L);

        verify(categoryRepository, never())
                .save(any(Category.class));
    }

    @Test
    void shouldSaveGlobalCategory() {

        category.setUser(user);

        categoryService.saveGlobal(category);

        assertNull(category.getUser());

        verify(categoryRepository, times(1))
                .save(category);
    }

    @Test
    void shouldUpdateCategory() {

        Category updated = new Category();
        updated.setName("Transporte");
        updated.setType(CategoryType.INCOME);

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        categoryService.update(1L, updated);

        assertEquals(
                "Transporte",
                category.getName()
        );

        assertEquals(
                CategoryType.INCOME,
                category.getType()
        );

        verify(categoryRepository, times(1))
                .findById(1L);

        verify(categoryRepository, times(1))
                .save(category);
    }

    @Test
    void shouldDeleteCategory() {

        categoryService.delete(1L);

        verify(categoryRepository, times(1))
                .deleteById(1L);
    }
}
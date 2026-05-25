package pe.edu.utp.control_gastos_app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pe.edu.utp.control_gastos_app.model.Role;
import pe.edu.utp.control_gastos_app.repository.RoleRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    private Role role;

    @BeforeEach
    void setUp() {

        role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
    }

    @Test
    void shouldFindRoleByName() {

        when(roleRepository.findByName("ROLE_USER"))
                .thenReturn(Optional.of(role));

        Optional<Role> result =
                roleService.findByName("ROLE_USER");

        assertTrue(result.isPresent());
        assertEquals(
                "ROLE_USER",
                result.get().getName()
        );

        verify(roleRepository, times(1))
                .findByName("ROLE_USER");
    }

    @Test
    void shouldReturnEmptyWhenRoleNotFound() {

        when(roleRepository.findByName("ROLE_ADMIN"))
                .thenReturn(Optional.empty());

        Optional<Role> result =
                roleService.findByName("ROLE_ADMIN");

        assertTrue(result.isEmpty());

        verify(roleRepository, times(1))
                .findByName("ROLE_ADMIN");
    }

    @Test
    void shouldFindAllRoles() {

        Role adminRole = new Role();
        adminRole.setId(2L);
        adminRole.setName("ROLE_ADMIN");

        when(roleRepository.findAll())
                .thenReturn(List.of(role, adminRole));

        List<Role> roles =
                roleService.findAll();

        assertNotNull(roles);
        assertEquals(2, roles.size());

        assertEquals(
                "ROLE_USER",
                roles.get(0).getName()
        );

        assertEquals(
                "ROLE_ADMIN",
                roles.get(1).getName()
        );

        verify(roleRepository, times(1))
                .findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoRolesExist() {

        when(roleRepository.findAll())
                .thenReturn(List.of());

        List<Role> roles =
                roleService.findAll();

        assertNotNull(roles);
        assertTrue(roles.isEmpty());

        verify(roleRepository, times(1))
                .findAll();
    }
}
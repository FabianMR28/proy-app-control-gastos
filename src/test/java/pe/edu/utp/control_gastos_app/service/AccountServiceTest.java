package pe.edu.utp.control_gastos_app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.utp.control_gastos_app.enums.AccountType;
import pe.edu.utp.control_gastos_app.model.Account;
import pe.edu.utp.control_gastos_app.model.User;
import pe.edu.utp.control_gastos_app.repository.AccountRepository;
import pe.edu.utp.control_gastos_app.repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountService accountService;

    private User user;
    private Account account;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);

        account = new Account();
        account.setId(1L);
        account.setName("Cuenta Principal");
        account.setType(AccountType.BANK);
        account.setBalance(BigDecimal.valueOf(1000));
        account.setCurrency("PEN");
        account.setUser(user);
    }

    @Test
    void shouldFindAccountsByUserId() {

        when(accountRepository.findByUserId(1L))
                .thenReturn(List.of(account));

        List<Account> accounts = accountService.findByUserId(1L);

        assertNotNull(accounts);
        assertEquals(1, accounts.size());
        assertEquals("Cuenta Principal", accounts.get(0).getName());
        assertEquals(AccountType.BANK, accounts.get(0).getType());

        verify(accountRepository, times(1))
                .findByUserId(1L);
    }

    @Test
    void shouldFindAccountById() {

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        Account result = accountService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Cuenta Principal", result.getName());
        assertEquals(AccountType.BANK, result.getType());

        verify(accountRepository, times(1))
                .findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenAccountNotFound() {

        when(accountRepository.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> accountService.findById(1L)
        );

        assertEquals(
                "Account not found with id: 1",
                exception.getMessage()
        );

        verify(accountRepository, times(1))
                .findById(1L);
    }

    @Test
    void shouldSaveAccount() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        accountService.save(account, 1L);

        assertNotNull(account.getUser());
        assertEquals(user, account.getUser());

        verify(userRepository, times(1))
                .findById(1L);

        verify(accountRepository, times(1))
                .save(account);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundOnSave() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> accountService.save(account, 1L)
        );

        assertEquals(
                "User not found with id: 1",
                exception.getMessage()
        );

        verify(userRepository, times(1))
                .findById(1L);

        verify(accountRepository, never())
                .save(any(Account.class));
    }

    @Test
    void shouldUpdateAccount() {

        Account updated = new Account();
        updated.setName("Cuenta Secundaria");
        updated.setType(AccountType.CASH);
        updated.setCurrency("USD");

        when(accountRepository.existsByIdAndUserId(1L, 1L))
                .thenReturn(true);

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        accountService.update(1L, updated, 1L);

        assertEquals("Cuenta Secundaria", account.getName());
        assertEquals(AccountType.CASH, account.getType());
        assertEquals("USD", account.getCurrency());

        verify(accountRepository, times(1))
                .existsByIdAndUserId(1L, 1L);

        verify(accountRepository, times(1))
                .findById(1L);

        verify(accountRepository, times(1))
                .save(account);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingInvalidAccount() {

        when(accountRepository.existsByIdAndUserId(1L, 1L))
                .thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> accountService.update(1L, account, 1L)
        );

        assertEquals(
                "Account not found or does not belong to user",
                exception.getMessage()
        );

        verify(accountRepository, times(1))
                .existsByIdAndUserId(1L, 1L);

        verify(accountRepository, never())
                .findById(anyLong());

        verify(accountRepository, never())
                .save(any(Account.class));
    }

    @Test
    void shouldDeleteAccount() {

        when(accountRepository.existsByIdAndUserId(1L, 1L))
                .thenReturn(true);

        accountService.delete(1L, 1L);

        verify(accountRepository, times(1))
                .existsByIdAndUserId(1L, 1L);

        verify(accountRepository, times(1))
                .deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingInvalidAccount() {

        when(accountRepository.existsByIdAndUserId(1L, 1L))
                .thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> accountService.delete(1L, 1L)
        );

        assertEquals(
                "Account not found or does not belong to user",
                exception.getMessage()
        );

        verify(accountRepository, times(1))
                .existsByIdAndUserId(1L, 1L);

        verify(accountRepository, never())
                .deleteById(anyLong());
    }
}
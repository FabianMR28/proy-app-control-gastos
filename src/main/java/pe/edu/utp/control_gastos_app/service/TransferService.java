package pe.edu.utp.control_gastos_app.service;

import org.springframework.stereotype.Service;
import pe.edu.utp.control_gastos_app.model.Account;
import pe.edu.utp.control_gastos_app.model.Transfer;
import pe.edu.utp.control_gastos_app.repository.AccountRepository;
import pe.edu.utp.control_gastos_app.repository.TransferRepository;

import java.util.List;

@Service
public class TransferService {

    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;

    public TransferService(TransferRepository transferRepository, AccountRepository accountRepository) {
        this.transferRepository = transferRepository;
        this.accountRepository = accountRepository;
    }

    public List<Transfer> findByUserId(Long userId) {
        return transferRepository.findBySourceAccountUserIdOrDestinationAccountUserId(userId, userId);
    }

    public Transfer findById(Long id) {
        return transferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transfer not found with id: " + id));
    }

    public void save(Transfer transfer, Long sourceAccountId, Long destinationAccountId) {
        if (sourceAccountId.equals(destinationAccountId)) {
            throw new RuntimeException("Source and destination accounts must be different");
        }

        Account source = accountRepository.findById(sourceAccountId)
                .orElseThrow(() -> new RuntimeException("Source account not found with id: " + sourceAccountId));
        Account destination = accountRepository.findById(destinationAccountId)
                .orElseThrow(() -> new RuntimeException("Destination account not found with id: " + destinationAccountId));

        if (source.getBalance().compareTo(transfer.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance in source account");
        }

        source.setBalance(source.getBalance().subtract(transfer.getAmount()));
        destination.setBalance(destination.getBalance().add(transfer.getAmount()));

        accountRepository.save(source);
        accountRepository.save(destination);

        transfer.setSourceAccount(source);
        transfer.setDestinationAccount(destination);
        transferRepository.save(transfer);
    }

    public void delete(Long id) {
        Transfer transfer = findById(id);

        Account source = transfer.getSourceAccount();
        Account destination = transfer.getDestinationAccount();

        source.setBalance(source.getBalance().add(transfer.getAmount()));
        destination.setBalance(destination.getBalance().subtract(transfer.getAmount()));

        accountRepository.save(source);
        accountRepository.save(destination);

        transferRepository.deleteById(id);
    }
}
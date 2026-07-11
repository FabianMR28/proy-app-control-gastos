package pe.edu.utp.control_gastos_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.utp.control_gastos_app.model.Transfer;

import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {
    List<Transfer> findBySourceAccountUserId(Long userId);
    List<Transfer> findBySourceAccountUserIdOrDestinationAccountUserId(Long userId, Long sameUserId);
}
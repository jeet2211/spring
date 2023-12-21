@Component
public class AutomatedLeaveTasks {

    @Autowired
    private LeaveService leaveService;

    @Scheduled(cron = "0 0 12 * * ?") // Run every day at 12 PM
    public void expireCompOffs() {
        // Logic to expire comp-offs not used within 30 days
        leaveService.expireCompOffs();
    }

    @Scheduled(cron = "0 0 12 * * ?") // Run every day at 12 PM
    public void autoApproveLeaveRequests() {
        // Logic to auto-approve leave requests pending for 30 days
        leaveService.autoApproveLeaveRequests();
    }

    @Scheduled(cron = "0 0 0 1 * ?") // Run on the 1st day of every month
    public void creditLeaveBalances() {
        // Logic to credit earned and casual/sick leaves at the beginning of the month
        leaveService.creditLeaveBalances();
    }
}
@Service
public class LeaveService {

    @Autowired
    private LeaveRepository leaveRepository;

    // Existing methods...

    public void expireCompOffs() {
        List<Leave> compOffsToExpire = leaveRepository.findByLeaveTypeAndStatusAndCreatedAtBefore(
                LeaveType.COMP_OFF, LeaveStatus.APPROVED, LocalDateTime.now().minusDays(30));

        compOffsToExpire.forEach(compOff -> {
            // Expire comp-off logic (e.g., decrement comp-off balance, update status)
            compOff.setStatus(LeaveStatus.EXPIRED);
            // Other logic...
        });

        leaveRepository.saveAll(compOffsToExpire);
    }

    public void autoApproveLeaveRequests() {
        List<Leave> pendingLeaveRequests = leaveRepository.findByStatusAndCreatedAtBefore(
                LeaveStatus.PENDING, LocalDateTime.now().minusDays(30));

        pendingLeaveRequests.forEach(leave -> {
            // Auto-approve leave request logic (e.g., update status)
            leave.setStatus(LeaveStatus.APPROVED);
            // Other logic...
        });

        leaveRepository.saveAll(pendingLeaveRequests);
    }

    public void creditLeaveBalances() {
        // Logic to credit earned and casual/sick leaves at the beginning of the month
        // Implementation details...
    }
}
@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {

    List<Leave> findByLeaveTypeAndStatusAndCreatedAtBefore(LeaveType leaveType, LeaveStatus status, LocalDateTime date);

    List<Leave> findByStatusAndCreatedAtBefore(LeaveStatus status, LocalDateTime date);

    // Other query methods...
}
public enum LeaveStatus {
    PENDING,
    APPROVED,
    REJECTED,
    EXPIRED
}

@Entity
public class Leave {

    // Existing fields...

    @Enumerated(EnumType.STRING)
    private LeaveStatus status;

    // Getters and setters...
}
@Component
public class LeaveMapper {

    public LeaveDTO leaveToLeaveDTO(Leave leave) {
        LeaveDTO leaveDTO = new LeaveDTO();
        leaveDTO.setId(leave.getId());
        leaveDTO.setUserId(leave.getUser().getId());
        leaveDTO.setLeaveType(leave.getLeaveType());
        leaveDTO.setStartDate(leave.getStartDate());
        leaveDTO.setEndDate(leave.getEndDate());
        leaveDTO.setReason(leave.getReason());
        leaveDTO.setStatus(leave.getStatus());
        // Set other leave details...

        return leaveDTO;
    }
}

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    }

    public void updateUserDetails(UserDTO userDTO) {
        User user = getCurrentUser();
        user.setUsername(userDTO.getUsername());
        // Update other user details...
        userRepository.save(user);
    }

    // Other user-related methods...
}
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @PutMapping("/update")
    public ResponseEntity<String> updateUserDetails(@RequestBody UserDTO userDTO) {
        userService.updateUserDetails(userDTO);
        return ResponseEntity.ok("User details updated successfully");
    }

    // Other user-related endpoints...
}
@Service
public class LeaveService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private LeaveMapper leaveMapper;

    public Leave getLeaveById(Long leaveId) {
        return leaveRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave", "id", leaveId));
    }

    public void cancelLeaveRequest(Long leaveId) {
        Leave leave = getLeaveById(leaveId);
        leaveRepository.delete(leave);
    }

    // Other leave-related methods...
}
@RestController
@RequestMapping("/api/leave")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private LeaveMapper leaveMapper;

    @GetMapping("/details/{leaveId}")
    public ResponseEntity<LeaveDTO> getLeaveDetails(@PathVariable Long leaveId) {
        Leave leave = leaveService.getLeaveById(leaveId);
        LeaveDTO leaveDTO = leaveMapper.leaveToLeaveDTO(leave);
        return ResponseEntity.ok(leaveDTO);
    }

    @DeleteMapping("/cancel/{leaveId}")
    public ResponseEntity<String> cancelLeaveRequest(@PathVariable Long leaveId) {
        leaveService.cancelLeaveRequest(leaveId);
        return ResponseEntity.ok("Leave request canceled successfully");
    }

    // Other leave-related endpoints...
}
@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, "Validation error");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
public class ErrorResponse {

    private HttpStatus status;
    private String message;

    public ErrorResponse(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    // Getters and setters...
}
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: %s", resourceName, fieldName, fieldValue));
    }
}

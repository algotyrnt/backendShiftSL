package com.shiftsl.backend.unittests.Service;

import com.google.firebase.database.DatabaseException;
import com.shiftsl.backend.DTO.LeaveDTO;
import com.shiftsl.backend.Exceptions.LeaveNotSavedException;
import com.shiftsl.backend.Exceptions.LeaveRetrievalException;
import com.shiftsl.backend.Service.LeaveService;
import com.shiftsl.backend.Service.ShiftService;
import com.shiftsl.backend.Service.UserService;
import com.shiftsl.backend.model.*;
import com.shiftsl.backend.repo.LeaveRepo;
import com.shiftsl.backend.unittests.Extensions.TimingExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(TimingExtension.class)
@ExtendWith(MockitoExtension.class)
public class LeaveServiceTest {

    @InjectMocks
    private LeaveService underTest;
    @Mock
    private LeaveRepo leaveRepo;
    @Mock
    private UserService userService;
    @Mock
    private ShiftService shiftService;

    @Mock
    private User testDoctor;
    @Mock
    private Shift testShift;
    @Mock
    private Leave testLeave;
    @Mock
    private LeaveDTO testLeaveDTO;

    private ArgumentCaptor<Leave> captor;

    private static final Long ID = 123456L;
    private static final String FIREBASEUID = "Yl2HkVQk3g9uPZtWbCmXoNRAesT1";
    private static List<Leave> testLeaves;
    private static HashSet<User> testDoctors;



    private static Leave createLeave(Long id, LeaveType type, String cause, Shift shift, User doctor, Status status) {
        Leave leave = new Leave();
        leave.setId(id);
        leave.setType(type);
        leave.setCause(cause);
        leave.setShift(shift);
        leave.setDoctor(doctor);
        leave.setStatus(status);
        return leave;
    }

    private static User createUser(Long id, String firstName, String lastName, String slmcReg,
                                   String firebaseUid, String phoneNo, String email, Role role) {
        User user = new User();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setSlmcReg(slmcReg);
        user.setFirebaseUid(firebaseUid);
        user.setPhoneNo(phoneNo);
        user.setEmail(email);
        user.setRole(role);
        return user;
    }



    @BeforeEach
    void setUp() {
        // create a mock user
        testDoctor = new User();
        testDoctor.setId(1L);
        // create a mock shift object
        testShift = new Shift();
        testShift.setId(2L);

        testLeave = new Leave();
        testLeave.setId(ID);
        testLeave.setType(LeaveType.SICK);
        testLeave.setCause("fever");
        testLeave.setShift(testShift);
        testLeave.setDoctor(testDoctor);
        testLeave.setStatus(Status.PENDING);
        captor = ArgumentCaptor.forClass(Leave.class);
    }

    @BeforeAll
    static void setUpList(){
        User fakeDoctor = new User();
        fakeDoctor.setId(ID);
        testLeaves = List.of(
                createLeave(1L, LeaveType.SICK, "Flu symptoms", new Shift(), new User(), Status.PENDING),
                createLeave(2L, LeaveType.CASUAL, "Family event", new Shift(), new User(), Status.APPROVED),
                createLeave(3L, LeaveType.CASUAL, "Vacation trip", new Shift(), new User(), Status.REJECTED),
                createLeave(4L, LeaveType.SICK, "Flu symptoms", new Shift(), fakeDoctor, Status.PENDING),
                createLeave(5L, LeaveType.SICK, "Flu symptoms", new Shift(), fakeDoctor, Status.PENDING)
        );

        testDoctors = new HashSet<>(Set.of(
                createUser(1L, "Alice", "Perera", "SLMC001", "uid-abc-1", "0712345678", "alice@example.com", Role.DOCTOR_TEMP),
                createUser(2L, "Bob", "Fernando", "SLMC002", "uid-abc-2", "0712345679", "bob@example.com", Role.DOCTOR_PERM),
                createUser(3L, "Charlie", "Wijesinghe", "SLMC003", "uid-abc-3", "0712345680", "charlie@example.com", Role.DOCTOR_PERM)
        ));

    }
    @Test
    void requestLeaveTest() {
        testLeaveDTO = new LeaveDTO(LeaveType.SICK, "fever", ID, ID);
        when(userService.getUserById(ID)).thenReturn(testDoctor);
        when(shiftService.getShiftByID(ID)).thenReturn(testShift);
        when(leaveRepo.save(any(Leave.class))).thenReturn(testLeave);

        underTest.requestLeave(testLeaveDTO);

        verify(leaveRepo).save(captor.capture());
        verify(userService).getUserById(ID);
        verify(shiftService).getShiftByID(ID);

        Leave capturedLeave = captor.getValue();
        assertEquals(testDoctor, capturedLeave.getDoctor());
        assertEquals(testShift, capturedLeave.getShift());
        assertEquals(Status.PENDING, capturedLeave.getStatus());
        assertEquals(LeaveType.SICK, capturedLeave.getType());
        assertEquals("fever", capturedLeave.getCause());
    }

    @Test
    void requestLeaveExceptionTest() {
        LeaveDTO testLeaveDTO = new LeaveDTO(LeaveType.SICK, "fever", ID, ID);
        when(userService.getUserById(ID)).thenThrow(new RuntimeException());

        Exception e = assertThrows(LeaveNotSavedException.class,() -> underTest.requestLeave(testLeaveDTO));
        assertEquals(String.format( "Failed to save leave request for doctorID=%d and shiftID=%d",
                testLeaveDTO.doctorID(), testLeaveDTO.shiftID()), e.getMessage());
    }

    @Test
    void getLeaveTest() {
        when(leaveRepo.findById(ID)).thenReturn(Optional.ofNullable(testLeave));
        underTest.getLeave(ID);
        verify(leaveRepo).findById(ID);
    }

    @Test
    void rejectTest() {
        when(leaveRepo.findById(ID)).thenReturn(Optional.of(testLeave));
        when(leaveRepo.save(any(Leave.class))).thenReturn(testLeave);
        String result = underTest.reject(ID);
        verify(leaveRepo).save(captor.capture());
        verify(leaveRepo).findById(ID);

        Leave capturedLeave = captor.getValue();
        System.out.println(capturedLeave.getStatus());
        assertEquals(Status.REJECTED, capturedLeave.getStatus());
        assertEquals("leave request rejected", result);
    }

    @Test
    void rejectExceptionTest() {
        when(leaveRepo.findById(ID)).thenThrow(new DatabaseException("Unable to access the database"));
        verify(leaveRepo, never()).save(testLeave); // verify that the leave object was never saved to database
        Exception e = assertThrows(LeaveNotSavedException.class,() -> underTest.approve(ID));
        assertEquals(String.format("Failed to update leave status in database for leaveId=%d", ID), e.getMessage());
    }

    @Test
    @Order(2)
    void approveTest() {
        testShift = Mockito.mock(Shift.class);
        when(shiftService.getShiftWithLock(2L)).thenReturn(testShift);
        when(testShift.getDoctors()).thenReturn(testDoctors);
        when(shiftService.updateShiftByID(testShift)).thenReturn(testShift);
        when(leaveRepo.findById(ID)).thenReturn(Optional.of(testLeave));
        when(leaveRepo.save(testLeave)).thenReturn(testLeave);

        String result = underTest.approve(ID);
        verify(shiftService).getShiftWithLock(2L);
        verify(shiftService).updateShiftByID(testShift);
        verify(testShift).getDoctors();
        verify(leaveRepo).save(captor.capture());

        Leave capturedLeave = captor.getValue();
        assertEquals("leave request approved", result);
        assertEquals(Status.APPROVED, capturedLeave.getStatus());
    }

    @Test
    void approveExceptionTest() {
        when(leaveRepo.findById(ID)).thenThrow(new DatabaseException("Unable to access the database"));
        verify(leaveRepo, never()).save(testLeave); // verify that the leave object was never saved to database
        Exception e = assertThrows(LeaveNotSavedException.class,() -> underTest.approve(ID));
        assertEquals(String.format("Failed to update leave status in database for leaveId=%d", ID), e.getMessage());
    }

    @Test
    void getLeavesTest() {
        when(leaveRepo.findAll()).thenReturn(testLeaves);
        List<Leave> mockLeaves = underTest.getLeaves();
        assertEquals(testLeaves, mockLeaves);
        verify(leaveRepo).findAll();
    }

    @Test
    void getLeavesExceptionTest() {
        when(leaveRepo.findAll()).thenThrow(new DatabaseException("Unable to access the database"));
        Exception ex = assertThrows(LeaveRetrievalException.class, () -> underTest.getLeaves());
        verify(leaveRepo).findAll();
        assertEquals("Error occurred while retrieving all leaves", ex.getMessage());
    }

    @Test
    void getLeaveByDoctorTest() {
        when(leaveRepo.findByDoctorId(ID))
                .thenReturn(testLeaves
                        .stream()
                        .filter(leave -> Objects.equals(leave.getDoctor().getId(), ID))
                        .collect(Collectors.toList()));

        List<Leave> result = underTest.getLeaveByDoctor(ID);
        verify(leaveRepo).findByDoctorId(ID);
        assertEquals(2, result.size());
    }

    @Test
    void getLeaveByDoctorExceptionTest() {
        when(leaveRepo.findByDoctorId(ID)).thenThrow(new DatabaseException("Unable to access the database"));
        Exception ex = assertThrows(LeaveRetrievalException.class, () -> underTest.getLeaveByDoctor(ID));
        verify(leaveRepo).findByDoctorId(ID);
        assertEquals(String.format("Error occurred while retrieving leaves for doctorID=%d",ID), ex.getMessage());
    }
}

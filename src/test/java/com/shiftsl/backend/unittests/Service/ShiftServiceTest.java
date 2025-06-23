package com.shiftsl.backend.unittests.Service;

import com.shiftsl.backend.DTO.ShiftDTO;
import com.shiftsl.backend.Exceptions.*;
import com.shiftsl.backend.Service.ShiftService;
import com.shiftsl.backend.Service.UserService;
import com.shiftsl.backend.Service.WardService;
import com.shiftsl.backend.model.Shift;
import com.shiftsl.backend.model.User;
import com.shiftsl.backend.model.Ward;
import com.shiftsl.backend.repo.ShiftRepo;
import com.shiftsl.backend.unittests.Extensions.TimingExtension;
import jakarta.transaction.TransactionalException;
import jakarta.persistence.PessimisticLockException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(TimingExtension.class)
@ExtendWith(MockitoExtension.class)
@Execution(ExecutionMode.CONCURRENT)
public class ShiftServiceTest {

    @InjectMocks
    private ShiftService underTest;
    @Mock
    private ShiftRepo shiftRepo;
    @Mock(strictness = Mock.Strictness.LENIENT)
    private UserService userService;
    @Mock
    private WardService wardService;

    private final Long id = 1L;

    private static Shift testShift;
    private static Ward testWard;
    private static User testDoctor;

    private static Set<Long> doctorIds;
    private static Set<User> doctors;
    private static List<Shift> shifts;
    private ArgumentCaptor<Shift> shiftCaptor = ArgumentCaptor.forClass(Shift.class);

    @BeforeEach
    void init() {
        testShift = new Shift();
        testShift.setId(1L);
        testShift.setDoctors(doctors);
        testShift.setNoOfDoctors(1);
        testShift.setTotalDoctors(2);
        testShift.setStartTime(LocalDateTime.MIN);
        testShift.setEndTime(LocalDateTime.MAX);
        testShift.setWard(testWard);
    }

    @BeforeAll
    static void setUp() {
        doctorIds = new HashSet<>(Set.of(
               1L
        ));

        doctors = new HashSet<>(Set.of(
                new User()
        ));

        shifts = new ArrayList<>(List.of(
                new Shift()
        ));
        testWard = new Ward();
        testDoctor = new User(); testDoctor.setId(1L);
    }

    @Test
    void createShiftTest() {
        ShiftDTO testShiftDTO = new ShiftDTO(1, LocalDateTime.MIN, LocalDateTime.MAX, doctorIds);
        User doctor1 = new User(); doctor1.setId(1L);
        when(userService.getUserById(1L)).thenReturn(doctor1);
        when(wardService.getWardByID(id)).thenReturn(testWard);
        when(shiftRepo.save(any(Shift.class))).thenReturn(testShift);

        underTest.createShift(testShiftDTO, id);

        verify(wardService).getWardByID(id);
        verify(shiftRepo).save(shiftCaptor.capture());

        Shift result = shiftCaptor.getValue();

        assertEquals(testShiftDTO.startTime(), result.getStartTime());
        assertEquals(testShiftDTO.endTime(), result.getEndTime());
        assertEquals(testShiftDTO.totalDoctors(), result.getTotalDoctors());
    }

    @Test
    void createShiftFailTest() {
        ShiftDTO testShiftDTO = new ShiftDTO(0, LocalDateTime.MIN, LocalDateTime.MAX, doctorIds);
        User doctor1 = new User(); doctor1.setId(1L);
        when(userService.getUserById(1L)).thenReturn(doctor1);
        when(wardService.getWardByID(id)).thenReturn(testWard);

        Exception ex = assertThrows(DoctorCountExceededException.class, () -> underTest.createShift(testShiftDTO, id));
        verify(wardService).getWardByID(id);
        verify(userService).getUserById(1L);
        verify(shiftRepo, never()).save(any(Shift.class));
        assertEquals("Number of assigned doctors exceeds the allowed limit.", ex.getMessage());
    }

    @Test
    void getAvailableShiftsTest() {
        when(shiftRepo.findAvailableShifts()).thenReturn(List.of(new Shift()));
        List<Shift> result = underTest.getAvailableShifts();
        verify(shiftRepo).findAvailableShifts();
        assertEquals(1, result.size());
    }

    @Test
    void getAvailableShiftsFailTest() {
        when(shiftRepo.findAvailableShifts()).thenThrow(TransactionalException.class);
        Exception ex = assertThrows(ShiftRetrievalException.class, () -> underTest.getAvailableShifts());
        verify(shiftRepo).findAvailableShifts();
        assertEquals("Error occurred while trying to retrieve available shifts from database", ex.getMessage());
    }

    @Test
    void claimShiftTest() {
        when(shiftRepo.findShiftWithLock(id)).thenReturn(Optional.of(testShift));
        when(userService.getUserById(id)).thenReturn(testDoctor);

        underTest.claimShift(id, id);
        verify(shiftRepo).save(shiftCaptor.capture());
        verify(userService).getUserById(id);
        verify(shiftRepo).findShiftWithLock(id);

        Shift result = shiftCaptor.getValue();
        assertEquals(testShift, result);
    }

    @Test
    void claimShiftLockExceptionTest() {
        when(shiftRepo.findShiftWithLock(id)).thenThrow(new PessimisticLockException("Unable to get lock", new SQLException(), "some sql string"));

        Exception ex = assertThrows(ShiftClaimFailedException.class, () -> underTest.claimShift(id, id));
        verify(shiftRepo).findShiftWithLock(id);
        verify(userService, never()).getUserById(id);
        verify(shiftRepo, never()).save(any(Shift.class));
        assertEquals("System is experiencing high load. Please try again later. Unable to get lock", ex.getMessage());
    }

    @Test
    void claimShiftGeneralExceptionTest() {
        when(shiftRepo.findShiftWithLock(id)).thenThrow(new TransactionalException("Error while trying to retrieve shift details from database", new SQLException()));

        Exception ex = assertThrows(ShiftClaimFailedException.class, () -> underTest.claimShift(id, id));
        verify(shiftRepo).findShiftWithLock(id);
        verify(userService, never()).getUserById(id);
        verify(shiftRepo, never()).save(any(Shift.class));
        assertEquals(String.format("Unable to claim shiftId: %d for doctorId: %d. Error while trying to retrieve shift details from database", 1, 1), ex.getMessage());
    }

    @Test
    void getShiftByIdTest() {
        when(shiftRepo.findById(id)).thenReturn(Optional.of(testShift));
        Shift result = underTest.getShiftByID(id);
        verify(shiftRepo).findById(id);
        assertEquals(testShift, result);
    }

    @Test
    void getShiftByIdFailTest() {
        when(shiftRepo.findById(id)).thenReturn(Optional.empty());
        Exception ex = assertThrows(ShiftNotFoundException.class, () -> underTest.getShiftByID(id));
        assertEquals("Shift ID - (" + id + ") not found.", ex.getMessage());
        verify(shiftRepo).findById(id);
    }

    @Test
    void getShiftWithLock() {
        when(shiftRepo.findShiftWithLock(id)).thenReturn(Optional.of(testShift));
        Shift result = underTest.getShiftWithLock(id);
        verify(shiftRepo).findShiftWithLock(id);
        assertEquals(testShift, result);
    }

    @Test
    void getShiftWithLockFailTest() {
        when(shiftRepo.findShiftWithLock(id)).thenReturn(Optional.empty());
        Exception ex = assertThrows(ShiftNotFoundException.class, () -> underTest.getShiftWithLock(id));
        assertEquals("Shift ID - (" + id + ") not found.", ex.getMessage());
        verify(shiftRepo).findShiftWithLock(id);
    }

    @Test
    void getShiftsForDoctorTest() {
        when(userService.getUserById(id)).thenReturn(testDoctor);
        when(shiftRepo.findByDoctors_Id(id)).thenReturn(shifts);

        List<Shift> result = underTest.getShiftsForDoctor(id);
        verify(shiftRepo).findByDoctors_Id(id);
        verify(userService).getUserById(id);
        assertArrayEquals(shifts.toArray(), result.toArray());
    }

    @Test
    void getShiftsForDoctorFailTest() {
        when(shiftRepo.findByDoctors_Id(id)).thenReturn(new ArrayList<>());
        Exception ex = assertThrows(ShiftsNotFoundException.class, () -> underTest.getShiftsForDoctor(id));
        verify(userService).getUserById(id);
        verify(shiftRepo).findByDoctors_Id(id);
        assertEquals("No shifts found for doctor with ID " + id, ex.getMessage());
    }

    @Test
    void getShiftsForDoctorForNonExistingDoctorTest() {
        when(userService.getUserById(id)).thenThrow(new UserNotFoundException("User - (" + id + ") not found."));
        Exception ex = assertThrows(UserNotFoundException.class, () -> underTest.getShiftsForDoctor(id));
        verify(userService).getUserById(id);
        verify(shiftRepo, never()).findByDoctors_Id(id);
        assertEquals("User - (" + id + ") not found.", ex.getMessage());
    }

    @Test
    void getAllShiftsTest() {
        // test for non-empty database
        when(shiftRepo.findAll()).thenReturn(shifts);
        List<Shift> result = underTest.getAllShifts();
        verify(shiftRepo).findAll();
        assertArrayEquals(shifts.toArray(), result.toArray());
        assertFalse(result.isEmpty());

        // test for empty database
        when(shiftRepo.findAll()).thenReturn(new ArrayList<>());
        List<Shift> resultEmpty = underTest.getAllShifts();
        verify(shiftRepo, times(2)).findAll();
        assertTrue(resultEmpty.isEmpty());

    }

    @Test
    void getAllShiftsFailTest() {
        when(shiftRepo.findAll()).thenThrow(new RuntimeException());
        Exception ex = assertThrows(Exception.class, () -> underTest.getAllShifts());
        verify(shiftRepo).findAll();
        assertEquals("Error occurred while trying to retrieve all shifts from database", ex.getMessage());
    }

    @Test
    void deleteShiftByIdTest() {
        doNothing().when(shiftRepo).delete(testShift);
        doReturn(Optional.of(testShift)).when(shiftRepo).findById(20L);

        underTest.deleteShiftByID(20L);
        verify(shiftRepo).findById(20L);
        verify(shiftRepo).delete(testShift);
    }

    @Test
    void deleteShiftByIdFailTest() {
        when(shiftRepo.findById(3L)).thenReturn(Optional.empty());
        Exception ex = assertThrows(ShiftRetrievalException.class, () -> underTest.deleteShiftByID(3L));
        assertEquals("Shift ID - (" + 3 + ") not found.", ex.getMessage());
    }

    @Test
    void updateShiftByIdTest() {
        when(shiftRepo.findById(testShift.getId())).thenReturn(Optional.of(testShift));
        when(shiftRepo.save(testShift)).thenReturn(testShift);
        Shift result = underTest.updateShiftByID(testShift);
        verify(shiftRepo).findById(testShift.getId());
        assertEquals(testShift, result);
    }

    @Test
    void getRosterTest() {
        when(shiftRepo.findByStartTimeBetween(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(shifts);
        List<Shift> result = underTest.getRoster(2);
        verify(shiftRepo).findByStartTimeBetween(any(LocalDateTime.class), any(LocalDateTime.class));
        assertArrayEquals(shifts.toArray(), result.toArray());
    }

    @Test
    void getRosterForInvalidMonthTest() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> underTest.getRoster(-1));
        assertEquals("Invalid month. Please provide a value between 1 and 12.", ex.getMessage());

        Exception ex2 = assertThrows(IllegalArgumentException.class, () -> underTest.getRoster(13));
        assertEquals("Invalid month. Please provide a value between 1 and 12.", ex2.getMessage());

        verify(shiftRepo, never()).findByStartTimeBetween(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void getRosterFailTest() {
        when(shiftRepo.findByStartTimeBetween(any(LocalDateTime.class), any(LocalDateTime.class))).thenThrow(new RuntimeException());
        Exception ex = assertThrows(ShiftsNotFoundException.class, () -> underTest.getRoster(2));
        assertEquals("Unable to retrieve shifts for the given month from database", ex.getMessage());
    }

}


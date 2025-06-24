package com.shiftsl.backend.unittests.Service;

import com.shiftsl.backend.Exceptions.ShiftNotFoundException;
import com.shiftsl.backend.Exceptions.ShiftSwapNotFoundException;
import com.shiftsl.backend.Service.ShiftService;
import com.shiftsl.backend.Service.ShiftSwapService;
import com.shiftsl.backend.Service.UserService;
import com.shiftsl.backend.model.*;
import com.shiftsl.backend.repo.ShiftSwapRepo;
import com.shiftsl.backend.unittests.Extensions.TimingExtension;
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

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(TimingExtension.class)
@ExtendWith(MockitoExtension.class)
@Execution(ExecutionMode.CONCURRENT)
public class ShiftSwapServiceTest {

    @InjectMocks
    private ShiftSwapService underTest;
    @Mock
    private ShiftSwapRepo shiftSwapRepo;
    @Mock
    private ShiftService shiftService;
    @Mock
    private UserService userService;

    private final Long id = 1L;

    private static Shift testShift;
    private static Ward testWard;
    private static User testDoctor;
    private static ShiftSwap testShiftSwap;

    private static Set<User> doctors = mock(Set.class);
    private static List<ShiftSwap> shiftSwaps = mock(List.class);
    private final ArgumentCaptor<ShiftSwap> shiftSwapCaptor = ArgumentCaptor.forClass(ShiftSwap.class);

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

        testShiftSwap = new ShiftSwap();
        testShiftSwap.setId(1L);
        testShiftSwap.setShift(testShift);
    }

    @BeforeAll
    static void setUp() {
//        doctorIds = new HashSet<>(Set.of(
//                1L
//        ));

//        doctors = new HashSet<>(Set.of(
//                new User()
//        ));

//        shiftSwaps = new ArrayList<>(List.of(
//                new ShiftSwap()
//        ));
        testWard = new Ward();
        testDoctor = new User(); testDoctor.setId(1L);

    }

    @Test
    void requestSwapTest() {
        when(shiftService.getShiftByID(id)).thenReturn(testShift);
        when(userService.getUserById(id)).thenReturn(testDoctor);
        when(userService.getUserById(id)).thenReturn(testDoctor);
        when(shiftSwapRepo.save(any(ShiftSwap.class))).thenReturn(new ShiftSwap());

        underTest.requestSwap(id, id, id);

        verify(shiftService).getShiftByID(id);
        verify(userService, times(2)).getUserById(id);
        verify(shiftSwapRepo).save(shiftSwapCaptor.capture());

        ShiftSwap result = shiftSwapCaptor.getValue();

        assertNotNull(result);
        assertEquals(result.getShift(), testShift);
        assertEquals(result.getSender(), testDoctor);
        assertEquals(result.getReceiver(), testDoctor);
        assertEquals(result.getReceiverStat(), StatusSwap.PENDING);
    }

    @Test
    void getShiftSwapTest() {
        when(shiftSwapRepo.findById(id)).thenReturn(Optional.of(testShiftSwap));
        ShiftSwap result = underTest.getShiftSwap(id);
        assertNotNull(result);
        verify(shiftSwapRepo).findById(id);
        assertEquals(testShiftSwap, result);
    }

    @Test
    void getShiftSwapExceptionTest() {
        when(shiftSwapRepo.findById(id)).thenReturn(Optional.empty());
        Exception ex = assertThrows(ShiftNotFoundException.class, () -> underTest.getShiftSwap(id));
        assertEquals("Shift swap " + id + "not found.", ex.getMessage());
        verify(shiftSwapRepo).findById(id);
    }

    @Test
    void acceptTest() {
        when(shiftSwapRepo.findById(id)).thenReturn(Optional.of(testShiftSwap));
        when(shiftService.updateShiftByID(testShift)).thenReturn(testShift);
        when(shiftSwapRepo.save(any(ShiftSwap.class))).thenReturn(new ShiftSwap());
        String result = underTest.accept(id);

        verify(shiftSwapRepo).findById(id);
        verify(shiftSwapRepo).save(testShiftSwap);
        verify(shiftService).updateShiftByID(testShift);

        verify(doctors).remove(testShiftSwap.getSender());
        verify(doctors).add(testShiftSwap.getReceiver());

        assertEquals("shift swap accepted by the receiving doctor", result);
    }

    @Test
    void rejectTest() {
        when(shiftSwapRepo.findById(id)).thenReturn(Optional.of(testShiftSwap));
        when(shiftSwapRepo.save(any(ShiftSwap.class))).thenReturn(testShiftSwap);

        String result = underTest.reject(id);

        verify(shiftSwapRepo).findById(id);
        verify(shiftSwapRepo).save(shiftSwapCaptor.capture());

        ShiftSwap savedSwap = shiftSwapCaptor.getValue();

        assertEquals("shift swap rejected by the receiving doctor", result);
        assertEquals(savedSwap, testShiftSwap);
    }

    @Test
    void getSwapReceivedByDoctorTest() {
        when(shiftSwapRepo.findByReceiverId(id)).thenReturn(shiftSwaps);
        List<ShiftSwap> result = underTest.getSwapReceivedByDoctor(id);
        verify(shiftSwapRepo).findByReceiverId(id);
        assertNotNull(result);
    }

    @Test
    void getSwapReceivedByDoctorExceptionTest() {
        when(shiftSwapRepo.findByReceiverId(id)).thenThrow(new RuntimeException());
        Exception ex = assertThrows(ShiftSwapNotFoundException.class, () -> underTest.getSwapReceivedByDoctor(id));
        assertEquals("Unable to find shift swap request details for doctor " + id, ex.getMessage());
    }

    @Test
    void getSwapSentByDoctorTest() {
        when(shiftSwapRepo.findBySenderId(id)).thenReturn(shiftSwaps);
        List<ShiftSwap> result = underTest.getSwapSentByDoctor(id);
        verify(shiftSwapRepo).findBySenderId(id);
        assertNotNull(result);
    }

    @Test
    void getSwapSentByDoctorExceptionTest() {
        when(shiftSwapRepo.findBySenderId(id)).thenThrow(new RuntimeException());
        Exception ex = assertThrows(ShiftSwapNotFoundException.class, () -> underTest.getSwapSentByDoctor(id));
        assertEquals("Unable to find shift swap request details by doctor " + id, ex.getMessage());
    }
}

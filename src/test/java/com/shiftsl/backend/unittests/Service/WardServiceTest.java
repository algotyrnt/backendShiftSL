package com.shiftsl.backend.unittests.Service;

import com.shiftsl.backend.Exceptions.NotAWardAdminException;
import com.shiftsl.backend.Exceptions.WardNotFoundException;
import com.shiftsl.backend.Service.UserService;
import com.shiftsl.backend.Service.WardService;
import com.shiftsl.backend.model.Role;
import com.shiftsl.backend.model.User;
import com.shiftsl.backend.model.Ward;
import com.shiftsl.backend.repo.WardRepo;
import com.shiftsl.backend.unittests.Extensions.TimingExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(TimingExtension.class)
@Execution(ExecutionMode.CONCURRENT)
public class WardServiceTest {

    @InjectMocks
    private WardService underTest;
    @Mock
    private WardRepo wardRepo;
    @Mock
    private UserService userService;

    private final Long id = 1L;
    private final String wardName = "testWard";
    private final List<Ward> wards = mock(List.class);
    private final Ward mockWard = mock(Ward.class);
    private final ArgumentCaptor<Ward> wardCaptor = ArgumentCaptor.forClass(Ward.class);

    private static User wardAdmin;

    @BeforeAll
    static void setUp() {
        wardAdmin = new User();
        wardAdmin.setRole(Role.WARD_ADMIN);
    }

    @Test
    void createWardTest() {
        when(userService.getUserById(id)).thenReturn(wardAdmin);
        when(wardRepo.save(any(Ward.class))).thenReturn(mockWard);
        underTest.createWard(id, wardName);

        verify(userService).getUserById(id);
        verify(wardRepo).save(wardCaptor.capture());
        Ward result = wardCaptor.getValue();
        assertEquals(wardName, result.getName());
        assertEquals(wardAdmin, result.getWardAdmin());
    }

    @Test
    void createWithoutWardAdminTest() {
        User notWardAdmin = new User();
        notWardAdmin.setRole(Role.HR_ADMIN);
        when(userService.getUserById(id)).thenReturn(notWardAdmin);
        Exception ex = assertThrows(NotAWardAdminException.class, () -> underTest.createWard(id, wardName));
        assertEquals("Given user is not a ward admin", ex.getMessage());
        verify(userService).getUserById(id);
        verify(wardRepo, never()).save(any(Ward.class));
    }

    @Test
    void updateWardTest() {
        when(wardRepo.save(mockWard)).thenReturn(mockWard);
        Ward result = underTest.updateWard(mockWard);
        verify(wardRepo).save(mockWard);
        assertNotNull(result);
    }

    @Test
    void findByNameTest() {
        when(wardRepo.findByName(wardName)).thenReturn(Optional.ofNullable(mockWard));
        Optional<Ward> result = underTest.findByName(wardName);
        verify(wardRepo).findByName(wardName);
        assertNotNull(result);
    }

    @Test
    void getWardList() {
        when(wardRepo.findAll()).thenReturn(wards);
        List<Ward> result = underTest.getWardList();
        assertNotNull(result);
        verify(wardRepo).findAll();
    }

    @Test
    void deleteWardTest() {
        doNothing().when(wardRepo).deleteById(id);
        underTest.deleteWardById(id);
        verify(wardRepo).deleteById(id);
    }

    @Test
    void getWardByIDTest() {
        when(wardRepo.findById(id)).thenReturn(Optional.ofNullable(mockWard));
        Ward result = underTest.getWardByID(id);
        assertNotNull(result);
        verify(wardRepo).findById(id);
    }

    @Test
    void getWardByIDForNonExistingWardTest() {
        when(wardRepo.findById(id)).thenReturn(Optional.empty());
        Exception ex = assertThrows(WardNotFoundException.class, () -> underTest.getWardByID(id));
        assertEquals("Ward (ID - " + id + " not found.", ex.getMessage());
    }

}

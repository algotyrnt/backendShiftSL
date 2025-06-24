package com.shiftsl.backend.unittests.Service;

import com.shiftsl.backend.DTO.UserDTO;
import com.shiftsl.backend.Exceptions.PhoneAlreadyInUseException;
import com.shiftsl.backend.Exceptions.UserNotFoundException;
import com.shiftsl.backend.Exceptions.UserNotUpdatedException;
import com.shiftsl.backend.Service.FirebaseAuthService;
import com.shiftsl.backend.Service.UserService;
import com.shiftsl.backend.model.Role;
import com.shiftsl.backend.model.User;
import com.shiftsl.backend.repo.UserRepo;
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
public class UserServiceTest {

    @InjectMocks
    private UserService underTest;
    @Mock
    private UserRepo userRepo;
    @Mock
    private FirebaseAuthService firebaseAuthService;

    private final Long id = 1L;
    private final String firebaseUid = "123456";
    private final List<User> users = mock(List.class);
    private final User mockUser = mock(User.class);
    private final ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

    private static UserDTO fakeUserDTO;

    @BeforeAll
    static void setUp() {
        fakeUserDTO = new UserDTO("John", "Doe", "1234567", "john.doe@gmail.com", "0771234598", Role.HR_ADMIN);
    }

    @Test
    void registerUserTest() {
        when(userRepo.findByPhoneNo(fakeUserDTO.phoneNo())).thenReturn(Optional.empty());
        when(firebaseAuthService.createUser(fakeUserDTO.email())).thenReturn(firebaseUid);
        when(userRepo.save(any(User.class))).thenReturn(mockUser);

        underTest.registerUser(fakeUserDTO);
        verify(userRepo).findByPhoneNo(fakeUserDTO.phoneNo());
        verify(firebaseAuthService).createUser(fakeUserDTO.email());
        verify(userRepo).save(userCaptor.capture());

        User result = userCaptor.getValue();
        assertEquals(fakeUserDTO.firstName(), result.getFirstName());
        assertEquals(fakeUserDTO.lastName(), result.getLastName());
        assertEquals(fakeUserDTO.email(), result.getEmail());
        assertEquals(fakeUserDTO.phoneNo(), result.getPhoneNo());
        assertEquals(fakeUserDTO.role(), result.getRole());
        assertEquals(fakeUserDTO.slmcReg(), result.getSlmcReg());
    }

    @Test
    void registerExistingUserTest() {
        when(userRepo.findByPhoneNo(fakeUserDTO.phoneNo())).thenReturn(Optional.of(mockUser));
        Exception ex = assertThrows(PhoneAlreadyInUseException.class, () -> underTest.registerUser(fakeUserDTO));
        assertEquals("Phone Number already in use.", ex.getMessage());
        verify(userRepo).findByPhoneNo(fakeUserDTO.phoneNo());
        verify(userRepo, never()).save(any(User.class));
        verify(firebaseAuthService, never()).createUser(any(String.class));
    }

    @Test
    void getUserByIdTest() {
        when(userRepo.findById(id)).thenReturn(Optional.of(mockUser));
        User result = underTest.getUserById(id);
        verify(userRepo).findById(id);
        assertNotNull(result);
    }

    @Test
    void getUserByIdExceptionTest() {
        when(userRepo.findById(id)).thenReturn(Optional.empty());
        Exception ex = assertThrows(UserNotFoundException.class, () -> underTest.getUserById(id));
        assertEquals("User - (" + id + ") not found.", ex.getMessage());
    }

    @Test
    void getUserByRoleTest() {
        when(userRepo.findByRoleIn(List.of(Role.HR_ADMIN))).thenReturn(users);
        List<User> result = underTest.getUsersByRole(Role.HR_ADMIN);
        assertNotNull(result);
        verify(userRepo).findByRoleIn(List.of(Role.HR_ADMIN));
    }

    @Test
    void updateUserByIdTest() {
        when(userRepo.findById(id)).thenReturn(Optional.of(mockUser));
        when(userRepo.save(mockUser)).thenReturn(mockUser);
        User result = underTest.updateUserById(id, mockUser);

        verify(userRepo).findById(id);
        verify(userRepo).save(mockUser);
        assertNotNull(result);
    }

    @Test
    void updateUserByIdExceptionTest() {
        when(userRepo.findById(id)).thenReturn(Optional.empty());
        assertThrows(UserNotUpdatedException.class, () -> underTest.updateUserById(id, mockUser));
    }

    @Test
    void deleteUserByIdTest() {
        doNothing().when(userRepo).deleteById(id);
        String result = underTest.deleteUserById(id);
        verify(userRepo).deleteById(id);
        assertEquals("User deleted successfully.", result);
    }

    @Test
    void findUserByFirebaseUidTest() {
        when(userRepo.findByFirebaseUid(firebaseUid)).thenReturn(Optional.ofNullable(mockUser));
        User result = underTest.findUserByFirebaseUid(firebaseUid);
        assertNotNull(result);
        verify(userRepo).findByFirebaseUid(firebaseUid);
    }

    @Test
    void findUserByFirebaseUidExceptionTest() {
        when(userRepo.findByFirebaseUid(firebaseUid)).thenReturn(Optional.empty());
        Exception ex = assertThrows(UserNotFoundException.class, () -> underTest.findUserByFirebaseUid(firebaseUid));
        assertEquals("Unable to find user by Firebase UID - " + firebaseUid, ex.getMessage());
    }

    @Test
    void getAllUsersTest() {
        when(userRepo.findAll()).thenReturn(users);
        List<User> result = underTest.getAllUsers();
        assertNotNull(result);
        verify(userRepo).findAll();
    }

    @Test
    void getDoctorsTest() {
        when(userRepo.findByRoleIn(List.of(Role.DOCTOR_PERM, Role.DOCTOR_TEMP))).thenReturn(users);
        List<User> result = underTest.getDoctors();
        verify(userRepo).findByRoleIn(List.of(Role.DOCTOR_PERM, Role.DOCTOR_TEMP));
        assertNotNull(result);
    }
}

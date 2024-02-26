package hospital.server.grpc;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import hospital.server.model.Patient;
import hospital.server.repository.HospitalRepository;
import hospital.server.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import io.grpc.stub.StreamObserver;
import hospital.server.grpc.PatientOuterClass.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PatientServiceEndpointImplTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientServiceEndpointImpl patientService;

    @Test
    void createPatientTest() {
        CreatePatientRequest request = CreatePatientRequest.newBuilder()
                .setName("testName")
                .setLastName("testLastName")
                .setSex("male")
                .setBirthDate("11/04/1999")
                .build();
        StreamObserver<PatientResponse> responseObserver = Mockito.mock(StreamObserver.class);
        Patient mockPatient = Patient.builder()
                .id(1L)
                .name(request.getName())
                .lastName(request.getLastName())
                .sex(request.getSex())
                .birthDate(new Date(1999, 4, 11))
                .build();

        when(patientRepository.save(any(Patient.class))).thenReturn(mockPatient);

        patientService.createPatient(request, responseObserver);

        verify(patientRepository).save(any(Patient.class));
        verify(responseObserver).onNext(any(PatientResponse.class));
        verify(responseObserver).onCompleted();
    }

    @Test
    void deleteExistingPatientTest() {
        DeletePatientRequest request = DeletePatientRequest.newBuilder()
                .setId(1L)
                .build();
        StreamObserver<DeleteResponse> responseObserver = Mockito.mock(StreamObserver.class);

        when(patientRepository.existsById(1L)).thenReturn(true);

        patientService.deletePatient(request, responseObserver);

        verify(patientRepository).existsById(1L);
        verify(responseObserver).onNext(DeleteResponse.newBuilder().setSuccess(true).build());
        verify(responseObserver).onCompleted();
    }

    @Test
    void deleteNonExistingPatientTest() {
        DeletePatientRequest request = DeletePatientRequest.newBuilder()
                .setId(1L)
                .build();
        StreamObserver<DeleteResponse> responseObserver = Mockito.mock(StreamObserver.class);

        when(patientRepository.existsById(1L)).thenReturn(false);

        patientService.deletePatient(request, responseObserver);

        verify(patientRepository).existsById(1L);
        verify(responseObserver).onNext(DeleteResponse.newBuilder().setSuccess(false).build());
        verify(responseObserver).onCompleted();
    }

    @Test
    void findByExistingIdTest() {
        FindByIdRequest request = FindByIdRequest.newBuilder()
                .setId(1L)
                .build();
        StreamObserver<PatientResponse> responseObserver = Mockito.mock(StreamObserver.class);

        Patient mockPatient = Patient.builder()
                .id(1L)
                .name("testName")
                .lastName("testLastName")
                .sex("male")
                .birthDate(new Date(1999, 4, 11))
                .build();

        when(patientRepository.findById(1L)).thenReturn(Optional.ofNullable(mockPatient));

        patientService.findById(request, responseObserver);

        verify(patientRepository).findById(1L);
        verify(responseObserver).onNext(any(PatientResponse.class));
        verify(responseObserver).onCompleted();
    }

    @Test
    void findByNonExistingIdTest() {
        FindByIdRequest request = FindByIdRequest.newBuilder()
                .setId(1L)
                .build();
        StreamObserver<PatientResponse> responseObserver = Mockito.mock(StreamObserver.class);

        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        patientService.findById(request, responseObserver);

        verify(patientRepository).findById(1L);
        verify(responseObserver).onError(any(RuntimeException.class));
    }

}

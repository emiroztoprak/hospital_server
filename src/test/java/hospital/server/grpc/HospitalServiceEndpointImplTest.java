package hospital.server.grpc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import hospital.server.model.Hospital;
import hospital.server.repository.HospitalRepository;
import hospital.server.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import io.grpc.stub.StreamObserver;
import hospital.server.grpc.HospitalOuterClass.*;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class HospitalServiceEndpointImplTest {

    @Mock
    private HospitalRepository hospitalRepository;


    @InjectMocks
    private HospitalServiceEndpointImpl hospitalService;

    @Test
    void createHospitalTest() {
        CreateHospitalRequest request = CreateHospitalRequest.newBuilder()
                .setName("Test Hospital")
                .setAddress("123 Test Lane")
                .build();
        StreamObserver<HospitalResponse> responseObserver = Mockito.mock(StreamObserver.class);
        Hospital mockHospital = Hospital.builder()
                .id(1L)
                .name(request.getName())
                .address(request.getAddress())
                .build();

        when(hospitalRepository.save(any(Hospital.class))).thenReturn(mockHospital);

        hospitalService.createHospital(request, responseObserver);

        verify(hospitalRepository).save(any(Hospital.class));
        verify(responseObserver).onNext(any(HospitalResponse.class));
        verify(responseObserver).onCompleted();
    }

    @Test
    void deleteExistingHospitalTest() {
        DeleteHospitalRequest request = DeleteHospitalRequest.newBuilder()
                .setId(1L)
                .build();
        StreamObserver<DeleteResponse> responseObserver = Mockito.mock(StreamObserver.class);

        when(hospitalRepository.existsById(1L)).thenReturn(true);

        hospitalService.deleteHospital(request, responseObserver);

        verify(hospitalRepository).existsById(1L);
        verify(responseObserver).onNext(DeleteResponse.newBuilder().setSuccess(true).build());
        verify(responseObserver).onCompleted();
    }

    @Test
    void deleteNonExistingHospitalTest() {
        DeleteHospitalRequest request = DeleteHospitalRequest.newBuilder()
                .setId(1L)
                .build();
        StreamObserver<DeleteResponse> responseObserver = Mockito.mock(StreamObserver.class);

        when(hospitalRepository.existsById(1L)).thenReturn(false);

        hospitalService.deleteHospital(request, responseObserver);

        verify(hospitalRepository).existsById(1L);
        verify(responseObserver).onNext(DeleteResponse.newBuilder().setSuccess(false).build());
        verify(responseObserver).onCompleted();
    }

    @Test
    void findByExistingIdTest() {
        FindByIdRequest request = FindByIdRequest.newBuilder()
                .setId(1L)
                .build();
        StreamObserver<HospitalResponse> responseObserver = Mockito.mock(StreamObserver.class);

        Hospital mockHospital = Hospital.builder()
                .id(1L)
                .name("hospital")
                .address("Munich")
                .build();

        when(hospitalRepository.findById(1L)).thenReturn(Optional.ofNullable(mockHospital));

        hospitalService.findById(request, responseObserver);

        verify(hospitalRepository).findById(1L);
        verify(responseObserver).onNext(any(HospitalResponse.class));
        verify(responseObserver).onCompleted();
    }

    @Test
    void findByNonExistingIdTest() {
        FindByIdRequest request = FindByIdRequest.newBuilder()
                .setId(1L)
                .build();
        StreamObserver<HospitalResponse> responseObserver = Mockito.mock(StreamObserver.class);

        Hospital mockHospital = Hospital.builder()
                .id(1L)
                .name("hospital")
                .address("Munich")
                .build();

        when(hospitalRepository.findById(1L)).thenReturn(Optional.empty());

        hospitalService.findById(request, responseObserver);

        verify(hospitalRepository).findById(1L);
        verify(responseObserver).onError(any(RuntimeException.class));
    }

}


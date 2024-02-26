package hospital.server.grpc;

import hospital.server.model.Hospital;
import hospital.server.model.Patient;
import hospital.server.repository.HospitalRepository;
import hospital.server.repository.PatientRepository;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import hospital.server.grpc.HospitalOuterClass.*;
import hospital.server.grpc.HospitalServiceEndpointGrpc.*;
import org.hibernate.Hibernate;

import java.util.List;
import java.util.Optional;

@GrpcService
@AllArgsConstructor
public class HospitalServiceEndpointImpl extends HospitalServiceEndpointImplBase {
    private final HospitalRepository hospitalRepository;

    private final PatientRepository patientRepository;


    @Override
    public void findById(FindByIdRequest request, StreamObserver<HospitalResponse> responseObserver) {
        try {
            long id = request.getId();
            Optional<Hospital> optionalHospitalData = hospitalRepository.findById(id);

            if (optionalHospitalData.isEmpty()) {
                // Patient not found
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("Hospital with ID " + request.getId() + " does not exist.")
                        .asRuntimeException());
            } else {
                // Patient found
                HospitalResponse hospitalResponse = convertToHospitalResponse(optionalHospitalData.get());
                responseObserver.onNext(hospitalResponse);
                responseObserver.onCompleted();
            }
        } catch (Exception e) {
            // Handle unexpected errors.
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal error occurred: " + e.getMessage())
                    .asRuntimeException());
        }
    }


    @Override
    @Transactional
    public void listHospitalsByPatientId(ListByPatientIdRequest request, StreamObserver<ListHospitalsResponse> responseObserver) {
        long patientId = request.getPatientId();
        Optional<Patient> patientOptional = patientRepository.findById(patientId);
        if (patientOptional.isEmpty()) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Patient with ID " + patientId + " does not exist.")
                    .asRuntimeException());
        }
        Patient patient = patientOptional.get();
        Hibernate.initialize(patient.getHospitals());
        List<Hospital> hospitalList = patient.getHospitals();

        ListHospitalsResponse.Builder responseBuilder = ListHospitalsResponse.newBuilder();
        hospitalList.forEach(hospitalData -> {
            HospitalResponse hospitalResponse = convertToHospitalResponse(hospitalData);
            responseBuilder.addHospitals(hospitalResponse);
        });
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void listAllHospitals(Empty request, StreamObserver<ListHospitalsResponse> responseObserver) {
        List<Hospital> hospitalDataList = hospitalRepository.findAll();

        ListHospitalsResponse.Builder responseBuilder = ListHospitalsResponse.newBuilder();
        hospitalDataList.forEach(hospitalData -> {
            HospitalResponse hospitalResponse = convertToHospitalResponse(hospitalData);
            responseBuilder.addHospitals(hospitalResponse);
        });

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }


    @Override
    public void createHospital(CreateHospitalRequest request, StreamObserver<HospitalResponse> responseObserver) {
        Hospital hospital = hospitalRepository.save(Hospital.builder()
                .name(request.getName())
                .address(request.getAddress())
                .build());

        HospitalResponse response = convertToHospitalResponse(hospital);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updateHospital(UpdateHospitalRequest request, StreamObserver<HospitalResponse> responseObserver) {
        Hospital hospital = hospitalRepository.save(Hospital.builder()
                .id(request.getId())
                .name(request.getName())
                .address(request.getAddress())
                .build());


        HospitalResponse response = convertToHospitalResponse(hospital);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteHospital(DeleteHospitalRequest request, StreamObserver<DeleteResponse> responseObserver) {
        Optional.ofNullable(request)
                .map(DeleteHospitalRequest::getId)
                .ifPresent(hospitalRepository::deleteById);

        responseObserver.onNext(DeleteResponse.newBuilder()
                .setSuccess(true).build());
        responseObserver.onCompleted();
    }

    private HospitalResponse convertToHospitalResponse(Hospital hospital) {
        return HospitalResponse.newBuilder()
                .setId(hospital.getId())
                .setName(hospital.getName())
                .setAddress(hospital.getAddress())
                .build();
    }
}


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
import hospital.server.grpc.PatientOuterClass.*;
import hospital.server.grpc.PatientServiceEndpointGrpc.*;
import org.hibernate.Hibernate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@GrpcService
@AllArgsConstructor
public class PatientServiceEndpointImpl extends PatientServiceEndpointImplBase {
    private final PatientRepository patientRepository;

    private final HospitalRepository hospitalRepository;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");


    @Override
    public void findById(FindByIdRequest request, StreamObserver<PatientResponse> responseObserver) {
        try {
            long id = request.getId();
            Optional<Patient> optionalPatientData = patientRepository.findById(id);

            if (optionalPatientData.isEmpty()) {
                // Patient not found
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("Patient with ID " + request.getId() + " does not exist.")
                        .asRuntimeException());
            } else {
                // Patient found
                PatientResponse patientResponse = convertToPatientResponse(optionalPatientData.get());
                responseObserver.onNext(patientResponse);
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
    public void registerPatientInHospital(RegisterPatientRequest request, StreamObserver<RegisterPatientResponse> responseObserver) {
        long hospitalId = request.getHospitalId();
        long patientId = request.getPatientId();
        Optional<Patient> patientOptional = patientRepository.findById(patientId);

        if (patientOptional.isEmpty()) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Patient with ID " + patientId + " does not exist.")
                    .asRuntimeException());
        }
        Patient patient = patientOptional.get();

        Optional<Hospital> hospitalOptional = hospitalRepository.findById(hospitalId);

        if (hospitalOptional.isEmpty()) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Hospital with ID " + hospitalId + " does not exist.")
                    .asRuntimeException());
        }

        Hospital hospital = hospitalOptional.get();
        Hibernate.initialize(hospital.getPatients());

        List<Patient> patients = hospital.getPatients();
        patients.add(patient);

        responseObserver.onNext(RegisterPatientResponse.newBuilder().setSuccess(true).build());
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void listPatientsByHospitalId(ListByHospitalIdRequest request, StreamObserver<ListPatientsResponse> responseObserver) {
        long hospitalId = request.getHospitalId();
        Optional<Hospital> hospitalOptional = hospitalRepository.findById(hospitalId);

        if (hospitalOptional.isEmpty()) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Patient with ID " + hospitalId + " does not exist.")
                    .asRuntimeException());
        }

        Hospital hospital = hospitalOptional.get();
        Hibernate.initialize(hospital.getPatients());
        List<Patient> patientList = hospital.getPatients();

        ListPatientsResponse.Builder responseBuilder = ListPatientsResponse.newBuilder();
        patientList.forEach(patientData -> {
            PatientResponse patientResponse = convertToPatientResponse(patientData);
            responseBuilder.addPatients(patientResponse);
        });
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void listAllPatients(Empty request, StreamObserver<ListPatientsResponse> responseObserver) {
        List<Patient> patientList = patientRepository.findAll();
        ListPatientsResponse.Builder responseBuilder = ListPatientsResponse.newBuilder();
        patientList.forEach(patientData -> {
            PatientResponse patientResponse = convertToPatientResponse(patientData);
            responseBuilder.addPatients(patientResponse);
        });

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }


    @Override
    public void createPatient(CreatePatientRequest request, StreamObserver<PatientResponse> responseObserver) {
        try {
            Date birthDate = dateFormat.parse(request.getBirthDate());

            Patient patient = patientRepository.save(Patient.builder()
                    .sex(request.getSex())
                    .name(request.getName())
                    .lastName(request.getLastName())
                    .birthDate(birthDate)
                    .build());

            PatientResponse response = convertToPatientResponse(patient);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (ParseException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid date format: " + e.getMessage())
                    .asRuntimeException());
        }
    }


    @Override
    public void updatePatient(UpdatePatientRequest request, StreamObserver<PatientResponse> responseObserver) {
        try {
            Date birthDate = dateFormat.parse(request.getBirthDate());

            Patient patient = patientRepository.save(Patient.builder()
                    .id(request.getId())
                    .sex(request.getSex())
                    .name(request.getName())
                    .lastName(request.getLastName())
                    .birthDate(birthDate)
                    .build());

            PatientResponse response = convertToPatientResponse(patient);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (ParseException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid date format: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void deletePatient(DeletePatientRequest request, StreamObserver<DeleteResponse> responseObserver) {
        Optional<Long> optionalId = Optional.ofNullable(request).map(DeletePatientRequest::getId);

        if (optionalId.isPresent()) {
            long id = optionalId.get();
            if (patientRepository.existsById(id)) {
                patientRepository.deleteById(id);
                responseObserver.onNext(DeleteResponse.newBuilder()
                        .setSuccess(true)
                        .build());
            } else {
                responseObserver.onNext(DeleteResponse.newBuilder()
                        .setSuccess(false)
                        .build());
            }
        } else {
            responseObserver.onNext(DeleteResponse.newBuilder()
                    .setSuccess(false)
                    .build());
        }

        responseObserver.onCompleted();
    }

    private PatientResponse convertToPatientResponse(Patient patient) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(patient.getBirthDate());
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        String date = String.format("%s/%s/%s", day, month, year);
        return PatientResponse.newBuilder()
                .setId(patient.getId())
                .setName(patient.getName())
                .setLastName(patient.getLastName())
                .setBirthDate(date)
                .setSex(patient.getSex())
                .build();
    }
}

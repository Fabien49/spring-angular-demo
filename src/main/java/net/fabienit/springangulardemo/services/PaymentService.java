package net.fabienit.springangulardemo.services;

import net.fabienit.springangulardemo.dtos.NewPaymentDTO;
import net.fabienit.springangulardemo.entities.Payment;
import net.fabienit.springangulardemo.entities.PaymentStatus;
import net.fabienit.springangulardemo.entities.PaymentType;
import net.fabienit.springangulardemo.entities.Student;
import net.fabienit.springangulardemo.repository.PaymentRepository;
import net.fabienit.springangulardemo.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.UUID;

@Service
@Transactional
public class PaymentService {
    private StudentRepository studentRepository;
    private PaymentRepository paymentRepository;

    public PaymentService(StudentRepository studentRepository, PaymentRepository paymentRepository) {
        this.studentRepository = studentRepository;
        this.paymentRepository = paymentRepository;
    }

    public Payment savePayment(MultipartFile file, NewPaymentDTO newPaymentDTO) throws IOException {

        Path folderPath = Paths.get(System.getProperty("user.home"), "other-data", "payments");
        if(!Files.exists(folderPath)){
            Files.createDirectories(folderPath);
        }
        String fileName = UUID.randomUUID().toString();
        Path filePath = Paths.get(System.getProperty("user.home"), "other-data", "payments", fileName+".pdf");
        Files.copy(file.getInputStream(),filePath);
        Student student = studentRepository.findByCode(newPaymentDTO.getStudentCode());
        Payment payment = Payment.builder()
                .date(newPaymentDTO.getDate()).type(newPaymentDTO.getType()).student(student)
                .amount(newPaymentDTO.getAmount())
                .file(filePath.toUri().toString())
                .status(PaymentStatus.CREATED)
                .build();
        return paymentRepository.save(payment);
    }

    public Payment updatePaymentStatus(PaymentStatus status, @PathVariable Long id){
        Payment payment = paymentRepository.findById(id).get();
        payment.setStatus(status);
        return paymentRepository.save(payment);
    }

    public byte[] getPaymentFile(Long id) throws IOException {
        Payment payment = paymentRepository.findById(id).get();
        return Files.readAllBytes(Path.of(URI.create(payment.getFile())));
    }
}

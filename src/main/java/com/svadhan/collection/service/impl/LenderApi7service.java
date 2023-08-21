package com.svadhan.collection.service.impl;

import com.svadhan.collection.banking.entity.Emi;
import com.svadhan.collection.banking.repository.EmiRepository;
import com.svadhan.collection.entity.Loan;
import com.svadhan.collection.model.response.LoanEmiDues;
import com.svadhan.collection.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LenderApi7service {
    private final LenderAPIService lenderAPIService;
    private final LoanRepository loanRepository;
    private final EmiRepository emiRepository;

    @Scheduled(cron = "0 59 23 * * *") // Run at 11:59 pm every day
    public void processLenderAPI7() {
        List<LoanEmiDues> loanEmiDues = lenderAPIService.lenderApi7();
        List<String> loanIds = loanEmiDues.stream().map(e -> e.getLoanId()).collect(Collectors.toList());
        List<Loan> allLoans = loanRepository.findAllByLenderLoanIdIn(loanIds);
        Map<String, Loan> loanIdLoanMap = allLoans.stream().collect(Collectors.toMap(Loan::getLenderLoanId, Function.identity()));
        for (LoanEmiDues loanEmiDue : loanEmiDues) {
            if (!loanIdLoanMap.containsKey(loanEmiDue.getLoanId()))
                continue;
            Loan loan = loanIdLoanMap.get(loanEmiDue.getLoanId());
            List<Emi> emis = emiRepository.findByLoanIdAndDueDateEquals(loan.getId(), loanEmiDue.getEMIDate());
            if (CollectionUtils.isEmpty(emis)) {
                Long installmentNumber = 1l;
                List<Emi> allByLoanIdAndDueDateBefore = emiRepository.findAllByLoanIdAndDueDateBefore(loan.getId(), loanEmiDue.getEMIDate());
                Optional<Emi> max = allByLoanIdAndDueDateBefore.stream().max(Comparator.comparing(Emi::getDueDate));
                if (max.isPresent())
                    installmentNumber = max.get().getInstallmentNo() + 1;
                Emi emi = new Emi();
                emi.setCreatedOn(LocalDateTime.now());
                emi.setInstallmentNo(installmentNumber);
                emi.setLoanId(loan.getId());
                emi.setDueDate(loanEmiDue.getEMIDate());
                emi.setDpd(loanEmiDue.getDPD().intValue());
                emi.setStatus("PAYMENT_DUE");
                emi.setDueAmount(loanEmiDue.getEMIAmount().longValue());
                emiRepository.save(emi);
            } else {
                Emi emi = emis.get(0);
                emi.setDueAmount(loanEmiDue.getEMIAmount().longValue());
                emi.setDpd(loanEmiDue.getDPD().intValue());
                emiRepository.save(emi);
            }
        }

    }
}

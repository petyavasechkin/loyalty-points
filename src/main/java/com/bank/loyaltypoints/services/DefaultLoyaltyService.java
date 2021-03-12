package com.bank.loyaltypoints.services;

import com.bank.loyaltypoints.entities.CustomerLoyalEntity;
import com.bank.loyaltypoints.entities.TransactionEntity;
import com.bank.loyaltypoints.exceptions.NoSuchCustomerException;
import com.bank.loyaltypoints.exceptions.NotEnoughAvailablePointsException;
import com.bank.loyaltypoints.exceptions.TransactionAlreadyExistsException;
import com.bank.loyaltypoints.mapper.TransactionEntityMapper;
import com.bank.loyaltypoints.model.CalcPointsContext;
import com.bank.loyaltypoints.model.LoyaltyPoints;
import com.bank.loyaltypoints.model.LoyaltyPointsHistory;
import com.bank.loyaltypoints.model.LoyaltyUsage;
import com.bank.loyaltypoints.model.Transaction;
import com.bank.loyaltypoints.repositories.CustomerLoyalRepository;
import com.bank.loyaltypoints.repositories.TransactionRepository;
import com.bank.loyaltypoints.utils.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.bank.loyaltypoints.utils.Utility.isCurrentWeekDate;

@Service
public class DefaultLoyaltyService implements LoyaltyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultLoyaltyService.class);
    private static final String PROCESS_TRANS_MSG = "Processing transaction for customer with Id:{}";
    private static final String DUPLICATE_TRANS_MSG = "Received duplicate transaction with Id: %s";
    private static final String CUSTOMER_NOT_EXISTS = "Customer not exits.";
    private static final String NOT_ENOUGH_POINTS = "Customer have no enough available points.";
    private static final String SPENT_POINTS_MSG = "Points was spent:{} for customer with Id:{}";

    @Value("${bound.first:5000}")
    private int firstBound;
    @Value("${bound.second:7500}")
    private int secondBound;
    @Value("${factor.first:1}")
    private int firstFactor;
    @Value("${factor.second:2}")
    private int secondFactor;
    @Value("${factor.third:3}")
    private int thirdFactor;
    @Value("${weekSpentNeed:500}")
    private int weekSpentNeed;

    private final CustomerLoyalRepository customerLoyalRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionEntityMapper transactionEntityMapper;

    public DefaultLoyaltyService(CustomerLoyalRepository customerLoyalRepository,
                                 TransactionRepository transactionRepository,
                                 TransactionEntityMapper transactionEntityMapper) {
        this.customerLoyalRepository = customerLoyalRepository;
        this.transactionRepository = transactionRepository;
        this.transactionEntityMapper = transactionEntityMapper;
    }

    @Override
    @Transactional
    public void processTransaction(Transaction transaction) {
        LOGGER.debug(PROCESS_TRANS_MSG, transaction.getCustomerId());
        checkForDuplication(transaction);
        TransactionEntity transactionEntity = transactionEntityMapper.covertTransactionDtoToEntity(transaction);
        transactionRepository.save(transactionEntity);

        int points = calculatePoints(transaction);

        Optional<CustomerLoyalEntity> customerLoyalEntityOptional = customerLoyalRepository.findById(transaction.getCustomerId());
        CustomerLoyalEntity customerLoyalEntityForSave;
        if (customerLoyalEntityOptional.isPresent()) {
            customerLoyalEntityForSave = customerLoyalEntityOptional.get();
            clearExpiredPoints(customerLoyalEntityForSave, LocalDateTime.now());
            if (isCurrentWeekDate(transaction.getDateTime().toLocalDate())) {
                customerLoyalEntityForSave.setPendingPoints(customerLoyalEntityForSave.getPendingPoints() + points);
                checkAndUpdatePoints(customerLoyalEntityForSave, transaction);
            }
        } else {
            customerLoyalEntityForSave = createNewCustomerLoyalty(transaction, points);
        }
        updateLastTransactionTime(customerLoyalEntityForSave, transaction);
        customerLoyalRepository.save(customerLoyalEntityForSave);
    }

    @Override
    @Transactional(readOnly = true)
    public LoyaltyPointsHistory getHistory(Long customerId) {
        CustomerLoyalEntity customerLoyalEntity =
                customerLoyalRepository.findById(customerId)
                        .orElseThrow(() -> new NoSuchCustomerException(CUSTOMER_NOT_EXISTS));
        return new LoyaltyPointsHistory(customerLoyalEntity.getAllAccruedPoints(),
                customerLoyalEntity.getAllSpentPoints());
    }

    @Override
    @Transactional
    public LoyaltyPoints getLoyalty(Long customerId) {
        CustomerLoyalEntity customerLoyalEntity =
                customerLoyalRepository.findById(customerId)
                        .orElseThrow(() -> new NoSuchCustomerException(CUSTOMER_NOT_EXISTS));
        clearExpiredPoints(customerLoyalEntity, LocalDateTime.now());
        return new LoyaltyPoints(customerLoyalEntity.getAvailablePoints(),
                customerLoyalEntity.getPendingPoints());
    }

    @Override
    @Transactional
    public LoyaltyUsage spendLoyalty(Long customerId, String amount) {
        CustomerLoyalEntity customerLoyalEntity =
                customerLoyalRepository.findById(customerId)
                        .orElseThrow(() -> new NoSuchCustomerException(CUSTOMER_NOT_EXISTS));
        clearExpiredPoints(customerLoyalEntity, LocalDateTime.now());
        int cents = Utility.getCentsFromAmountStr(amount);
        if (customerLoyalEntity.getAvailablePoints() < cents) {
            throw new NotEnoughAvailablePointsException(NOT_ENOUGH_POINTS);
        } else {
            LOGGER.debug(SPENT_POINTS_MSG, cents, customerId);
            customerLoyalEntity.setAvailablePoints(customerLoyalEntity.getAvailablePoints() - cents);
            customerLoyalEntity.setAllSpentPoints(customerLoyalEntity.getAllSpentPoints() + cents);
            return new LoyaltyUsage(Utility.getAmountStrFromCents(cents));
        }
    }

    private CustomerLoyalEntity createNewCustomerLoyalty(Transaction transaction, int points) {
        CustomerLoyalEntity customerLoyalEntity = new CustomerLoyalEntity();
        customerLoyalEntity.setCustomerId(transaction.getCustomerId());
        customerLoyalEntity.setLastUpdated(transaction.getDateTime());

        if (isCurrentWeekDate(transaction.getDateTime().toLocalDate())) {
            customerLoyalEntity.setPendingPoints(points);
        }
        return customerLoyalEntity;
    }

    private void clearExpiredPoints(CustomerLoyalEntity customerLoyalEntity, LocalDateTime currentDateTime) {
        if (currentDateTime.minusWeeks(5).isAfter(customerLoyalEntity.getLastUpdated())) {
            customerLoyalEntity.setAvailablePoints(0);
            customerLoyalEntity.setPendingPoints(0);
        }
    }

    private void checkAndUpdatePoints(CustomerLoyalEntity customerLoyalEntity, Transaction transaction) {
        LocalDate currentDate = LocalDate.now();
        if (!currentDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            LOGGER.debug("Today is not last day Of week. Skipping check and update points...");
            return;
        }
        LocalDateTime transactionDateTime = transaction.getDateTime();

        if (currentDate.isEqual(customerLoyalEntity.getLastAccruedDate())) {
            customerLoyalEntity.setAvailablePoints(customerLoyalEntity.getPendingPoints());
        } else if (currentDate.isEqual(transactionDateTime.toLocalDate())) {

            if (customerLoyalEntity.getPendingPoints() < weekSpentNeed) {
                LOGGER.debug("No enough money was spent for accruing. Skipping...");
                return;
            }

            LocalDateTime startWeekDateTime = currentDate.minusDays(6).atStartOfDay();
            List<TransactionEntity> transactionEntities =
                    transactionRepository.getAllByCustomerIdAndDateTimeAfter(transaction.getCustomerId(), startWeekDateTime);
            if (checkForEachDayOfWeekTransaction(transactionEntities)) {
                int currentPoints = customerLoyalEntity.getPendingPoints();
                LOGGER.debug("Accrued point {} for customer with Id:{}", currentPoints, customerLoyalEntity.getCustomerId());
                customerLoyalEntity.setAvailablePoints(customerLoyalEntity.getAvailablePoints() + customerLoyalEntity.getPendingPoints());
                customerLoyalEntity.setPendingPoints(0);
                customerLoyalEntity.setAllAccruedPoints(customerLoyalEntity.getAllAccruedPoints() + currentPoints);
                customerLoyalEntity.setLastAccruedDate(currentDate);
            }
        }
    }

    private boolean checkForEachDayOfWeekTransaction(List<TransactionEntity> transactionEntities) {
        Set<DayOfWeek> dayOfWeeksForCheck = new HashSet<>(Arrays.asList(DayOfWeek.values()));
        dayOfWeeksForCheck.remove(DayOfWeek.SUNDAY);
        return transactionEntities.stream()
                .map(TransactionEntity::getDateTime)
                .map(LocalDateTime::getDayOfWeek)
                .distinct()
                .anyMatch(day -> dayOfWeeksForCheck.remove(day) && dayOfWeeksForCheck.isEmpty());
    }

    private void updateLastTransactionTime(CustomerLoyalEntity customerLoyalEntityForSave, Transaction transaction) {
        if (customerLoyalEntityForSave.getLastUpdated() == null
                || transaction.getDateTime().isAfter(customerLoyalEntityForSave.getLastUpdated())) {
            customerLoyalEntityForSave.setLastUpdated(transaction.getDateTime());
        }
    }

    private void checkForDuplication(Transaction transaction) {
        if (transactionRepository.existsById(transaction.getId())) {
            String duplicateTransMsg = String.format(DUPLICATE_TRANS_MSG, transaction.getId());
            LOGGER.warn(duplicateTransMsg);
            throw new TransactionAlreadyExistsException(duplicateTransMsg);
        }
    }

    private int calculatePoints(Transaction transaction) {
        CalcPointsContext ctx = new CalcPointsContext(transaction, firstBound, secondBound,
                firstFactor, secondFactor, thirdFactor);
        return Utility.calculatePoints(ctx);
    }
}

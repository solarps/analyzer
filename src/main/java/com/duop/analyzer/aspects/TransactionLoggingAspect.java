package com.duop.analyzer.aspects;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class TransactionLoggingAspect {

    private final Logger logger = LoggerFactory.getLogger("Transaction logger");

    @Pointcut("@annotation(org.springframework.transaction.annotation.Transactional) " +
            "&& within(com.duop.analyzer.sheets.SheetResultProcessor)")
    public void saveSheetResultTransaction() {
    }

    @AfterThrowing(pointcut = "saveSheetResultTransaction()", throwing = "ex")
    public void rollBackAfterException(Exception ex) {
        logger.error("Transaction error: {}", ex.getMessage());
    }

}

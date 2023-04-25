package com.duop.analyzer.aspects;

import com.google.api.services.drive.model.File;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
public class ReaderAspect {
    private final Logger logger = LoggerFactory.getLogger("AnalyzerLogger");

    @Around("@annotation(com.duop.analyzer.aspects.LogReadingTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        final StopWatch stopWatch = new StopWatch();

        stopWatch.start();

        Object proceed = joinPoint.proceed();

        stopWatch.stop();
        File file = (File) joinPoint.getArgs()[0];
        logger.info("file \"{}\" read in {} ms", file.getId(), stopWatch.getTotalTimeMillis());

        return proceed;
    }
}
